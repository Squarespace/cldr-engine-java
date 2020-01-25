package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateIntervalFormatOptions;
import com.squarespace.cldrengine.api.GregorianDate;

public class Sketch12 {

  public static void main(String[] args) {
    CLDR cldr = CLDR.get("en");
    DateIntervalFormatOptions options = DateIntervalFormatOptions.build()
        .skeleton("EEEEyMMMd");
    String zoneId = "America/New_York";
    long epoch = 1513513513535L;
    CalendarDate start = GregorianDate.fromUnixEpoch(epoch, zoneId, 1, 1);
    CalendarDate end = GregorianDate.fromUnixEpoch(epoch + (3 * 86400000), zoneId, 1, 1);

    String s = cldr.Calendars.formatDateInterval(start, end, options);
    System.out.println(s);
  }
}
