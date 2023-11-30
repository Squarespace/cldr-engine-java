package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.DayPeriodAltType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.EraAltType;
import com.squarespace.cldrengine.api.EraWidthType;
import com.squarespace.cldrengine.api.MetaZoneType;
import com.squarespace.cldrengine.api.TimeZoneNameType;
import com.squarespace.cldrengine.general.GeneralInternals;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.CalendarFields;
import com.squarespace.cldrengine.internal.CalendarSchema;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.TimeZoneSchema;
import com.squarespace.cldrengine.internal.Vector2Arrow;
import com.squarespace.cldrengine.internal.Vector3Arrow;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.parsing.DateTimePattern.DateTimeNode;

import lombok.AllArgsConstructor;

/**
 * Formats a date-time pattern using a given calendar context.
 */
class CalendarFormatter<T extends CalendarDate> {

  public final Internals internals;
  public final GeneralInternals general;
  public final CalendarSchema cal;
  public final TimeZoneSchema tz;

  public CalendarFormatter(Internals internals, CalendarSchema schema) {
    this.internals = internals;
    this.general = internals.general;
    this.cal = schema;
    this.tz = internals.schema.TimeZones;
  }

  public <R> void format(AbstractValue<R> val, CalendarContext<T> ctx, DateTimePattern pattern) {
    format(val, ctx, pattern, true);
  }

  public <R> void format(AbstractValue<R> val, CalendarContext<T> ctx, DateTimePattern pattern, boolean first) {
    int len = pattern.nodes.size();
    for (int i = 0; i < len; i++) {
      Object node = pattern.nodes.get(i);
      if (node instanceof String) {
        val.add("literal", (String)node);
        continue;
      }

      DateTimeNode d = (DateTimeNode)node;
      int w = d.width;
      ContextTransformFieldType field = null;
      String type = "";
      String value = "";

      // Date field symbol table
      // https://www.unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table
      switch (d.field) {

        // ERA
        case 'G':
          type = "era";
          value = this.cal.eras.get(ctx.bundle,
              w == 5 ? EraWidthType.NARROW : w == 4 ? EraWidthType.NAMES : EraWidthType.ABBR,
                  Long.toString(ctx.date.era()),
                  new EraAltType[] { ctx.alt.era.get(), EraAltType.NONE });
          if (w != 5) {
            field = w == 4 ? ContextTransformFieldType.ERA_NAME : ContextTransformFieldType.ERA_ABBR;
          }
          break;

        // YEAR
        case 'y':
          type = "year";
          value = _year(ctx, ctx.date.year(), w);
          break;

        // YEAR IN WEEK OF YEAR
        case 'Y':
          type = "year";
          value = _year(ctx, ctx.date.yearOfWeekOfYear(), w);
          break;

        // EXTENDED YEAR
        case 'u':
          type = "year";
          value = _num(ctx, ctx.date.extendedYear(), w);
          break;

        // CYCLIC YEAR
        case 'U':
          type = "cyclic-year";
          // TODO: support chinese cyclical years
          value = "";
          break;

        // RELATED YEAR
        case 'r':
          type = "related-year";
          // Note: this is always rendered using 'latn' digits
          value = ctx.latnSystem.formatString(ctx.date.relatedYear(), false, w);
          break;

        // QUARTER
        case 'Q':
        case 'q':
          type = "quarter";
          value = this.quarter(ctx, d);
          break;

        // MONTH FORMAT
        case 'M':
          type = "month";
          value = this.month(ctx, d);
          switch (w) {
            case 3:
            case 4:
              field = ContextTransformFieldType.MONTH_FORMAT_EXCEPT_NARROW;
              break;
          }
          break;

         // MONTH STANDALONE
        case 'L':
          type = "month";
          value = this.month(ctx, d);
          switch (w) {
            case 3:
            case 4:
              field = ContextTransformFieldType.MONTH_STANDALONE_EXCEPT_NARROW;
              break;
          }
          break;


        // 'l' - deprecated

        // WEEK OF WEEK YEAR
        case 'w':
          type = "week";
          value = _num(ctx, ctx.date.weekOfYear(), Math.min(w, 2));
          break;

        // WEEK OF MONTH
        case 'W':
          type = "week";
          value = _num(ctx, ctx.date.weekOfMonth(), 1);
          break;

        // DAY OF MONTH
        case 'd':
          type = "day";
          value = _num(ctx, ctx.date.dayOfMonth(), Math.min(w,  2));
          break;

        // DAY OF YEAR
        case 'D':
          type = "day";
          value = _num(ctx, ctx.date.dayOfYear(), Math.min(w,  3));
          break;

        // DAY OF WEEK IN MONTH
        case 'F':
          type = "day";
          value = _num(ctx, ctx.date.dayOfWeekInMonth(), 1);
          break;

        // MODIFIED JULIAN DAY
        case 'g':
          type = "mjulian-day";
          value = _num(ctx, ctx.date.modifiedJulianDay(), w);
          break;

        // WEEKDAY FORMAT
        case 'E':
          type = "weekday";
          value = this._weekday(ctx.bundle, this.cal.format.weekdays, ctx.date, w);
          if (w != 5) {
            field = ContextTransformFieldType.DAY_FORMAT_EXCEPT_NARROW;
          }
          break;

        // WEEKDAY LOCAL
        case 'e':
          type = "weekday";
          value = this._weekdayLocal(ctx, d, false);
          break;

        // WEEKDAY LOCAL STANDALONE
        case 'c':
          type = "weekday";
          value = this._weekdayLocal(ctx, d, true);
          if (w != 5) {
            field = ContextTransformFieldType.DAY_STANDALONE_EXCEPT_NARROW;
          }
          break;

        // DAY PERIOD AM/PM
        case 'a':
          type = "dayperiod";
          value = this.cal.format.dayPeriods.get(ctx.bundle, widthKey(w),
              ctx.date.hourOfDay() < 12 ? "am" : "pm", new DayPeriodAltType[] { ctx.alt.dayPeriod.get(), DayPeriodAltType.NONE });
          break;

        // DAY PERIOD EXTENDED
        case 'b':
          type = "dayperiod";
          value = this.dayPeriodExt(ctx, d);
          break;

        // DAY PERIOD FLEXIBLE
        case 'B':
          type = "dayperiod";
          value = this.dayPeriodFlex(ctx, d);
          break;

        // HOUR 1-12 AND 0-23
        case 'h':
        case 'H':
          type = "hour";
          value = this.hour(ctx, d);
          break;

        // HOUR 0-11 and 1-24
        case 'K':
        case 'k':
          type = "hour";
          value = this.hourAlt(ctx, d);
          break;

        // 'j', 'J', 'C' - input skeleton symbols, not present in formats

        // MINUTE
        case 'm':
          type = "minute";
          value = _num(ctx, ctx.date.minute(), Math.min(w, 2));
          break;

        // SECOND
        case 's':
          type = "second";
          value = _num(ctx, ctx.date.second(), Math.min(w,  2));
          break;

        // FRACTIONAL SECOND
        case 'S':
          type = "fracsec";
          value = this.fractionalSecond(ctx, d);
          break;

        // MILLISECONDS IN DAY
        case 'A':
          type = "millis-in-day";
          value = _num(ctx, ctx.date.millisecondsInDay(), w);
          break;

        // TIMEZONE SPECIFIC NON-LOCATION
        case 'z':
          type = "timezone";
          value = this.timezone_z(ctx, d);
          break;

        // TIMEZONE ISO-8601 EXTENDED
        case 'Z':
          type = "timezone";
          value = this.timezone_Z(ctx, d);
          break;

        // TIMEZONE LOCALIZED
        case 'O':
          type = "timezone";
          value = this.timezone_O(ctx, d);
          break;

        // TIMEZONE GENERIC NON-LOCATION
        case 'v':
          type = "timezone";
          value = this.timezone_v(ctx, d);
          break;

        // TIMEZONE ID, EXEMPLAR CITY, GENERIC LOCATION
        case 'V':
          type = "timezone";
          value = this.timezone_V(ctx, d);
          break;

        // TIMEZONE ISO-8601 BASIC, EXTENDED
        case 'X':
        case 'x':
          type = "timezone";
          value = this.timezone_x(ctx, d);
          break;

        default:
          continue;
      }

      if (first && i == 0 && ctx.context != null && field != null) {
        value = this.internals.general.contextTransform(value, ctx.transform, ctx.context, field);
      }
      val.add(type, value);
    }
  }

  protected String _formatQuarterOrMonth(CalendarContext<T> ctx, Vector2Arrow<String, String> format,
      long value, int width) {
    return width >= 3 ?
        format.get(ctx.bundle, widthKey(width), Long.toString(value)) :
        _num(ctx, value, width);
  }

  protected String quarter(CalendarContext<T> ctx, DateTimeNode node) {
    CalendarFields format = node.field == 'Q' ? this.cal.format : this.cal.standAlone;
    int quarter = (int)((ctx.date.month() - 1) / 3) + 1;
    return this._formatQuarterOrMonth(ctx, format.quarters, quarter, node.width);
  }

  protected String month(CalendarContext<T> ctx, DateTimeNode node) {
    CalendarFields format = node.field == 'M' ? this.cal.format : this.cal.standAlone;
    return this._formatQuarterOrMonth(ctx, format.months, ctx.date.month(), node.width);
  }

  protected String _weekday(Bundle bundle, Vector2Arrow<String, String> format, CalendarDate date, int width) {
    String key2 = Long.toString(date.dayOfWeek());
    String key1 = "abbreviated";
    switch (width) {
      case 6:
        key1 = "short";
        break;
      case 5:
        key1 = "narrow";
        break;
      case 4:
        key1 = "wide";
        break;
    }
    return format.get(bundle, key1, key2);
  }

  protected String _weekdayLocal(CalendarContext<T> ctx, DateTimeNode node, boolean standalone) {
    int width = node.width;
    if (width > 2) {
      CalendarFields format = standalone ? this.cal.standAlone : this.cal.format;
      return this._weekday(ctx.bundle, format.weekdays, ctx.date, width);
    }
    long ord = ctx.date.ordinalDayOfWeek();
    if (standalone) {
      width = 1;
    }
    return ctx.system.formatString(ord, false, width);
  }

  protected String dayPeriodExt(CalendarContext<T> ctx, DateTimeNode node) {
    String key1 = widthKey(node.width);
    String key2 = ctx.date.isAM() ? "am" : "pm";
    String key2ext = key2;
    if (ctx.date.minute() == 0) {
      long hour = ctx.date.hourOfDay();
      key2ext = hour == 0 ? "midnight" : hour == 12 ? "noon" : key2;
    }
    Vector3Arrow<String, String, DayPeriodAltType> format = this.cal.format.dayPeriods;
    DayPeriodAltType[] alt = new DayPeriodAltType[] { ctx.alt.dayPeriod.get(), DayPeriodAltType.NONE };
    // Try extended and if it doesn't exist, fall back to am/pm
    String result = format.get(ctx.bundle, key1, key2ext, alt);
    return result.equals("") ? format.get(ctx.bundle, key1, key2, alt) : result;
  }

  protected String dayPeriodFlex(CalendarContext<T> ctx, DateTimeNode node) {
    long minutes = (ctx.date.hourOfDay() * 60) + ctx.date.minute();
    String key = this.internals.calendars.flexDayPeriod(ctx.bundle, minutes);
    String res = null;
    if (key != null) {
      DayPeriodAltType[] alt = new DayPeriodAltType[] { ctx.alt.dayPeriod.get(), DayPeriodAltType.NONE };
      res = this.cal.format.dayPeriods.get(ctx.bundle, widthKey(node.width), key, alt);
    }
    return isEmpty(res) ? this.dayPeriodExt(ctx, node) : res;
  }

  protected String hour(CalendarContext<T> ctx, DateTimeNode node) {
    boolean twelve = node.field == 'h';
    long hour = twelve ? ctx.date.hour() : ctx.date.hourOfDay();
    if (twelve && hour == 0) {
      hour = 12;
    }
    return _num(ctx, hour, Math.min(node.width, 2));
  }

  protected String hourAlt(CalendarContext<T> ctx, DateTimeNode node) {
    boolean twelve = node.field == 'K';
    long hour = twelve ? ctx.date.hour() : ctx.date.hourOfDay();
    if (!twelve && hour == 0) {
      hour = 24;
    }
    return _num(ctx, hour, Math.min(node.width, 2));
  }

  protected String fractionalSecond(CalendarContext<T> ctx, DateTimeNode node) {
    int width = node.width;
    long m = ctx.date.milliseconds();
    int d = width > 3 ? width - 3 : 0;
    width -= d;
    if (d > 0) {
      m *= Math.pow(10, d);
    }
    // Milliseconds always have precision 3, so handle the cases compactly
    long n = width == 3 ? m : (width == 2 ? (m / 10) : (m / 100));
    return _num(ctx, n, node.width);
  }

  /**
   * Timezone: short/long specific non-location format.
   * https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone
   */
  protected String timezone_z(CalendarContext<T> ctx, DateTimeNode node) {
    if (node.width > 4) {
      return "";
    }
    String key2 = ctx.date.metaZoneId();
    if (!isEmpty(key2)) {
      Vector2Arrow<TimeZoneNameType, MetaZoneType> format = node.width == 4 ?
          this.tz.metaZones.long_ : this.tz.metaZones.short_;
      String name = format.get(ctx.bundle, ctx.date.isDaylightSavings() ? TimeZoneNameType.DAYLIGHT : TimeZoneNameType.STANDARD,
          MetaZoneType.fromString(key2));
      if (!isEmpty(name)) {
        return name;
      }
    }
    return this.timezone_O(ctx, node);
  }

  /**
   * Timezone: ISO8601 basic/extended format, long localized GMT format.
   * https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone
   */
  protected String timezone_Z(CalendarContext<T> ctx, DateTimeNode node) {
    int width = node.width;
    if (width == 4) {
      return this.timezone_O(ctx, new DateTimeNode('O', width));
    }

    TZC tzc = getTZC(ctx.date.timeZoneOffset());
    StringBuilder fmt = new StringBuilder();
    if (width <= 5) {
      // TODO: use number params
      fmt.append(tzc.negative ? '-' : '+');
      fmt.append(_num(ctx, tzc.hours, 2));
      if (width == 5) {
        fmt.append(':');
      }
      fmt.append(_num(ctx, tzc.minutes, 2));
    }
    return fmt.toString();
  }

  /**
   * Timezone: short/long localized GMT format.
   */
  protected String timezone_O(CalendarContext<T> ctx, DateTimeNode node) {
    return node.width == 1 || node.width == 4 ? this._wrapGMT(ctx, node.width == 1) : "";
  }

  /**
   * Timezone: short/long generic non-location format.
   */
  protected String timezone_v(CalendarContext<T> ctx, DateTimeNode node) {
    int width = node.width;
    if (width != 1 && width != 4) {
      return "";
    }
    String name = "";
    String key = ctx.date.metaZoneId();
    Vector2Arrow<TimeZoneNameType, MetaZoneType> format = width == 1 ?
        this.tz.metaZones.short_ : this.tz.metaZones.long_;
    name = format.get(ctx.bundle, TimeZoneNameType.GENERIC, MetaZoneType.fromString(key));
    return name.isEmpty() ? this.timezone_O(ctx, new DateTimeNode('O', width)) : name;
  }

  /**
   * Timezone: short/long zone ID, exemplar city, generic location format.
   * https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone
   */
  protected String timezone_V(CalendarContext<T> ctx, DateTimeNode node) {
    String stableId = ctx.date.timeZoneStableId();
    String city = "";
    switch (node.width) {
      case 4:
        city = this.tz.exemplarCity.get(ctx.bundle, stableId);
        if (city.isEmpty()) {
          return this.timezone_O(ctx, new DateTimeNode('O', 4));
        }
        String pattern = this.tz.regionFormat.get(ctx.bundle);
        return this.general.formatWrapper(pattern, city);

      case 3:
        // Exemplar city for the timezone
        city = this.tz.exemplarCity.get(ctx.bundle, stableId);
        return city.isEmpty() ? this.tz.exemplarCity.get(ctx.bundle, "Etc/Unknown") : city;

      case 2:
        return ctx.date.timeZoneId();

      case 1:
        return "unk";

      default:
        return "";
    }
  }

  /**
   * Timezone: ISO8601 basic format
   * https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-zone
   */
  protected String timezone_x(CalendarContext<T> ctx, DateTimeNode node) {
    TZC tzc = getTZC(ctx.date.timeZoneOffset());
    StringBuilder fmt = new StringBuilder();
    if (node.width >= 1 && node.width <= 5) {
      boolean zero = tzc.hours == 0 && tzc.minutes == 0;
      fmt.append(zero ? '+' : tzc.negative ? '-' : '+');
      fmt.append(_num(ctx, tzc.hours, 2));
      if (node.width == 3 || node.width == 5) {
        fmt.append(':');
      }
      if (node.width != 1 || tzc.minutes > 0) {
        fmt.append(_num(ctx, tzc.minutes, 2));
      }
      if (node.field == 'X' && tzc.offset == 0) {
        fmt.append('Z');
      }
    }
    return fmt.toString();
  }

  protected String _wrapGMT(CalendarContext<T> ctx, boolean short_) {
    long offset = ctx.date.timeZoneOffset();
    if (offset == 0) {
      return this.tz.gmtZeroFormat.get(ctx.bundle);
    }
    TZC tzc = getTZC(offset);
    boolean emitMins = !short_ || tzc.minutes > 0;
    DateTimePattern hourPattern = this._hourPattern(ctx.bundle, tzc.negative);
    StringBuilder fmt = new StringBuilder();
    for (int i = 0; i < hourPattern.nodes.size(); i++) {
      Object elem = hourPattern.nodes.get(i);
      if (elem instanceof String) {
        String p = (String)elem;
        boolean sep = p.equals(".") || p.equals(":");
        if (!sep || emitMins) {
          fmt.append(p);
        }
      } else {
        DateTimeNode node = (DateTimeNode)elem;
        if (node.field == 'H') {
          fmt.append(node.width == 1 ? _num(ctx, tzc.hours, 1) : _num(ctx, tzc.hours, short_ ? 1 : node.width));
        } else if (node.field == 'm' && emitMins) {
          fmt.append(_num(ctx, tzc.minutes, node.width));
        }
      }
    }

    String wrap = this.tz.gmtFormat.get(ctx.bundle);
    return this.general.formatWrapper(wrap, fmt.toString());
  }

  protected DateTimePattern _hourPattern(Bundle bundle, boolean negative) {
    String raw = this.tz.hourFormat.get(bundle);
    return this.internals.calendars.getHourPattern(raw, negative);
  }

  private String _num(CalendarContext<T> ctx, long n, int minInt) {
    return ctx.system.formatString(new Decimal(n), false, minInt);
  }

  private String _year(CalendarContext<T> ctx, long year, int width) {
    return _num(ctx, width == 2 ? year % 100 : year, width);
  }

  private static String widthKey(int width) {
    switch (width) {
      case 5:
        return "narrow";
      case 4:
        return "wide";
      default:
        return "abbreviated";
    }
  }

  private static TZC getTZC(long offset) {
    boolean negative = offset < 0;
    if (negative) {
      offset *= -1;
    }
    offset /= 60000;
    long hours = offset / 60;
    long minutes = offset % 60;
    return new TZC(offset, negative, hours, minutes);
  }

  @AllArgsConstructor
  private static class TZC {
    long offset;
    boolean negative;
    long hours;
    long minutes;
  }
}
