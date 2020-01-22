package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.GregorianDate;

public class Sketch5 {

  public static void main(String[] args) {
    String id = "es";
    CLDR cldr = CLDR.get(id);
    String zoneId = "America/Los_Angeles";
    CalendarDate date = GregorianDate.fromUnixEpoch(1579634069000L, zoneId, 1, 1);
    System.out.println(date.toString());

    DateFormatOptions options;
    String r;

    options = DateFormatOptions.build()
        .datetime(FormatWidthType.FULL)
        .context(ContextType.BEGIN_SENTENCE);
    r = cldr.Calendars.formatDate(date, options);
    System.out.println("1>> " + r);

    options = DateFormatOptions.build()
        .skeleton("EEEEyMMMd")
        .context(ContextType.BEGIN_SENTENCE);
    r = cldr.Calendars.formatDate(date, options);
    System.out.println("2>> " + r);

    options = DateFormatOptions.build()
        .context(ContextType.BEGIN_SENTENCE)
        .time(FormatWidthType.LONG);
    r = cldr.Calendars.formatDate(date, options);
    System.out.println("3>> " + r);
  }
}
