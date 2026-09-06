package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.BuddhistDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.ISO8601Date;
import com.squarespace.cldrengine.api.JapaneseDate;
import com.squarespace.cldrengine.api.PersianDate;
import com.squarespace.cldrengine.api.TimePeriod;

/**
 * Add/subtract cases across all five calendars. Updated to assert the
 * Temporal-compatible algorithm ported in todo 008 (integer truncation of
 * fractional fields, end-of-month clampYMD day clamping, DST wall-clock
 * preservation via TZ.fromWall): the expected values match the TS 1.15.0
 * engine (see also DateAddTest, the verbatim dateadd.test.ts mirror).
 */
public class CalendarMathTest {

  // Sat March 11, 2000 8:00:25 AM UTC
  private static final long BASE = 952761625000L;

  private static final String NY = "America/New_York";
  private static final String LA = "America/Los_Angeles";
  private static final String LON = "Europe/London";

  @Test
  public void testFractionalYears() {
    // Sunday, April 11, 2004 4:34:56 PM UTC
    long epoch = 1081701296000L;
    String zoneId = "UTC";

    GregorianDate date = gregorian(epoch, zoneId);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2004-04-11 16:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(4.25)); // truncated to 4
    assertEquals(q.toString(), "Gregorian 2008-04-11 16:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(4).month(3));
    assertEquals(q.toString(), "Gregorian 2008-07-11 16:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(-4.25)); // truncated to -4
    assertEquals(q.toString(), "Gregorian 2000-04-11 16:34:56.000 Etc/UTC");

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

    // Fractional years are truncated
    q = date.add(TimePeriod.build().year(1.5));
    assertEquals(q.toString(), "Gregorian 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-3));
    assertEquals(q.toString(), "Gregorian 1997-03-11 03:00:25.000 America/New_York");

    // Earliest timezone offset for NY is LMT -4:56:2; wall clock is preserved
    q = date.add(TimePeriod.build().year(-305));
    assertEquals(q.toString(), "Gregorian 1695-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-1000));
    assertEquals(q.toString(), "Gregorian 1000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1100));
    assertEquals(q.toString(), "Gregorian 3100-03-11 03:00:25.000 America/New_York");

    q = date.withZone("America/Los_Angeles");
    assertEquals(q.toString(), "Gregorian 2000-03-11 00:00:25.000 America/Los_Angeles");
  }

  @Test
  public void testISO8601Years() {
    ISO8601Date date = iso8601(BASE, NY);
    ISO8601Date q;

    assertEquals(date.toString(), "ISO8601 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "ISO8601 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "ISO8601 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testJapaneseYears() {
    JapaneseDate date = japanese(BASE, NY);
    JapaneseDate q;

    assertEquals(date.toString(), "Japanese 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Japanese 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Japanese 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testPersianYears() {
    PersianDate date = persian(BASE, NY);
    PersianDate q;

    assertEquals(date.toString(), "Persian 1378-12-21 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Persian 1379-12-21 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Persian 1373-12-21 03:00:25.000 America/New_York");
  }

  @Test
  public void testBuddhistYears() {
    BuddhistDate date = buddhist(BASE, NY);
    BuddhistDate q;

    assertEquals(date.toString(), "Buddhist 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Buddhist 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Buddhist 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testMonths() {
    GregorianDate date = gregorian(BASE, NY);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Gregorian 2000-04-11 03:00:25.000 America/New_York");

    // Fractional months are truncated
    q = date.add(TimePeriod.build().month(1.5));
    assertEquals(q.toString(), "Gregorian 2000-04-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(7));
    assertEquals(q.toString(), "Gregorian 2000-10-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(9));
    assertEquals(q.toString(), "Gregorian 2000-12-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(-17));
    assertEquals(q.toString(), "Gregorian 1998-10-11 03:00:25.000 America/New_York");

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
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2019-03-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Gregorian 2019-04-30 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(2));
    assertEquals(q.toString(), "Gregorian 2019-05-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(3));
    assertEquals(q.toString(), "Gregorian 2019-06-30 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(4));
    assertEquals(q.toString(), "Gregorian 2019-07-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(5));
    assertEquals(q.toString(), "Gregorian 2019-08-31 12:30:45.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(6));
    assertEquals(q.toString(), "Gregorian 2019-09-30 12:30:45.000 Etc/UTC");
  }

  @Test
  public void testPersianMonths() {
    PersianDate date = persian(BASE, NY);
    PersianDate q;

    assertEquals(date.toString(), "Persian 1378-12-21 03:00:25.000 America/New_York");

    // Oddities show up with time and non-gregorian calendars, since the timezone
    // rules are based on the gregorian calendar. So adding 1 month below shifts
    // to the next persian year, but in gregorian calendar it crosses a daylight
    // savings boundary for America/New_York, so the hour changes.
    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Persian 1379-01-21 03:00:25.000 America/New_York");

    // Fractional months are truncated
    q = date.add(TimePeriod.build().month(1.5));
    assertEquals(q.toString(), "Persian 1379-01-21 03:00:25.000 America/New_York");

    // Monday, March 21, 2022 12:34:54 PM UTC
    date = persian(1647880496000L, NY);
    assertEquals(date.toString(), "Persian 1401-01-01 12:34:56.000 America/New_York");

    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Persian 1401-02-01 12:34:56.000 America/New_York");

    q = date.add(TimePeriod.build().month(1.5));
    assertEquals(q.toString(), "Persian 1401-02-01 12:34:56.000 America/New_York");
  }

  @Test
  public void testDays() {
    GregorianDate date = gregorian(BASE, NY);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(1));
    assertEquals(q.toString(), "Gregorian 2000-03-12 03:00:25.000 America/New_York");

    // Fractional days are truncated
    q = date.add(TimePeriod.build().day(1.5));
    assertEquals(q.toString(), "Gregorian 2000-03-12 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(15));
    assertEquals(q.toString(), "Gregorian 2000-03-26 03:00:25.000 America/New_York");

    // Crossing DST boundary preserves the wall clock
    q = date.add(TimePeriod.build().day(30));
    assertEquals(q.toString(), "Gregorian 2000-04-10 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(-45));
    assertEquals(q.toString(), "Gregorian 2000-01-26 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(450));
    assertEquals(q.toString(), "Gregorian 2001-06-04 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(-3650));
    assertEquals(q.toString(), "Gregorian 1990-03-14 03:00:25.000 America/New_York");

    // 40 days (fractional .25 truncated away)
    q = date.add(TimePeriod.build().day(40.25));
    assertEquals(q.toString(), "Gregorian 2000-04-20 03:00:25.000 America/New_York");
  }

  @Test
  public void testWeeks() {
    GregorianDate date = gregorian(BASE, NY);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");
    assertEquals(date.dayOfWeek(), DayOfWeek.SATURDAY);

    q = date.add(TimePeriod.build().week(1));
    assertEquals(q.toString(), "Gregorian 2000-03-18 03:00:25.000 America/New_York");
    assertEquals(q.dayOfWeek(), DayOfWeek.SATURDAY);

    // Fractional weeks are truncated to whole weeks
    q = date.add(TimePeriod.build().week(1.5));
    assertEquals(q.toString(), "Gregorian 2000-03-18 03:00:25.000 America/New_York");
    assertEquals(q.dayOfWeek(), DayOfWeek.SATURDAY);

    q = date.add(TimePeriod.build().week(10));
    assertEquals(q.toString(), "Gregorian 2000-05-20 03:00:25.000 America/New_York");
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
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.withZone(LA);
    assertEquals(q.toString(), "Gregorian 2000-03-11 00:00:25.000 America/Los_Angeles");

    q = date.withZone(LON);
    assertEquals(q.toString(), "Gregorian 2000-03-11 08:00:25.000 Europe/London");
  }

  @Test
  public void testHours() {
    GregorianDate date = gregorian(BASE, NY);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(5));
    assertEquals(q.toString(), "Gregorian 2000-03-11 08:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().hour(10.5)); // truncated to 10
    assertEquals(q.toString(), "Gregorian 2000-03-11 13:00:25.000 America/New_York");

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
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().minute(60));
    assertEquals(q.toString(), "Gregorian 2000-03-11 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().minute(5.505)); // truncated to 5
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:05:25.000 America/New_York");
  }

  @Test
  public void testMilliseconds() {
    GregorianDate date = gregorian(BASE, NY);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().millis(60));
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:00:25.060 America/New_York");

    q = date.add(TimePeriod.build().millis(120.5)); // truncated to 120
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:00:25.120 America/New_York");

    // milliseconds roll over next day
    q = date.add(TimePeriod.build().millis((86400L * 1000 * 2) + 120.5));
    assertEquals(q.toString(), "Gregorian 2000-03-13 03:00:25.120 America/New_York");
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
