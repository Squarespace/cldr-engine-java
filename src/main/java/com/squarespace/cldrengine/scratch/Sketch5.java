package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.calendars.CalendarDate;
import com.squarespace.cldrengine.calendars.DateFormatOptions;
import com.squarespace.cldrengine.calendars.GregorianDate;
import com.squarespace.cldrengine.internal.ContextType;
import com.squarespace.cldrengine.internal.FormatWidthType;

public class Sketch5 {

  public static void main(String[] args) {
    String id = "es";
    CLDR cldr = CLDR.get(id);
    String zoneId = "America/Los_Angeles";
    CalendarDate date = GregorianDate.fromUnixEpoch(1579634069000L, zoneId, 1, 1);
    System.out.println(date.toString());

    DateFormatOptions options;
    String r;

    options = DateFormatOptions.builder()
        .datetime(FormatWidthType.FULL)
        .context(ContextType.BEGIN_SENTENCE)
        .build();
    r = cldr.Calendars.formatDate(date, options);
    System.out.println("1>> " + r);

    options = DateFormatOptions.builder()
        .skeleton("EEEEyMMMd")
        .context(ContextType.BEGIN_SENTENCE)
        .build();
    r = cldr.Calendars.formatDate(date, options);
    System.out.println("2>> " + r);

    options = DateFormatOptions.builder()
        .context(ContextType.BEGIN_SENTENCE)
        .time(FormatWidthType.LONG)
        .build();
    r = cldr.Calendars.formatDate(date, options);
    System.out.println("3>> " + r);
  }
}
