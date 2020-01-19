package com.squarespace.cldrengine.calendars;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.CalendarSchema;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.parsing.DateTimePattern.DateTimeNode;
import com.squarespace.cldrengine.parsing.WrapperPattern;
import com.squarespace.cldrengine.utils.Cache;

public class CalendarInternals {

  public final Internals internals;
  public final Schema schema;

  public final Cache<CalendarFormatter<CalendarDate>> calendarFormatterCache;
  public final Set<String> availableCalendars;

  public CalendarInternals(Internals internals) {
    this.internals = internals;
    this.schema = internals.schema;

    System.err.println("CALENDARS: " + internals.config.get("calendars"));
    this.calendarFormatterCache = new Cache<>(this::buildFormatter, 1024);
    // TODO: fix
    this.availableCalendars = new HashSet<>(Arrays.asList("gregory"));
  }

  public CalendarFormatter<CalendarDate> getCalendarFormatter(CalendarType type) {
    return this.calendarFormatterCache.get(type.value);
  }

  public Object[] getHourPattern(String raw, boolean negative) {
    return new Object[] { } ;
  }

  public <R> R formatDateTime(CalendarType calendar, CalendarContext<CalendarDate> ctx,
      AbstractValue<R> value, DateTimeNode[] date, DateTimeNode[] time, String wrapper) {

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
      value.wrap(pattern, _time, _date);
      return value.render();
    }
    return _date != null ? _date : _time != null ? _time : value.empty();
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
    return new CalendarFormatter(this.internals, s);
  }
}
