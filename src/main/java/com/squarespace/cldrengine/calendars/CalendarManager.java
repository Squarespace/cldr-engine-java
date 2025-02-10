package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.util.HashSet;
import java.util.Set;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.CalendarType;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.DateIntervalFormatOptions;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.NumberSymbolType;
import com.squarespace.cldrengine.calendars.DateSkeleton.SkeletonField;
import com.squarespace.cldrengine.calendars.SkeletonData.Field;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.utils.Cache;

/**
 * Manages calendar-specific patterns and construction of formatting requests.
 */
class CalendarManager {

  private final Bundle bundle;
  private final Internals internals;
  private final Set<String> availableCalendars;
  private final Cache<CalendarPatterns> patternCache;

  private static final Field[] MASKED_FOVD_FIELDS = new Field[] {
      Field.ERA,
      Field.YEAR,
      Field.MONTH,
      Field.DAY,
      Field.DAYPERIOD,
      Field.HOUR,
      Field.MINUTE,
      Field.SECOND
  };

  public CalendarManager(Bundle bundle, Internals internals) {
    this.bundle = bundle;
    this.internals = internals;
    Schema schema = internals.schema;
    this.availableCalendars = new HashSet<>(internals.config.get("calendars"));
    this.patternCache = new Cache<>(calendar -> {
      if (this.availableCalendars.contains(calendar)) {
        switch (calendar) {
          case "buddhist":
            return new CalendarPatterns(bundle, internals, schema.Buddhist);
          case "japanese":
            return new CalendarPatterns(bundle, internals, schema.Japanese);
          case "persian":
            return new CalendarPatterns(bundle, internals, schema.Persian);
        }
      }
      return new GregorianPatterns(bundle, internals, schema.Gregorian);
    }, 20);
  }

  public CalendarPatterns getCalendarPatterns(String calendar) {
    return this.patternCache.get(calendar);
  }

  public DateFormatRequest getDateFormatRequest(CalendarDate date, DateFormatOptions options, NumberParams params) {
    CalendarType calendar = this.internals.calendars.selectCalendar(this.bundle, options.calendar.get());
    CalendarPatterns patterns = this.getCalendarPatterns(calendar.value);

    FormatWidthType dateKey = options.datetime.or(options.date.get());
    FormatWidthType timeKey = options.datetime.or(options.time.get());
    FormatWidthType wrapKey = options.wrap.get();
    String skelKey = options.skeleton.or("");

    if (dateKey == null && timeKey == null && isEmpty(skelKey)) {
      dateKey = FormatWidthType.LONG;
    }

    boolean atTime = options.atTime.or(true);
    String wrapper = "";
    if (wrapKey != null) {
      wrapper = patterns.getWrapperPattern(wrapKey, atTime);
    } else if (dateKey != null && timeKey != null) {
      wrapper = patterns.getWrapperPattern(dateKey, atTime);
    }

    DateFormatRequest req = new DateFormatRequest();
    req.wrapper = wrapper;
    req.params = params;
    if (dateKey != null) {
      req.date = patterns.getDatePattern(dateKey);
    }
    if (timeKey != null) {
      req.time = patterns.getTimePattern(timeKey);
    }

    DateSkeleton query = null;

    // We have both standard formats, we're done
    if (req.date != null && req.time != null) {
      return req;
    }

    // We have at least a date/time standard format.
    if (req.date != null || req.time != null) {

      // If no skeleton specified, we're done
      if (isEmpty(skelKey)) {
        return req;
      }

      // We have a standard date or time pattern along with a skeleton.
      // We split the skeleton into date/time parts, then use the one
      // that doesn't conflict with the specified standard format
      query = patterns.parseSkeleton(skelKey);

      // Use the part of the skeleton that does not conflict
      DateSkeleton time = query.split();
      if (req.date != null) {
        query = time;
      }

      // Update skeleton key with only the used fields
      skelKey = query.canonical();
    } else {
      // No standard format specified, so just parse the skeleton
      query = patterns.parseSkeleton(skelKey);
    }

    // Perform a best-fit match on the skeleton

    // TODO: skeleton caching disabled for now due to mixed formats
    // Check if we've cached the patterns for this skeleton before
    // CachedSkeletonRequest entry = patterns.getCachedSkeletonRequest(skelKey);
    // if (entry != null) {
    // req.date = entry.date;
    // req.time = entry.time;
    // if (wrapKey == null && entry.dateSkel != null && req.date != null && req.time != null) {
    // // If wrapper not explicitly requested, select based on skeleton date fields
    // req.wrapper = this.selectWrapper(patterns, entry.dateSkel, req.date);
    // }
    // return req;
    // }

    DateSkeleton timeQuery = null;
    DateSkeleton dateSkel = null;
    DateSkeleton timeSkel = null;

    // Check if skeleton specifies date or time fields, or both.
    if (query.compound()) {
      // Separate into a date and a time skeleton.
      timeQuery = query.split();
      dateSkel = patterns.matchAvailable(query);
      timeSkel = patterns.matchAvailable(timeQuery);
    } else if (query.isDate) {
      dateSkel = patterns.matchAvailable(query);
    } else {
      timeQuery = query;
      timeSkel = patterns.matchAvailable(query);
    }

    if (dateSkel != null) {
      req.date = this.getAvailablePattern(patterns, date, query, dateSkel, params);
    }
    if (timeQuery != null && timeSkel != null) {
      req.time = this.getAvailablePattern(patterns, date, timeQuery, timeSkel, params);
    }

    if (wrapKey == null) {
      if (dateSkel != null && req.date != null && req.time != null) {
        req.wrapper = this.selectWrapper(patterns, dateSkel, req.date, atTime);
      } else {
        req.wrapper = patterns.getWrapperPattern(dateKey == null ? FormatWidthType.SHORT : dateKey, atTime);
      }
    }

    // TODO: skeleton caching disabled for now due to mixed formats
    // entry = new CachedSkeletonRequest(dateSkel, req.date, req.time);
    // patterns.setCachedSkeletonRequest(skelKey, entry);
    return req;
  }

  /**
   * Formats datetime intervals.
   *
   * NOTE: The ICU implementation of CLDR datetime range formatting
   * contains inconsistent behavior (see code at end of comment below).
   *
   * The skeleton data for interval formatting consists of a series of
   * separate date and time patterns. No interval skeletons / formats
   * contain both date and time fields.
   *
   * For example, the data might contain the following:
   *
   *  ['y', 'yM', 'yMd', 'h', 'hm', 'H', 'Hm']
   *
   * However a caller can pass input skeleton containing a mix of date and
   * time fields. We split this skeleton into separate date and time
   * skeletons, and perform formatting by considering the possibilities below.
   *
   * Variables:
   *
   *   start         = start datetime
   *   end           = end datetime
   *   skeleton      = input skeleton
   *   fovd          = fieldOfVisualDifference(start, end)
   *   date_differs  = fovd in ('era', 'year', 'month', 'day')
   *   time_differs  = fovd in ('dayperiod', 'hour', 'minute')
   *   equal         = fovd in ('second', undefined)
   *
   * Formatting Rules:
   *
   * 1. Skeleton requests both date and time fields
   *
   *   a. IF time_differs, format date followed by time range
   *      e.g. "date, time0 - time 1"
   *
   *   b. ELSE IF date_differs, format generic fallback
   *      e.g. "date0, time0 - date1, time1"
   *
   *   c. ELSE format the date + time standalone
   *      e.g. "date0, time0"
   *
   * 2: Skeleton only requests date fields
   *
   *   a. IF date_differs, format date range
   *      e.g. "date0 - date1"
   *
   *   b. ELSE format date standalone
   *
   * 3: Skeleton only requests time fields
   *
   *   a. IF time_differs, format time range
   *      e.g. "time0 - time1"
   *
   *   b. ELSE format time standalone
   *
   * ========================================================
   *
   *
   * Example of inconsistency of ICU range formatting.
   * The output was produced using Node 23.7.0 and ICU 74.
   * ICU4J version 75 produces the same output.
   *
   *     const OPTS = [
   *       { day: 'numeric' },
   *       { day: 'numeric', minute: '2-digit' }
   *     ];
   *     const start = new Date(Date.UTC(2007, 0, 1, 10, 12, 0));
   *     const end = new Date(Date.UTC(2008, 0, 2, 11, 13, 0));
   *     for (let i = 0; i < OPTS.length; i++) {
   *       const opts = OPTS[i];
   *       const fmt = new Intl.DateTimeFormat('en', opts);
   *       console.log(fmt.formatRange(start, end));
   *     }
   *
   * This code formats two dates two different ways:
   *
   *   2007-Jan-01 10:12
   *   2008-Jan-02 11:13
   *
   * 1. Display the day: { day: 'numeric' }
   *
   *  ICU auto-expands the selected pattern to include the year
   *  and month:
   *
   *    "1/1/2007 – 1/2/2008"
   *
   *  This adds context needed to understand the two
   *  dates are separated by 367 days.
   *
   * 2. Display day and minute: { day: 'numeric', minute: '2-digit' }
   *
   *  No pattern expansion occurs, we get only what we
   *  requested. The output is highly ambiguous since it's
   *  missing year, month, and hour fields:
   *
   *    "1, 12 – 2, 13"
   * Additional context is added in one case but not in the other.
   *
   * IMO it makes more sense to leave the input skeleton as untouched
   * as possible, leaving it up to the caller to decide which
   * fields to request.
   */
  public DateIntervalFormatRequest getDateIntervalFormatRequest(CalendarType calendar,
      CalendarDate start, CalendarDate end, DateIntervalFormatOptions options,
      NumberParams params) {

    CalendarPatterns patterns = this.getCalendarPatterns(calendar.value());
    String wrapper = patterns.getIntervalFallback();
    DateIntervalFormatRequest req = new DateIntervalFormatRequest();
    req.params = params;
    req.wrapper = wrapper;

    // Determine whether the largest field of visual difference (fovd)
    // is a date or time field, or neither. Note that interval patterns
    // in the CLDR data do not include seconds, so we consider dates
    // that only differ in seconds to be equivalent.
    DateTimePatternFieldType fovd = start.fieldOfVisualDifference(end);
    if (fovd == null) {
      fovd = DateTimePatternFieldType.SECOND;
    }
    boolean dateDiffers = "GyMd".indexOf(fovd.value()) != -1;
    boolean timeDiffers = "BahHm".indexOf(fovd.value()) != -1;

    // If main skeleton input is not used, select either date or
    // time based on whether the date or time differ.
    String skeleton = options.skeleton.get();
    if (skeleton == null) {
      if (dateDiffers && options.date.ok()) {
        skeleton = options.date.get();
      } else {
        skeleton = options.time.get();
      }
    }

    // If no skeleton is defined, choose a simple default
    boolean defaulted = false;
    if (skeleton == null) {
      skeleton = dateDiffers ? "yMMMd" : "jm";
      defaulted = true;
    }

    // At this point the skeleton contains at least one field.

    // Parse the input skeleton.
    DateSkeleton query = patterns.parseSkeleton(skeleton);

    if (!defaulted) {
      // Interval skeletons for bare seconds 's' and minutes 'm' do not
      // exist in the CLDR data. We fill in the gap to ensure we at least
      // match on the correct hour field for the current locale.
      DateTimePatternFieldType largest = this.largestSkeletonField(query);
      if ("sm".indexOf(largest.value()) != -1) {
        skeleton = (largest == DateTimePatternFieldType.SECOND ? "jm" : "j") + skeleton;
        query = patterns.parseSkeleton(skeleton);
      }
    }

    System.out.println("query: " + query.canonical() + " fovd: " + fovd);
    
    // BEGIN formatting rules.

    // RULE 1. Skeleton contains both date and time fields
    if (query.compound()) {
      if (timeDiffers) {
        // RULE 1a. IF time_differs, format date followed by time range
        DateSkeleton timeQuery = query.split();
        req.date = this.matchAvailablePattern(patterns, start, query, params);
        query = timeQuery;
        // ... (1a) intentional fall through to format date + time range
      } else if (dateDiffers) {
        // RULE 1b. ELSE IF date_differs, format generic fallback
        req.skeleton = skeleton;
        return req;
      } else {
        // RULE 1c. ELSE format the date + time standalone
        DateSkeleton timeQuery = query.split();
        if (query.isDate) {
          req.date = this.matchAvailablePattern(patterns, start, query, params);
        }
        if (timeQuery.isTime) {
          req.time = this.matchAvailablePattern(patterns, start, timeQuery, params);
        }
        return req;
      }
    }

    // RULE 2: skeleton only contains date fields
    // RULE 3: skeleotn only contains time fields
    if (!fovd.equals(DateTimePatternFieldType.SECOND)) {
      // RULE 2a IF dateDiffers, format date range
      // RULE 3a IF timeDiffers, format time range
      DatePatternMatcherEntry<IntervalSkeleton> match = patterns.matchInterval(query);
      if (match != null && match.data != null) {
        IntervalSkeleton data = match.data.get();

        // Compute masked field of visual difference using the found skeleton.
        fovd = this.maskedFOVD(start, end, match.skeleton);

        // Use fovd to select final pattern. Since it was masked by the matched
        // skeleton, the fovd should completely cover the set of patterns in the data.
        String pattern = data.patterns.get(fovd);
        if (pattern == null) {
          pattern = "";
        }

        DateTimePattern parsedPattern = this.internals.calendars.parseDatePattern(pattern);
        /* istanbul ignore else */
        if (!parsedPattern.nodes.isEmpty()) {
          req.range = patterns.adjustPattern(parsedPattern, query, params.symbols.get(NumberSymbolType.DECIMAL));
        }
      }
    } else {
      // RULE 2b ELSE format date standalone
      // RULE 3b ELSE format time standalone
      req.date = this.matchAvailablePattern(patterns, start, query, params);
    }
    
    return req;
  }

  private DateTimePatternFieldType maskedFOVD(CalendarDate start, CalendarDate end, DateSkeleton skeleton) {
    SkeletonField smallest = null;
    for (Field field : MASKED_FOVD_FIELDS) {
      SkeletonField info = skeleton.info[field.ordinal()];
      if (info == null) {
        continue;
      }

      smallest = info;
      switch (field) {
        case ERA:
          if (start.era() != end.era()) {
            return DateTimePatternFieldType.ERA;
          }
          break;

        case YEAR:
          if (start.year() != end.year()) {
            return DateTimePatternFieldType.YEAR;
          }
          break;

        case MONTH:
          if (start.month() != end.month()) {
            return DateTimePatternFieldType.MONTH;
          }
          break;

        case DAY:
          if (start.dayOfMonth() != end.dayOfMonth()) {
            return DateTimePatternFieldType.DAY;
          }
          break;

        case DAYPERIOD:
          switch (info.field) {
            case 'a': // dayperiod
            case 'b': // dayperiod extended (resolve to 'a' for now)
              if (start.isAM() != end.isAM()) {
                return DateTimePatternFieldType.DAYPERIOD;
              }
              break;

            case 'B': // dayperiod flex
              if (this.dayperiodFlex(start) != this.dayperiodFlex(end)) {
                return DateTimePatternFieldType.DAYPERIOD_FLEX;
              }
              break;
          }
          break;

        case HOUR:
          switch (info.field) {
            case 'h': // hour 1-12
            case 'K': // hour 0-11
              if (start.hour() != end.hour()) {
                return DateTimePatternFieldType.HOUR12;
              }
              break;

            case 'H': // hour 0-23
            case 'k': // hour 1-24
              if (start.hourOfDay() != end.hourOfDay()) {
                return DateTimePatternFieldType.HOUR24;
              }
              break;
          }
          break;

        case MINUTE:
        case SECOND:
          if (start.minute() != end.minute()) {
            return DateTimePatternFieldType.MINUTE;
          }
          break;
          
        default:
          break;
      }
    }

    // If we exhaust all fields of the skeleton, the two dates are equivalent
    // with respect to the fields masked by the skeleton. We return the smallest
    // field by default.
    return smallest != null ? (DateTimePatternFieldType.fromString(Character.toString(smallest.field)))
        : DateTimePatternFieldType.MINUTE;
  }

  private String dayperiodFlex(CalendarDate date) {
    String result = this.internals.calendars.flexDayPeriod(this.bundle, minutes(date));
    return result == null ? "" : result;
  }

  private DateTimePatternFieldType largestSkeletonField(DateSkeleton skeleton) {
    for (Field field : MASKED_FOVD_FIELDS) {
      SkeletonField info = skeleton.info[field.ordinal()];
      if (info != null) {
        return DateTimePatternFieldType.fromString(Character.toString(info.field));
      }
    }
    return DateTimePatternFieldType.SECOND;
  }

  protected DateTimePattern matchAvailablePattern(CalendarPatterns patterns, CalendarDate date,
      DateSkeleton query, NumberParams params) {
    DateSkeleton match = patterns.matchAvailable(query);
    return this.getAvailablePattern(patterns, date, query, match, params);
  }

  protected DateTimePattern getAvailablePattern(CalendarPatterns patterns,
      CalendarDate date, DateSkeleton query, DateSkeleton match, NumberParams params) {
    DateTimePattern pattern = patterns.getAvailablePattern(date, match);
    return pattern.nodes.size() == 0 ? null
        : patterns.adjustPattern(pattern, query, params.symbols.get(NumberSymbolType.DECIMAL));
  }

  /**
   * Select appropriate wrapper based on fields in the date skeleton.
   */
  protected String selectWrapper(CalendarPatterns patterns, DateSkeleton dateSkel, DateTimePattern date,
      boolean atTime) {
    FormatWidthType wrapKey = FormatWidthType.SHORT;
    int monthWidth = dateSkel.monthWidth();
    boolean hasWeekday = dateSkel.has(Field.WEEKDAY.ordinal());
    if (monthWidth == 4) {
      wrapKey = hasWeekday ? FormatWidthType.FULL : FormatWidthType.LONG;
    } else if (monthWidth == 3) {
      wrapKey = FormatWidthType.MEDIUM;
    }
    return patterns.getWrapperPattern(wrapKey, atTime);
  }

  protected static long minutes(CalendarDate d) {
    return d.hourOfDay() * 60 + d.minute();
  }
}
