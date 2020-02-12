package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.util.Map;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.internal.CalendarSchema;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.parsing.DateTimePattern;

class GregorianPatterns extends CalendarPatterns {

  public GregorianPatterns(Bundle bundle, Internals internals, CalendarSchema schema) {
    super(bundle, internals, schema);
  }

  @Override
  public DateTimePattern getAvailablePattern(CalendarDate d, DateSkeleton s) {
    String pattern = s.pattern;
    if (isEmpty(pattern)) {
      switch (s.skeleton) {
        case "MMMMW":
          pattern = pluralPattern(d.weekOfMonth(), s);
          break;
        case "yw":
          pattern = pluralPattern(d.weekOfYear(), s);
          break;
        default:
          pattern = this.rawAvailableFormats.get(s.skeleton);
          break;
      }
    }
    return this.internals.calendars.parseDatePattern(isEmpty(pattern) ? "" : pattern);
  }

  private String pluralPattern(long n, DateSkeleton s) {
    Decimal value = new Decimal(n);
    PluralType plural = this.bundle.plurals().cardinal(value);
    Map<String, String> formats = this.rawPluralFormats.get(plural);
    return formats == null ? null : formats.get(s.skeleton);
  }
}
