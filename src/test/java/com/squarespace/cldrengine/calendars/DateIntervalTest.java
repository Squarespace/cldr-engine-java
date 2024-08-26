package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateIntervalFormatOptions;

public class DateIntervalTest {

  @Test
  public void testDateIntervalFormat() throws Exception {
    CLDR cldr = CLDR.get("ja");
    long startEpoch = -438678303000L;
    long endEpoch = 0L;
    String zoneId = "America/Argentina/Catamarca";
    CalendarDate start = cldr.Calendars.toGregorianDate(startEpoch, zoneId);
    CalendarDate end = cldr.Calendars.toGregorianDate(endEpoch, zoneId);

    DateIntervalFormatOptions opts = DateIntervalFormatOptions.build();

    String actual;

    actual = cldr.Calendars.formatDateInterval(start, end, opts);
    assertEquals(actual, "1956年2月6日～1969年12月31日");
  }
}
