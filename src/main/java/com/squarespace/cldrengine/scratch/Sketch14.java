package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;
import com.squarespace.cldrengine.api.RelativeTimeFormatOptions;
import com.squarespace.cldrengine.api.TimePeriodField;

public class Sketch14 {

  public static void main(String[] args) {
    CLDR cldr = CLDR.get("en");
    RelativeTimeFormatOptions options = RelativeTimeFormatOptions.build()
        .context(ContextType.BEGIN_SENTENCE)
        .minimumFractionDigits(1);
    String s;

    s = cldr.Calendars.formatRelativeTimeField(new Decimal("-5.7"), RelativeTimeFieldType.DAY, options);
    System.out.println(s);

    String zoneId = "America/New_York";
    long epoch = 135135133335L;
    CalendarDate start = GregorianDate.fromUnixEpoch(epoch, zoneId, 1, 1);
    CalendarDate end = GregorianDate.fromUnixEpoch((long)(epoch + (7.5 * 86400000)), zoneId, 1, 1);
    s = cldr.Calendars.formatRelativeTime(start, end, options);
    System.out.println(s);

    options.field(TimePeriodField.DAY).minimumFractionDigits(1).maximumFractionDigits(1);
    s = cldr.Calendars.formatRelativeTime(start, end, options);
    System.out.println(s);
  }
}
