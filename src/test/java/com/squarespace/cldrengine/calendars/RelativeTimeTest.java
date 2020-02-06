package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.RelativeTimeFormatOptions;
import com.squarespace.cldrengine.api.TimePeriodField;

public class RelativeTimeTest {

  @Test
  public void testRelativeTime() throws Exception {
    CLDR cldr = CLDR.get("en");
    long startEpoch = -438678303000L;
    long endEpoch = 0L;
    String zoneId = "America/Argentina/Catamarca";
    CalendarDate start = cldr.Calendars.toGregorianDate(startEpoch, zoneId);
    CalendarDate end = cldr.Calendars.toGregorianDate(endEpoch, zoneId);
    RelativeTimeFormatOptions opts = RelativeTimeFormatOptions.build()
        .dayOfWeek(false)
        .field(TimePeriodField.MONTH);

    String actual;

    actual = cldr.Calendars.formatRelativeTime(start, end, opts);
    assertEquals(actual, "in 167 months");

    opts.field(TimePeriodField.DAY);
    actual = cldr.Calendars.formatRelativeTime(start, end, opts);
    assertEquals(actual, "in 5,077 days");

    opts.field(TimePeriodField.MINUTE);
    actual = cldr.Calendars.formatRelativeTime(start, end, opts);
    assertEquals(actual, "in 7,311,305 minutes");
  }
}
