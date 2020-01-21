package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.util.HashSet;
import java.util.Set;

import com.squarespace.cldrengine.calendars.SkeletonData.Field;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.FormatWidthType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.NumberSymbolType;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.numbering.NumberParams;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.utils.Cache;
import com.squarespace.cldrengine.utils.StringUtils;

public class CalendarManager {

  private final Bundle bundle;
  private final Internals internals;
  private final Set<String> availableCalendars;
  private final Cache<CalendarPatterns> patternCache;

  public CalendarManager(Bundle bundle, Internals internals) {
    this.bundle = bundle;
    this.internals = internals;
    Schema schema = internals.schema;
    this.availableCalendars = new HashSet<>();
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
    CalendarType calendar = this.internals.calendars.selectCalendar(this.bundle, options.calendar);
    CalendarPatterns patterns = this.getCalendarPatterns(calendar.value);

    // TODO:
    FormatWidthType dateKey = this.supportedOption(options.datetime, options.date);
    FormatWidthType timeKey = this.supportedOption(options.datetime, options.time);
    FormatWidthType wrapKey = this.supportedOption(options.wrap);
    String skelKey = options.skeleton;
    if (skelKey == null) {
      skelKey = "";
    }

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

  protected FormatWidthType supportedOption(FormatWidthType ...keys) {
    for (FormatWidthType key : keys) {
      if (key == null) {
        continue;
      }
      switch (key) {
        case FULL:
        case LONG:
        case MEDIUM:
        case SHORT:
          return key;
       default:
         break;
      }
    }
    return null;
  }
}
