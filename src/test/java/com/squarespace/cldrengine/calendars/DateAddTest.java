package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.BuddhistDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.ISO8601Date;
import com.squarespace.cldrengine.api.JapaneseDate;
import com.squarespace.cldrengine.api.PersianDate;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.calendars.DayOfWeek;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/dateadd.test.ts
 * (tag v1.15.0). Case tables copied verbatim.
 */
public class DateAddTest {

  // REFERENCE TIMEZONES (_referencedates.ts)
  private static final String LONDON = "Europe/London";
  private static final String LOS_ANGELES = "America/Los_Angeles";
  private static final String NEW_YORK = "America/New_York";
  private static final String TOKYO = "Asia/Tokyo";

  // Sat March 11, 2000 8:00:25 AM UTC (_referencedates.ts)
  private static final long MAR_11_2000 = 952761625000L;

  private static final long DAY = 86400L * 1000;

  private static BuddhistDate buddhist(long e, String z) {
    return BuddhistDate.fromUnixEpoch(e, z, 1, 1);
  }

  private static GregorianDate gregorian(long e, String z) {
    return GregorianDate.fromUnixEpoch(e, z, 1, 1);
  }

  private static ISO8601Date iso8601(long e, String z) {
    return ISO8601Date.fromUnixEpoch(e, z, 1, 1);
  }

  private static JapaneseDate japanese(long e, String z) {
    return JapaneseDate.fromUnixEpoch(e, z, 1, 1);
  }

  private static PersianDate persian(long e, String z) {
    return PersianDate.fromUnixEpoch(e, z, 1, 1);
  }

  @Test
  public void testLeapYear() {
    // January 31, 2024 8:30:00 AM GMT-05:00
    GregorianDate s = gregorian(1706707800000L, NEW_YORK);

    GregorianDate q = s.add(TimePeriod.build().year(1).month(1));
    assertEquals(q.toString(), "Gregorian 2025-02-28 08:30:00.000 America/New_York");

    q = s.add(TimePeriod.build().year(1).month(1).day(1));
    assertEquals(q.toString(), "Gregorian 2025-03-01 08:30:00.000 America/New_York");

    // February 29, 2024 8:30:00 AM GMT-05:00
    s = gregorian(1709213400000L, NEW_YORK);

    q = s.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2025-02-28 08:30:00.000 America/New_York");

    q = s.add(TimePeriod.build().year(1).day(1));
    assertEquals(q.toString(), "Gregorian 2025-03-01 08:30:00.000 America/New_York");
  }

  @Test
  public void testFractionalYears() {
    // The TS test builds 2004-04-11 16:34:56 via new Date(2004,3,11,16,34,56)
    // "treated as UTC" (locale-independent epoch 1081701296000 = 2004-04-11
    // 16:34:56 UTC).
    GregorianDate date = gregorian(1081701296000L, "UTC");

    assertEquals(date.toString(), "Gregorian 2004-04-11 16:34:56.000 Etc/UTC");

    GregorianDate q = date.add(TimePeriod.build().year(4.25)); // truncated to 4
    assertEquals(q.toString(), "Gregorian 2008-04-11 16:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(4).month(3));
    assertEquals(q.toString(), "Gregorian 2008-07-11 16:34:56.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(-4).month(-3));
    assertEquals(q.toString(), "Gregorian 2000-01-11 16:34:56.000 Etc/UTC");
  }

  @Test
  public void testYearWrap() {
    // 2000-01-10 12:00:00 UTC (new Date(2000,0,10,12,0,0) treated as UTC;
    // epoch 947505600000 = 2000-01-10 12:00:00 UTC).
    GregorianDate date = gregorian(947505600000L, "UTC");

    assertEquals(date.toString(), "Gregorian 2000-01-10 12:00:00.000 Etc/UTC");

    GregorianDate q = date.add(TimePeriod.build().day(-20));
    assertEquals(q.toString(), "Gregorian 1999-12-21 12:00:00.000 Etc/UTC");

    q = date.add(TimePeriod.build().month(-1));
    assertEquals(q.toString(), "Gregorian 1999-12-10 12:00:00.000 Etc/UTC");
  }

  @Test
  public void testYears() {
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-3));
    assertEquals(q.toString(), "Gregorian 1997-03-11 03:00:25.000 America/New_York");

    // Earliest timezone offset for NY is LMT -4:56:2
    q = date.add(TimePeriod.build().year(-305));
    assertEquals(q.toString(), "Gregorian 1695-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-1000));
    assertEquals(q.toString(), "Gregorian 1000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1100));
    assertEquals(q.toString(), "Gregorian 3100-03-11 03:00:25.000 America/New_York");

    q = date.withZone(LOS_ANGELES);
    assertEquals(q.toString(), "Gregorian 2000-03-11 00:00:25.000 America/Los_Angeles");
  }

  @Test
  public void testISO8601Years() {
    ISO8601Date date = iso8601(MAR_11_2000, NEW_YORK);
    ISO8601Date q;

    assertEquals(date.toString(), "ISO8601 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "ISO8601 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "ISO8601 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testJapaneseYears() {
    JapaneseDate date = japanese(MAR_11_2000, NEW_YORK);
    JapaneseDate q;

    assertEquals(date.toString(), "Japanese 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Japanese 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Japanese 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testPersianYears() {
    PersianDate date = persian(MAR_11_2000, NEW_YORK);
    PersianDate q;

    assertEquals(date.toString(), "Persian 1378-12-21 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Persian 1379-12-21 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Persian 1373-12-21 03:00:25.000 America/New_York");
  }

  @Test
  public void testBuddhistYears() {
    BuddhistDate date = buddhist(MAR_11_2000, NEW_YORK);
    BuddhistDate q;

    assertEquals(date.toString(), "Buddhist 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Buddhist 2001-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().year(-5));
    assertEquals(q.toString(), "Buddhist 1995-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testMonths() {
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().month(1));
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
    PersianDate date = persian(MAR_11_2000, NEW_YORK);
    PersianDate q;

    assertEquals(date.toString(), "Persian 1378-12-21 03:00:25.000 America/New_York");

    // Oddities show up with time and non-gregorian calendars, since the timezone
    // rules are based on the gregorian calendar. So adding 1 month below shifts
    // to the next persian year, but in gregorian calendar it crosses a daylight
    // savings boundary for America/New_York, so the hour changes.
    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Persian 1379-01-21 03:00:25.000 America/New_York");

    // Monday, March 21, 2022 12:34:54 PM UTC
    date = persian(1647880496000L, NEW_YORK);
    assertEquals(date.toString(), "Persian 1401-01-01 12:34:56.000 America/New_York");

    q = date.add(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Persian 1401-02-01 12:34:56.000 America/New_York");

    q = date.add(TimePeriod.build().month(1.5)); // truncated to 1
    assertEquals(q.toString(), "Persian 1401-02-01 12:34:56.000 America/New_York");
  }

  @Test
  public void testDays() {
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(1));
    assertEquals(q.toString(), "Gregorian 2000-03-12 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(15));
    assertEquals(q.toString(), "Gregorian 2000-03-26 03:00:25.000 America/New_York");

    // Crossing DST boundary
    q = date.add(TimePeriod.build().day(30));
    assertEquals(q.toString(), "Gregorian 2000-04-10 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(-45));
    assertEquals(q.toString(), "Gregorian 2000-01-26 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(450));
    assertEquals(q.toString(), "Gregorian 2001-06-04 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().day(-3650));
    assertEquals(q.toString(), "Gregorian 1990-03-14 03:00:25.000 America/New_York");
  }

  @Test
  public void testWeeks() {
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");
    assertEquals(date.dayOfWeek(), DayOfWeek.SATURDAY);

    q = date.add(TimePeriod.build().week(1));
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
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.withZone(LOS_ANGELES);
    assertEquals(q.toString(), "Gregorian 2000-03-11 00:00:25.000 America/Los_Angeles");

    q = date.withZone(LONDON);
    assertEquals(q.toString(), "Gregorian 2000-03-11 08:00:25.000 Europe/London");
  }

  @Test
  public void testHours() {
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
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
  public void testMinute() {
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().minute(60));
    assertEquals(q.toString(), "Gregorian 2000-03-11 04:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().minute(5.505)); // truncated to 5
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:05:25.000 America/New_York");
  }

  @Test
  public void testMilliseconds() {
    GregorianDate date = gregorian(MAR_11_2000, NEW_YORK);
    GregorianDate q;

    assertEquals(date.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");

    q = date.add(TimePeriod.build().millis(60));
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:00:25.060 America/New_York");

    q = date.add(TimePeriod.build().millis(120.5)); // truncated to 120
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:00:25.120 America/New_York");

    // milliseconds roll over next day
    q = date.add(TimePeriod.build().millis(DAY * 2 + 120.5)); // truncated
    assertEquals(q.toString(), "Gregorian 2000-03-13 03:00:25.120 America/New_York");
  }

  @Test
  public void testMillisecondsInDay() {
    // May 17, 2026 10:00:00.000 AM
    GregorianDate utc = gregorian(1779012000000L, "UTC");
    // May 17, 2026 06:00:00.000 AM
    GregorianDate nyc = gregorian(1779012000000L, NEW_YORK);

    assertEquals(utc.modifiedJulianDay(), 2461178L);
    assertEquals(utc.millisecondsInDay(), 36000000L); // 10 hours in ms
    assertEquals(nyc.modifiedJulianDay(), 2461178L);
    assertEquals(nyc.millisecondsInDay(), 21600000L); // 6 hours in ms

    // May 17, 2026 03:00:00.000 AM
    utc = gregorian(1778986800000L, "UTC");
    // May 16, 2026 11:00:00.000 PM
    nyc = gregorian(1778986800000L, NEW_YORK);

    assertEquals(utc.modifiedJulianDay(), 2461178L);
    assertEquals(utc.millisecondsInDay(), 10800000L); // 3 hours in ms
    assertEquals(nyc.modifiedJulianDay(), 2461177L);
    assertEquals(nyc.millisecondsInDay(), 82800000L); // 23 hours in ms
  }

  @Test
  public void testYearsAddition1() {
    final long base = 1778986800000L;
    GregorianDate date;
    GregorianDate q;

    date = gregorian(base, "UTC");
    assertEquals(date.toString(), "Gregorian 2026-05-17 03:00:00.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-17 03:00:00.000 Etc/UTC");

    date = gregorian(base, NEW_YORK);
    assertEquals(date.toString(), "Gregorian 2026-05-16 23:00:00.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-16 23:00:00.000 America/New_York");

    q = date.add(TimePeriod.build().year(3));
    assertEquals(q.toString(), "Gregorian 2029-05-16 23:00:00.000 America/New_York");
  }

  @Test
  public void testYearsAddition2() {
    // May 17, 2026 10:00:00.000 AM
    long base = 1779012000000L;
    GregorianDate date;
    GregorianDate q;

    date = gregorian(base, "UTC");
    assertEquals(date.toString(), "Gregorian 2026-05-17 10:00:00.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-17 10:00:00.000 Etc/UTC");

    date = gregorian(base, NEW_YORK);
    assertEquals(date.toString(), "Gregorian 2026-05-17 06:00:00.000 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-17 06:00:00.000 America/New_York");

    // May 17, 2026 3:39:37.853 AM
    base = 1778989177853L;

    date = gregorian(base, "UTC");
    assertEquals(date.toString(), "Gregorian 2026-05-17 03:39:37.853 Etc/UTC");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-17 03:39:37.853 Etc/UTC");

    q = date.add(TimePeriod.build().year(2));
    assertEquals(q.toString(), "Gregorian 2028-05-17 03:39:37.853 Etc/UTC");

    q = date.add(TimePeriod.build().year(3));
    assertEquals(q.toString(), "Gregorian 2029-05-17 03:39:37.853 Etc/UTC");

    date = gregorian(base, "America/New_York");
    assertEquals(date.toString(), "Gregorian 2026-05-16 23:39:37.853 America/New_York");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-16 23:39:37.853 America/New_York");

    q = date.add(TimePeriod.build().year(2));
    assertEquals(q.toString(), "Gregorian 2028-05-16 23:39:37.853 America/New_York");

    q = date.add(TimePeriod.build().year(3));
    assertEquals(q.toString(), "Gregorian 2029-05-16 23:39:37.853 America/New_York");
  }

  @Test
  public void testYearsAddition3() {
    // May 17, 2026 4:00:00 PM
    final long base = 1779033600000L;
    GregorianDate date;
    GregorianDate q;

    date = gregorian(base, "UTC");
    assertEquals(date.toString(), "Gregorian 2026-05-17 16:00:00.000 Etc/UTC");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-17 16:00:00.000 Etc/UTC");

    date = gregorian(base, TOKYO);
    assertEquals(date.toString(), "Gregorian 2026-05-18 01:00:00.000 Asia/Tokyo");

    q = date.add(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2027-05-18 01:00:00.000 Asia/Tokyo");

    q = date.add(TimePeriod.build().year(2));
    assertEquals(q.toString(), "Gregorian 2028-05-18 01:00:00.000 Asia/Tokyo");

    q = date.add(TimePeriod.build().year(3));
    assertEquals(q.toString(), "Gregorian 2029-05-18 01:00:00.000 Asia/Tokyo");
  }

}
