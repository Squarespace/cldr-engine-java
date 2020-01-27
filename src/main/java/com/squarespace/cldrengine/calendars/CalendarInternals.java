package com.squarespace.cldrengine.calendars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.CalendarType;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.CalendarExternalData;
import com.squarespace.cldrengine.internal.CalendarSchema;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.parsing.WrapperPattern;
import com.squarespace.cldrengine.utils.Cache;
import com.squarespace.cldrengine.utils.JsonUtils;

public class CalendarInternals {

  private static final Map<String, List<String>> CALENDAR_PREFS = new HashMap<>();
  private static final Map<String, Integer> WEEK_FIRST_DAY = new HashMap<>();
  private static final Map<String, Integer> WEEK_MIN_DAYS = new HashMap<>();

  static {
    String[] calendarIds = JsonUtils.decodeArray(JsonParser.parseString(CalendarExternalData.CALENDARIDS));
    JsonObject root = JsonParser.parseString(CalendarExternalData.CALENDARPREFDATA).getAsJsonObject();
    for (String region : root.keySet()) {
      List<String> prefs = new ArrayList<>();
      JsonArray ids = root.getAsJsonArray(region);
      for (int i = 0; i < ids.size(); i++) {
        int id = ids.get(i).getAsInt();
        String pref = calendarIds[id];
        prefs.add(pref);
      }
      CALENDAR_PREFS.put(region, prefs);
    }

    root = JsonParser.parseString(CalendarExternalData.WEEKFIRSTDAY).getAsJsonObject();
    for (String region : root.keySet()) {
      int firstDay = root.get(region).getAsInt();
      WEEK_FIRST_DAY.put(region, firstDay);
    }

    root = JsonParser.parseString(CalendarExternalData.WEEKMINDAYS).getAsJsonObject();
    for (String region : root.keySet()) {
      int minDays = root.get(region).getAsInt();
      WEEK_MIN_DAYS.put(region, minDays);
    }
  }

  public final Internals internals;
  public final Schema schema;

  public final Cache<CalendarFormatter<CalendarDate>> calendarFormatterCache;
  public final DayPeriodRules dayPeriodRules;
  public final Cache<DateTimePattern> patternCache;
  public final Cache<DateTimePattern[]> hourPatternCache;
  public final Set<String> availableCalendars;

  public CalendarInternals(Internals internals) {
    this.internals = internals;
    this.schema = internals.schema;
    this.dayPeriodRules = new DayPeriodRules();
    this.patternCache = new Cache<>(DateTimePattern::parse, 1024);
    this.hourPatternCache = new Cache<>(s -> {
      String[] parts = s.split(";");
      DateTimePattern positive = patternCache.get(parts[0]);
      DateTimePattern negative = patternCache.get(parts[1]);
      return new DateTimePattern[] { positive, negative };
    }, 50);
    this.calendarFormatterCache = new Cache<>(this::buildFormatter, 1024);
    this.availableCalendars = new HashSet<>(internals.config.get("calendars"));
  }

  public String flexDayPeriod(Bundle bundle, long minutes) {
    return this.dayPeriodRules.get(bundle, minutes);
  }

  public CalendarFormatter<CalendarDate> getCalendarFormatter(CalendarType type) {
    return this.calendarFormatterCache.get(type.value);
  }

  public DateTimePattern parseDatePatterh(String raw) {
    return this.patternCache.get(raw);
  }

  public DateTimePattern getHourPattern(String raw, boolean negative) {
     return this.hourPatternCache.get(raw)[negative ? 1 : 0];
  }

  public int weekFirstDay(String region) {
    Integer w = WEEK_FIRST_DAY.get(region);
    return w == null ? WEEK_FIRST_DAY.get("001") : w;
  }

  public int weekMinDays(String region) {
    Integer w = WEEK_MIN_DAYS.get(region);
    return w == null ? WEEK_MIN_DAYS.get("001") : w;
  }

  public <R> R formatDateTime(CalendarType calendar, CalendarContext<CalendarDate> ctx,
      AbstractValue<R> value, DateTimePattern date, DateTimePattern time, String wrapper) {

    CalendarFormatter<CalendarDate> formatter = this.getCalendarFormatter(calendar);
    R _date = null;
    R _time = null;
    if (date != null) {
      formatter.format(value, ctx, date);
      _date = value.render();
    }
    if (time != null) {
      formatter.format(value, ctx, time);
      _time = value.render();
    }
    if (_date != null && _time != null && wrapper != null) {
      WrapperPattern pattern = this.internals.general.wrapperPatternCache.get(wrapper);
      // TODO: warning
      value.wrap(pattern, Arrays.asList(_time, _date));
      return value.render();
    }
    return _date != null ? _date : _time != null ? _time : value.empty();
  }

  public <R> R formatInterval(CalendarType calendar, CalendarContext<CalendarDate> ctx,
      AbstractValue<R> value, CalendarDate end, DateTimePattern pattern) {

    int i = DateTimePattern.intervalPatternBoundary(pattern);
    List<Object> nodes = pattern.nodes;
    DateTimePattern startPattern = new DateTimePattern(nodes.subList(0, i), "");
    DateTimePattern endPattern = new DateTimePattern(nodes.subList(i, nodes.size()), "");
    R s = this.formatDateTime(calendar, ctx, value, startPattern, null, null);
    ctx.date = end;
    R e = this.formatDateTime(calendar, ctx, value, endPattern, null, null);
    return value.join(Arrays.asList(s, e));
  }

  public CalendarType selectCalendar(Bundle bundle, CalendarType type) {
    return selectCalendar(bundle, type == null ? "gregory" : type.value);
  }

  public CalendarType selectCalendar(Bundle bundle, String calendar) {
    CalendarType cal = this.supportedCalendar(calendar);
    if (cal == null) {
      cal = this.supportedCalendar(bundle.calendarSystem());
    }
    if (cal == null) {
      List<String> prefs = CALENDAR_PREFS.get(bundle.region());
      if (prefs == null) {
        prefs = CALENDAR_PREFS.get("001");
      }
      if (prefs != null) {
        for (String id : prefs) {
          cal = this.supportedCalendar(id);
          if (cal != null) {
            return cal;
          }
        }
      }
      return CalendarType.GREGORY;
    }
    return cal;
  }

  protected CalendarType supportedCalendar(String c) {
    if (c != null && this.availableCalendars.contains(c)) {
      switch (c) {
        case "buddhist":
        case "iso8601":
        case "japanese":
        case "persian":
        case "gregory":
          return CalendarType.fromString(c);
        case "gregorian":
          return CalendarType.GREGORY;
        default:
          break;
      }
    }
    return null;
  }

  private CalendarFormatter<CalendarDate> buildFormatter(String calendar) {
    CalendarSchema s = this.schema.Gregorian;
    if (availableCalendars.contains(calendar)) {
      switch (calendar) {
        case "buddhist":
          s = this.schema.Buddhist;
          break;
        case "japanese":
          s = this.schema.Japanese;
          break;
        case "persian":
          s = this.schema.Persian;
          break;
        default:
          break;
      }
    }
    return new CalendarFormatter<>(this.internals, s);
  }
}
