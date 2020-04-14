package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

public class DateFormatTest {

  private final static CLDR EN = CLDR.get("en");

  @Test
  public void testAltOptions() {
    String r;
    CalendarDate date;

    // Friday, April 17, 2020 12:34:56 PM UTC
    date = EN.Calendars.toGregorianDate(1587126896000L, "America/New_York");

    r = EN.Calendars.formatDate(date, DateFormatOptions.build()
        .time(FormatWidthType.MEDIUM));
    assertEquals(r, "8:34:56 AM");

    r = EN.Calendars.formatDate(date, DateFormatOptions.build()
        .time(FormatWidthType.MEDIUM)
        .alt(DateFormatAltOptions.build().dayPeriod(DayPeriodAltType.CASING)));
    assertEquals(r, "8:34:56 am");

    r = EN.Calendars.formatDate(date, DateFormatOptions.build()
        .skeleton("GyMMd"));
    assertEquals(r, "Apr 17, 2020 AD");

    r = EN.Calendars.formatDate(date, DateFormatOptions.build()
        .skeleton("GGGGyMMd"));
    assertEquals(r, "Apr 17, 2020 Anno Domini");

    r = EN.Calendars.formatDate(date, DateFormatOptions.build()
        .skeleton("GyMMd")
        .alt(DateFormatAltOptions.build().era(EraAltType.SENSITIVE)));
    assertEquals(r, "Apr 17, 2020 CE");

    r = EN.Calendars.formatDate(date, DateFormatOptions.build()
        .skeleton("GGGGyMMd")
        .alt(DateFormatAltOptions.build().era(EraAltType.SENSITIVE)));
    assertEquals(r, "Apr 17, 2020 Common Era");
  }

}
