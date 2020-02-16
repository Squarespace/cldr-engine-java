package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateFormatOptions;

public class DateSkeletonTest {

  //March 11, 2018 7:00:25 AM UTC
  private static final long MARCH_11_2018_070025_UTC = 1520751625000L;

  //March 1, 2018 6:45:17 PM UTC
  private static final long MARCH_01_2018_184517_UTC = 1519929917000L;

  private static final String LA = "America/Los_Angeles";

  @Test
  public void testMetacharacters() {
    CLDR en = CLDR.get("en");

    CalendarDate date;
    String s;

    date = en.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, LA);

    s = en.Calendars.formatDate(date, opts().skeleton("j"));
    assertEquals(s, "11 PM");

    s = en.Calendars.formatDate(date, opts().skeleton("jmm"));
    assertEquals(s, "11:00 PM");

    s = en.Calendars.formatDate(date, opts().skeleton("J"));
    assertEquals(s, "23");

    s = en.Calendars.formatDate(date, opts().skeleton("Jmm"));
    assertEquals(s, "23:00");

    s = en.Calendars.formatDate(date, opts().skeleton("Cmm"));
    assertEquals(s, "11:00 PM");

    s = en.Calendars.formatDate(date, opts().skeleton("K"));
    assertEquals(s, "11 PM");

    s = en.Calendars.formatDate(date, opts().skeleton("BH"));
    assertEquals(s, "23");
  }

  @Test
  public void testMilliseconds() {
    CLDR en = CLDR.get("en");

    CalendarDate date;
    String s;

    date = en.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC + 567, LA);

    s = en.Calendars.formatDate(date, opts().skeleton("hmsS"));
    assertEquals(s, "11:00:25.5 PM");

    s = en.Calendars.formatDate(date, opts().skeleton("hmsSS"));
    assertEquals(s, "11:00:25.56 PM");

    s = en.Calendars.formatDate(date, opts().skeleton("hmsSSS"));
    assertEquals(s, "11:00:25.567 PM");

    s = en.Calendars.formatDate(date, opts().skeleton("hmsSSSS"));
    assertEquals(s, "11:00:25.5670 PM");

    CLDR fr = CLDR.get("fr");
    s = fr.Calendars.formatDate(date, opts().skeleton("hmsSSSS"));
    assertEquals(s, "11:00:25,5670 PM");
  }

  private static DateFormatOptions opts() {
    return DateFormatOptions.build();
  }
}
