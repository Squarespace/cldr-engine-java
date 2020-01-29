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
import com.squarespace.cldrengine.calendars.SkeletonData.Field;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.utils.Cache;

class CalendarManager {

  private final Bundle bundle;
  private final Internals internals;
  private final Set<String> availableCalendars;
  private final Cache<CalendarPatterns> patternCache;

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

    // TODO:
    FormatWidthType dateKey = options.datetime.or(options.date.get());
    FormatWidthType timeKey = options.datetime.or(options.time.get());
    FormatWidthType wrapKey = options.wrap.get();
    String skelKey = options.skeleton.or("");

    if (dateKey == null && timeKey == null && isEmpty(skelKey)) {
      dateKey = FormatWidthType.LONG;
    }

    String wrapper = "";
    if (wrapKey != null) {
      wrapper = patterns.getWrapperPattern(wrapKey);
    } else if (dateKey != null && timeKey != null) {
      wrapper = patterns.getWrapperPattern(dateKey);
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

    // Standard format
    if (req.date != null || req.time != null) {
      return req;
    }

    // Perform a best-fit match on the skeleton

    // Check if we've cached the patterns for this skeleton before
    CachedSkeletonRequest entry = patterns.getCachedSkeletonRequest(skelKey);
    if (entry != null) {
      req.date = entry.date;
      req.time = entry.time;
      if (wrapKey != null && entry.dateSkel != null && req.date != null && req.time != null) {
        // If wrapper not explicitly requested, select based on skeleton date fields
        req.wrapper = this.selectWrapper(patterns, entry.dateSkel, req.date);
      }
      return req;
    }

    DateSkeleton timeQuery = null;
    DateSkeleton dateSkel = null;
    DateSkeleton timeSkel = null;

    // Check if skeleton specifies date or time fields, or both.
    DateSkeleton query = patterns.parseSkeleton(skelKey);
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

    req.date = dateSkel != null ?
        this.getAvailablePattern(patterns, date, query, dateSkel, params) : null;
    req.time = timeQuery != null && timeSkel != null ?
        this.getAvailablePattern(patterns, date, timeQuery, timeSkel, params) : null;

    if (wrapKey == null && dateSkel != null && req.date != null && req.time != null) {
      req.wrapper = this.selectWrapper(patterns, dateSkel, req.date);
    }

    entry = new CachedSkeletonRequest(dateSkel, req.date, req.time);
    patterns.setCachedSkeletonRequest(skelKey, entry);
    return req;
  }

  /**
   * Best-fit match an input skeleton. The skeleton can contain both date and
   * time fields.
   *
   * The field of greatest difference between the start and end dates can be
   * either a date or time field.
   *
   * Given this we need to cover the following cases:
   *
   * 1. Input skeleton requests both date and time fields.
   *  a. "yMd" same: split skeleton, format date standalone, followed by time range.
   *  b. "yMd" differ: format full start / end with fallback format.
   *
   * 2. Input skeleton requests date fields only:
   *  a. "yMd" same: format date standalone
   *  b. "yMd" differ: select and format date range
   *
   * 3. Input skeleton requests time fields only:
   *  a. "yMd" same, "ahms" same: format time standalone
   *  b. "yMd" same, "ahms" differ: select and format time range.
   *  c. "yMd" differ: prepend "yMd" to skeleton and go to (1a).
   */
  public DateIntervalFormatRequest getDateIntervalFormatRequest(CalendarType calendar,
      CalendarDate start, DateTimePatternFieldType fieldDiff, DateIntervalFormatOptions options,
      NumberParams params) {

    CalendarPatterns patterns = this.getCalendarPatterns(calendar.value);
    boolean dateDiffers = "yMd".indexOf(fieldDiff.value()) != -1;
    String wrapper = patterns.getIntervalFallback();
    DateIntervalFormatRequest req = new DateIntervalFormatRequest();
    req.params = params;
    req.wrapper = wrapper;

    String origSkeleton = options.skeleton.get();
    if (origSkeleton == null) {
      if (dateDiffers && options.date.ok()) {
        origSkeleton = options.date.get();
      } else {
        origSkeleton = options.time.get();
      }
    }

    // If the skeleton is still undefined, select a reasonable default
    if (origSkeleton == null) {
      origSkeleton = dateDiffers ? "yMMMd" : "hmsa";
    }
    String skeleton = origSkeleton;

    // Cache key consists of the input skeleton and the field of greatest difference between
    // the start and end dates.
    String cacheKey = skeleton + "\t" + fieldDiff;
    CachedIntervalRequest entry = patterns.getCachedIntervalRequest(cacheKey);
    if (entry != null) {
      req.date = entry.date;
      req.range = entry.range;
      req.skeleton = entry.skeleton;
      return req;
    }

    entry = new CachedIntervalRequest(null, null, null);
    DateSkeleton query = patterns.parseSkeleton(skeleton);

    // TODO: Augment skeleton to ensure context. day without month, minute without hour, etc.

    boolean standalone = fieldDiff == DateTimePatternFieldType.SECOND ||
        (query.isDate && !dateDiffers) || (query.isTime && dateDiffers);
    if (!standalone) {
      if (query.has(SkeletonData.Field.DAY.ordinal()) && !query.has(SkeletonData.Field.MONTH.ordinal())) {
        skeleton = "M" + skeleton;
      }
      if (query.has(SkeletonData.Field.MINUTE.ordinal()) && !query.has(SkeletonData.Field.HOUR.ordinal())) {
        skeleton = "j" + skeleton;
      }
    }

    if (!query.isDate && dateDiffers) {
      // 3c. prepend "yMd" and proceed
      if (fieldDiff == DateTimePatternFieldType.YEAR) {
        skeleton = "yMd" + skeleton;
      } else if (fieldDiff == DateTimePatternFieldType.MONTH) {
        skeleton = "Md" + skeleton;
      } else {
        skeleton = "d" + skeleton;
      }
    }

    if (!origSkeleton.equals(skeleton)) {
      query = patterns.parseSkeleton(skeleton);
    }

    DateSkeleton timeQuery = null;

    // If both date and time fields are requested, we have two choices:
    // a. date fields are the same:  "<date>, <time start> - <time end>"
    // b. date fields differ, format full range: "<start> - <end>"
    if (query.compound()) {
      if (dateDiffers) {
        // 1b. format start and end dates with fallback: "<start> - <end>"
        req.skeleton = skeleton;
        entry.skeleton = skeleton;
        patterns.setCachedIntervalRequest(cacheKey, entry);
        return req;
      }

      // 1a. split skeleton, format date standalone ..
      timeQuery = query.split();
      entry.date = this.matchAvailablePattern(patterns, start, query, params);

      // ... followed by time range: "<date>, <time start> - <time end>"
      query = timeQuery;
    }

    // standalone: in certain cases we cannot display a range
    standalone = fieldDiff == DateTimePatternFieldType.SECOND ||
        (query.isDate && !dateDiffers) || (query.isTime && dateDiffers);
    if (standalone) {
      // 2a. format date standalone: "<date>"
      // 3a. format time standalone: "<time>"
      entry.date = this.matchAvailablePattern(patterns, start, query, params);
    } else {
      // 2b. format date interval: "<date start> - <date end>"
      // 3b. format time interval: "<time start> - <time end>"
      DateSkeleton match = patterns.matchInterval(query, fieldDiff);
      DateTimePattern pattern = patterns.getIntervalPatern(fieldDiff, match.skeleton);
      String decimal = params.symbols.get(NumberSymbolType.DECIMAL);
      entry.range = pattern.nodes.size() == 0 ? null : patterns.adjustPattern(pattern, query, decimal);
    }

    patterns.setCachedIntervalRequest(cacheKey, entry);
    req.date = entry.date;
    req.range = entry.range;
    return req;
  }

  protected DateTimePattern matchAvailablePattern(CalendarPatterns patterns, CalendarDate date,
      DateSkeleton query, NumberParams params) {
    DateSkeleton match = patterns.matchAvailable(query);
    return this.getAvailablePattern(patterns, date, query, match, params);
  }

  protected DateTimePattern getAvailablePattern(CalendarPatterns patterns,
      CalendarDate date, DateSkeleton query, DateSkeleton match, NumberParams params) {
    DateTimePattern pattern = patterns.getAvailablePattern(date, match);
    return pattern.nodes.size() == 0 ?
        null : patterns.adjustPattern(pattern, query, params.symbols.get(NumberSymbolType.DECIMAL));
  }

  /**
   * Select appropriate wrapper based on fields in the date skeleton.
   */
  protected String selectWrapper(CalendarPatterns patterns, DateSkeleton dateSkel, DateTimePattern date) {
    FormatWidthType wrapKey = FormatWidthType.SHORT;
    int monthWidth = dateSkel.monthWidth();
    boolean hasWeekday = dateSkel.has(Field.WEEKDAY.ordinal());
    if (monthWidth == 4) {
      wrapKey = hasWeekday ? FormatWidthType.FULL : FormatWidthType.LONG;
    } else if (monthWidth == 3) {
      wrapKey = FormatWidthType.MEDIUM;
    }
    return patterns.getWrapperPattern(wrapKey);
  }
}
