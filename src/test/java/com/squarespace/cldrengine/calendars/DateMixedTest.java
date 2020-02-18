package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.FormatWidthType;

public class DateMixedTest {

  //March 11, 2018 7:00:25 AM UTC
  private static final long MARCH_11_2018_070025_UTC = 1520751625000L;

  private static final String LA = "America/Los_Angeles";

  @Test
  public void testMixedFormats() {
    CLDR en = CLDR.get("en");
    CalendarDate date;
    DateFormatOptions opts;
    String s;

    date = en.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, LA);

    opts = opts().date(FormatWidthType.SHORT).skeleton("hm");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "3/10/18, 11:00 PM");

    opts = opts().date(FormatWidthType.SHORT).skeleton("Hm");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "3/10/18, 23:00");

    opts = opts().time(FormatWidthType.SHORT).skeleton("EyMMMd");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "Sat, Mar 10, 2018, 11:00 PM");

    opts = opts().time(FormatWidthType.FULL).skeleton("yMd");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "3/10/2018, 11:00:25 PM Pacific Standard Time");

    // skeleton conflicts with both date/time, not used

    opts = opts().date(FormatWidthType.LONG).time(FormatWidthType.LONG)
        .skeleton("hm");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "March 10, 2018 at 11:00:25 PM PST");

    // skeleton conflicts with date, only time portion used

    opts = opts().date(FormatWidthType.FULL).skeleton("yMdhm");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "Saturday, March 10, 2018 at 11:00 PM");

    opts = opts().date(FormatWidthType.FULL).skeleton("yMdHm");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "Saturday, March 10, 2018 at 23:00");

    // skeleton conflicts with time, only date portion used

    opts = opts().time(FormatWidthType.SHORT).skeleton("yMdhmmsv");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "3/10/2018, 11:00 PM");

    opts = opts().time(FormatWidthType.SHORT).skeleton("EyMMMdHmmmsv");
    s = en.Calendars.formatDate(date, opts);
    assertEquals(s, "Sat, Mar 10, 2018, 11:00 PM");
  }

  private static DateFormatOptions opts() {
    return DateFormatOptions.build();
  }

}
