package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.decimal.Decimal;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.CalendarSchema;
import com.squarespace.cldrengine.internal.ContextTransformFieldType;
import com.squarespace.cldrengine.internal.EraWidthType;
import com.squarespace.cldrengine.internal.FieldWidthType;
import com.squarespace.cldrengine.internal.GeneralInternals;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.parsing.DateTimePattern.DateTimeNode;

import lombok.AllArgsConstructor;

public class CalendarFormatterImpl<T extends CalendarDate> {

  public final GeneralInternals general;
  public final CalendarSchema cal;

  public CalendarFormatterImpl(GeneralInternals general) {
    this.general = general;
    this.cal = null;
  }

  public <R> void format(AbstractValue<R> val, CalendarContext<T> ctx, DateTimePattern pattern) {
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
                  Long.toString(ctx.date.era()));
          if (w != 5) {
            field = w == 4 ? ContextTransformFieldType.ERA_NAME : ContextTransformFieldType.ERA_ABBR;
          }
          break;

        // YEAR
        case 'y':
          type = "year";
//          value = _year
          break;
      }
    }

  }

  private String _num(CalendarContext<T> ctx, int n, int minInt) {
    return ctx.system.formatString(Decimal.coerce(n), false, minInt);
  }

  private String _year(CalendarContext<T> ctx, int year, int width) {
    return _num(ctx, width == 2 ? year % 100 : year, width);
  }

  private static FieldWidthType widthKey (int width) {
    switch (width) {
      case 5:
        return FieldWidthType.NARROW;
      case 4:
        return FieldWidthType.WIDE;
      default:
        return FieldWidthType.ABBREVIATED;
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
