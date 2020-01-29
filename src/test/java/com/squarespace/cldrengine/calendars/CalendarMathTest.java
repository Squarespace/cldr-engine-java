package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.BuddhistDate;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.ISO8601Date;
import com.squarespace.cldrengine.api.JapaneseDate;
import com.squarespace.cldrengine.api.PersianDate;
import com.squarespace.cldrengine.api.TimePeriod;

public class CalendarMathTest {

  // Sat March 11, 2000 8:00:25 AM UTC
  private static final long BASE = 952761625000L;

  private static final String NY = "America/New_York";
  private static final String LA = "America/Los_Angeles";
  private static final String LON = "Europe/London";

  @Test
  public void testFractionalYears() {
    // Sunday, April 11, 2004 4:34:56 AM
    long epoch = 1081701296000L;
    String zoneId = "UTC";

    GregorianDate date = gregorian(epoch, zoneId);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2004-04-11 16:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(4.25)); // + 4 years and 91.5 days (366 * .25)
    assertEquals(q.toString(), "Gregorian 2008-07-12 04:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(4).month(3));
    assertEquals(q.toString(), "Gregorian 2008-07-11 16:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(-4.25)); // - 4 years and 91.5 days (366 * .25)
    assertEquals(q.toString(), "Gregorian 2000-01-11 04:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(-4).month(-3));
    assertEquals(q.toString(), "Gregorian 2000-01-11 16:34:56.000 Etc/UTC");
  }

  @Test
  public void testWrap() {
    // Monday, January 10, 2000 12:00:00 PM
    long epoch = 947505600000L;
    String zoneId = "UTC";

    GregorianDate date = gregorian(epoch, zoneId);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-01-10 12:00:00.000 Etc/UTC");

    q = date.add(TimePeriod.build().day(-20));
    assertEquals(q.toString(), "Gregorian 1999-12-21 12:00:00.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(-1));
    assertEquals(q.toString(), "Gregorian 1999-12-10 12:00:00.000 Etc/UTC");
  }

  @Test
  public void testYears() {
    GregorianDate date = gregorian(BASE, NY);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1.5));
    assertEquals(q.toString(), "Gregorian 2001-09-09 16:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-3));
    assertEquals(q.toString(), "Gregorian 1997-03-11 03:00:25.000 America/New_York");

    // Earliest timezone offset for NY is LMT -4:56:2
    q = date.add(TimePeriod.build().year(-305));
    assertEquals(q.toString(), "Gregorian 1695-03-11 03:04:23.000 America/New_York");

    q = date.add(TimePeriod.build().year(-1000));
    assertEquals(q.toString(), "Gregorian 1000-03-11 03:04:23.000 America/New_York");

    q = date.add(TimePeriod.build().year(1100));
    assertEquals(q.toString(), "Gregorian 3100-03-11 03:00:25.000 America/New_York");

    q = date.withZone("America/Los_Angeles");
    assertEquals(q.toString(), "Gregorian 2000-03-11 00:00:25.000 America/Los_Angeles");
  }

  @Test
  public void testISO8601Years() {
    ISO8601Date date = iso8601(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "ISO8601 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "ISO8601 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "ISO8601 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testJapaneseYears() {
    JapaneseDate date = japanese(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Japanese 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Japanese 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Japanese 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testPersianYears() {
    PersianDate date = persian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Persian 1378-12-21 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Persian 1379-12-21 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Persian 1373-12-21 03:00:25.000 America/New_York");
  }

  @Test
  public void testBuddhistYears() {
    BuddhistDate date = buddhist(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Buddhist 2543-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Buddhist 2544-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Buddhist 2538-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testMonths() {
    GregorianDate date = gregorian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Gregorian 2000-04-11 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(1.5));
    assertEquals(q.toString(), "Gregorian 2000-04-26 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(7));
    assertEquals(q.toString(), "Gregorian 2000-10-11 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(9));
    assertEquals(q.toString(), "Gregorian 2000-12-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(-17));
    assertEquals(q.toString(), "Gregorian 1998-10-11 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(-60));
    assertEquals(q.toString(), "Gregorian 1995-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(-600));
    assertEquals(q.toString(), "Gregorian 1950-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(900));
    assertEquals(q.toString(), "Gregorian 2075-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testMonthRollover() {
    // Sunday, March 31, 2019 12:30:45 PM
    GregorianDate date = gregorian(1554035445000L, "UTC");
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2019-03-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Gregorian 2019-05-01 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(2));
    assertEquals(q.toString(), "Gregorian 2019-05-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(3));
    assertEquals(q.toString(), "Gregorian 2019-07-01 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(4));
    assertEquals(q.toString(), "Gregorian 2019-07-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(5));
    assertEquals(q.toString(), "Gregorian 2019-08-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(6));
    assertEquals(q.toString(), "Gregorian 2019-10-01 12:30:45.000 Etc/UTC");
  }

  @Test
  public void testPersianMonths() {
    PersianDate date = persian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Persian 1378-12-21 03:00:25.000 America/New_York");

    // Oddities show up with time and non-gregorian calendars, since the timezone
    // rules are based on the gregorian calendar. So adding 1 month below shifts
    // to the next persian year, but in gregorian calendar it crosses a daylight
    // savings boundary for America/New_York, so the hour changes.
    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Persian 1379-01-21 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(1.5));
    assertEquals(q.toString(), "Persian 1379-02-05 16:00:25.000 America/New_York");

    // Monday, March 21, 2022 12:34:54 PM UTC
    date = persian(1647880496000L, NY);
    assertEquals(date.toString(), "Persian 1401-01-01 12:34:56.000 America/New_York");

    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Persian 1401-02-01 12:34:56.000 America/New_York");

    q = date.add(TimePeriod.build().month(1.5));
    assertEquals(q.toString(), "Persian 1401-02-17 00:34:56.000 America/New_York");
  }

  @Test
  public void testDays() {
    GregorianDate date = gregorian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(1));
    assertEquals(q.toString(), "Gregorian 2000-03-12 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(1.5));
    assertEquals(q.toString(), "Gregorian 2000-03-12 15:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(15));
    assertEquals(q.toString(), "Gregorian 2000-03-26 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(-45));
    assertEquals(q.toString(), "Gregorian 2000-01-26 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(450));
    assertEquals(q.toString(), "Gregorian 2001-06-04 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(-3650));
    assertEquals(q.toString(), "Gregorian 1990-03-14 03:00:25.000 America/New_York");

    // 35 days (5 weeks) + 5 days + 6 hours (.25 day) + 1 hour (timezone offset change)
    q = date.add(TimePeriod.build().day(40.25));
    assertEquals(q.toString(), "Gregorian 2000-04-20 10:00:25.000 America/New_York");
  }

  @Test
  public void testWeeks() {
    GregorianDate date = gregorian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");
    assertEquals(date.dayOfWeek(), DayOfWeek.SATURDAY);

    q = date.add(TimePeriod.build().week(1));
    assertEquals(q.toString(), "Gregorian 2000-03-18 03:00:25.000 America/New_York");
    assertEquals(q.dayOfWeek(), DayOfWeek.SATURDAY);

    q = date.add(TimePeriod.build().week(1.5)); // 10 days 12 hours
    assertEquals(q.toString(), "Gregorian 2000-03-21 15:00:25.000 America/New_York");
    assertEquals(q.dayOfWeek(), DayOfWeek.TUESDAY);

    q = date.add(TimePeriod.build().week(10));
    assertEquals(q.toString(), "Gregorian 2000-05-20 04:00:25.000 America/New_York");
    assertEquals(q.dayOfWeek(), DayOfWeek.SATURDAY);

    q = date.add(TimePeriod.build().week(-52));
    assertEquals(q.toString(), "Gregorian 1999-03-13 03:00:25.000 America/New_York");
    assertEquals(q.dayOfWeek(), DayOfWeek.SATURDAY);

    q = date.add(TimePeriod.build().week(520));
    assertEquals(q.toString(), "Gregorian 2010-02-27 03:00:25.000 America/New_York");
    assertEquals(q.dayOfWeek(), DayOfWeek.SATURDAY);
  }

  @Test
  public void testWithZone() {
    GregorianDate date = gregorian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.withZone(LA);
    assertEquals(q.toString(), "Gregorian 2000-03-11 00:00:25.000 America/Los_Angeles");

    q = date.withZone(LON);
    assertEquals(q.toString(), "Gregorian 2000-03-11 08:00:25.000 Europe/London");
  }

  @Test
  public void testHours() {
    GregorianDate date = gregorian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(5));
    assertEquals(q.toString(), "Gregorian 2000-03-11 08:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(10.5));
    assertEquals(q.toString(), "Gregorian 2000-03-11 13:30:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(-24));
    assertEquals(q.toString(), "Gregorian 2000-03-10 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(72));
    assertEquals(q.toString(), "Gregorian 2000-03-14 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(96));
    assertEquals(q.toString(), "Gregorian 2000-03-15 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(108));
    assertEquals(q.toString(), "Gregorian 2000-03-15 15:00:25.000 America/New_York");
  }

  @Test
  public void testMinutes() {
    GregorianDate date = gregorian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().minute(60));
    assertEquals(q.toString(), "Gregorian 2000-03-11 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().minute(5.505));
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:05:55.300 America/New_York");
  }

  @Test
  public void testMilliseconds() {
    GregorianDate date = gregorian(BASE, NY);
    CalendarDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().millis(60));
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:00:25.060 America/New_York");

    q = date.add(TimePeriod.build().millis(120.5));
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:00:25.121 America/New_York");

    // milliseconds roll over next day
    q = date.add(TimePeriod.build().millis((86400 * 1000 * 2.5) + 120.5));
    assertEquals(q.toString(), "Gregorian 2000-03-13 15:00:25.121 America/New_York");
}

  private static GregorianDate gregorian(long epoch, String zoneId) {
    return GregorianDate.fromUnixEpoch(epoch, zoneId, 1, 1);
  }

  private static ISO8601Date iso8601(long epoch, String zoneId) {
    return ISO8601Date.fromUnixEpoch(epoch, zoneId, 1, 1);
  }

  private static JapaneseDate japanese(long epoch, String zoneId) {
    return JapaneseDate.fromUnixEpoch(epoch, zoneId, 1, 1);
  }

  private static PersianDate persian(long epoch, String zoneId) {
    return PersianDate.fromUnixEpoch(epoch, zoneId, 1, 1);
  }

  private static BuddhistDate buddhist(long epoch, String zoneId) {
    return BuddhistDate.fromUnixEpoch(epoch, zoneId, 1, 1);
  }
}


