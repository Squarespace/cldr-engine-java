package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;

public class CalendarDateTest {

  private static final String NEW_YORK = "America/New_York";

  @Test
  public void testTimeZoneAbbr() {
    GregorianDate d;
    long n;

    // Thursday, December 31, 2020 12:30:00 PM UTC
    n = 1609417800000L;

    d = make(n, NEW_YORK);
    assertEquals(d.timeZoneAbbr(), "EST");
  }

  @Test
  public void testWeeksCoverage() {
    GregorianDate d;
    long n;

    //  December 2020
    //  Su Mo Tu We Th Fr Sa
    //         1  2  3  4  5
    //   6  7  8  9 10 11 12
    //  13 14 15 16 17 18 19
    //  20 21 22 23 24 25 26
    //  27 28 29 30 31

    // Thursday, December 31, 2020 12:30:00 PM UTC
    n = 1609417800000L;

    // Dec 31
    d = make(n, NEW_YORK);
    assertEquals(d.weekOfMonth(), 5);
    assertEquals(d.weekOfYear(), 1);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 53);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Dec 30
    d = d.subtract(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 5);
    assertEquals(d.weekOfYear(), 1);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 53);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Dec 29
    d = d.subtract(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 5);
    assertEquals(d.weekOfYear(), 1);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 53);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Dec 28
    d = d.subtract(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 5);
    assertEquals(d.weekOfYear(), 1);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 53);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Dec 27
    d = d.subtract(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 5);
    assertEquals(d.weekOfYear(), 1);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 52);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Dec 26
    d = d.subtract(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 4);
    assertEquals(d.weekOfYear(), 52);
    assertEquals(d.yearOfWeekOfYear(), 2020);
    assertEquals(d.weekOfYearISO(), 52);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Dec 25
    d = d.subtract(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 4);
    assertEquals(d.weekOfYear(), 52);
    assertEquals(d.yearOfWeekOfYear(), 2020);
    assertEquals(d.weekOfYearISO(), 52);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    //  January 2021
    //  Su Mo Tu We Th Fr Sa
    //                  1  2
    //   3  4  5  6  7  8  9
    //  10 11 12 13 14 15 16
    //  17 18 19 20 21 22 23
    //  24 25 26 27 28 29 30
    //  31

    // Jan 1
    d = make(n, NEW_YORK).add(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 1);
    assertEquals(d.weekOfYear(), 1);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 53);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Jan 2
    d = d.add(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 1);
    assertEquals(d.weekOfYear(), 1);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 53);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Jan 3
    d = d.add(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 2);
    assertEquals(d.weekOfYear(), 2);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 53);
    assertEquals(d.yearOfWeekOfYearISO(), 2020);

    // Jan 4
    d = d.add(TimePeriod.build().day(1));
    assertEquals(d.weekOfMonth(), 2);
    assertEquals(d.weekOfYear(), 2);
    assertEquals(d.yearOfWeekOfYear(), 2021);
    assertEquals(d.weekOfYearISO(), 1);
    assertEquals(d.yearOfWeekOfYearISO(), 2021);
  }

  private static GregorianDate make(long epoch, String zoneId) {
    return GregorianDate.fromUnixEpoch(epoch, zoneId, DayOfWeek.SUNDAY, 1);
  }

}
