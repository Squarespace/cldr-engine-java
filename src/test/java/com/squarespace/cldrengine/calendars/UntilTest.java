package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.calendars.DiffTestUtils.FEB_28_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.FEB_29_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.JAN_30_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_03_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_09_2025;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.NEW_YORK;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.UTC;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.VANCOUVER;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.dateString;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.dateTimeString;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.gregorian;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.periodString;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateDiffOptions;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/until.test.ts
 * (tag v1.15.0). Case tables copied verbatim.
 *
 * The upstream tests partially validate the CLDR result against the
 * @js-temporal/polyfill implementation; since the Java port has no Temporal
 * polyfill, those dual-engine asserts were replaced with the expected literals
 * captured from the published @phensley/cldr 1.15.0 oracle (node one-liners
 * against codegen/node_modules).
 *
 * 'temporal test262 dst-balancing-result' through 'temporal test262
 * sub-minute-offset' exercise the 23-25h roundResult hack; 'temporal bug 1' and
 * 'temporal bug 2 cldr result' exercise the same-day Fall-DST shortcut.
 * until-edge-case-scan-1/2.ts (property scans) are intentionally not ported.
 */
public class UntilTest {

  // Test case row for the 'date-only utc' table.
  private static class Case1 {
    final long epoch;
    final int day;
    final TimePeriodField[] fields;
    final String expDate;
    final String expDiff;

    Case1(long epoch, int day, TimePeriodField[] fields, String expDate, String expDiff) {
      this.epoch = epoch;
      this.day = day;
      this.fields = fields;
      this.expDate = expDate;
      this.expDiff = expDiff;
    }
  }

  // Test case row for the 'date time utc' table.
  private static class Case2 {
    final long epoch;
    final TimePeriod added;
    final String expDate;
    final String expDiff;

    Case2(long epoch, TimePeriod added, String expDate, String expDiff) {
      this.epoch = epoch;
      this.added = added;
      this.expDate = expDate;
      this.expDiff = expDiff;
    }
  }

  // Test case row for the 'rolldown utc' table.
  private static class Case3 {
    final long epoch;
    final TimePeriod added;
    final TimePeriodField[] fields;
    final String expDate;
    final String expDiff;

    Case3(long epoch, TimePeriod added, TimePeriodField[] fields, String expDate, String expDiff) {
      this.epoch = epoch;
      this.added = added;
      this.fields = fields;
      this.expDate = expDate;
      this.expDiff = expDiff;
    }
  }

  @Test
  public void test262DstMonthDayBoundary() {
    // test/intl402/Temporal/ZonedDateTime/prototype/until/dst-month-day-boundary.js
    untilEpochs(957258000000L, 954669600000L, "America/Vancouver", "-29d -23H");
  }

  @Test
  public void test262DstBalancingResult() {
    // test/intl402/Temporal/ZonedDateTime/prototype/until/dst-balancing-result.js
    // The 24H in both balancing/rounding results exercises the 23-25h
    // roundResult hack: the elapsed span includes the Spring DST shift.
    untilEpochs(949132800000L, 972889200000L, "America/Vancouver", "9m 24H");
  }

  @Test
  public void test262DstRoundingResult1() {
    // test/intl402/Temporal/ZonedDateTime/prototype/until/dst-rounding-result.js
    untilEpochs(949132800000L, 972889200000L, "America/Vancouver", "9m 24H");
  }

  @Test
  public void test262DstRoundingResult2() {
    untilEpochs(951991200000L, 956005200000L, "America/Vancouver", "1m 15d 12H");
  }

  @Test
  public void test262SubMinuteOffset() {
    // test/intl402/Temporal/ZonedDateTime/prototype/until/sub-minute-offset.js
    // Same calendar day (1970-01-01) in Africa/Monrovia (-00:44:30 LMT):
    // exercises the same-day pure time-of-day difference shortcut.
    untilEpochs(0L, 5340000L, "Africa/Monrovia", "1H 29M");
  }

  @Test
  public void testApiCommentExamples() {
    long epoch1 = 1706634000000L; // 2024-01-30
    long epoch2 = 1709485200000L; // 2024-03-03

    // "2024-01-30".until("2024-03-03") == "1m 3d"
    assertEquals(
        periodString(gregorian(epoch1, NEW_YORK).until(gregorian(epoch2, NEW_YORK), options())),
        "1m 3d");
    // Asymmetry with the reverse direction (documented in the api docs).
    assertEquals(
        periodString(gregorian(epoch2, NEW_YORK).until(gregorian(epoch1, NEW_YORK), options())),
        "-1m -4d");
  }

  @Test
  public void testEdgeCase1() {
    // MAR_09_2025 America/New_York + { year: 1, month: 6, week: 7, day: 4,
    // hour: 2, minute: 3, second: 2, millis: 5 }
    GregorianDate c1 = gregorian(MAR_09_2025, "America/New_York");
    GregorianDate c2 = c1.add(TimePeriod.build().year(1).month(6).week(7).day(4).hour(2).minute(3).second(2).millis(5));
    assertEquals(dateTimeString(c2, true, true), "2026-11-01 03:02:02.005-05:00");
    assertEquals(periodString(c1.until(c2, options())), "1y 7m 23d 2H 3M 2S 5ms");
  }

  @Test
  public void testEdgeCase2() {
    assertEquals(
        periodString(gregorian(1742205000000L, VANCOUVER).until(gregorian(1741517400000L, VANCOUVER), options())),
        "-8d");
  }

  @Test
  public void testTemporalBug1() {
    long epoch1 = 1762070460000L;
    long epoch2 = 1762074000000L;

    // Filed bug 1:
    // https://github.com/tc39/proposal-temporal/issues/3141
    // https://github.com/js-temporal/temporal-polyfill/issues/347
    //
    // Both timestamps fall on the same calendar day 2025-11-02 in Vancouver
    // (01:01-07:00 before the Fall DST shift and 01:00-08:00 after); the
    // polyfill throws a RangeError ("mixed-sign values not allowed as duration
    // fields") but the CLDR engine returns a pure time-of-day difference.
    assertEquals(periodString(gregorian(epoch1, VANCOUVER).until(gregorian(epoch2, VANCOUVER), options())), "59M");
    assertEquals(periodString(gregorian(epoch2, VANCOUVER).until(gregorian(epoch1, VANCOUVER), options())), "-59M");
  }

  @Test
  public void testTemporalBug2CldrResult() {
    String zone = "America/New_York";

    // 2025-11-02 04:00:00 UTC
    // 2025-11-02 00:00:00-0400 local, midnight before DST shift.
    long midnight = 1762056000000L;

    // 2025-11-02 05:59:00 UTC
    // 2025-11-02 01:59:00-04:00 local, first 1:59 before DST shift.
    long first0159 = 1762063140000L;

    // 2025-11-02 06:59:00 UTC
    // 2025-11-02 01:59:00-05:00 local, second 1:59 after DST shift.
    long second0159 = 1762066740000L;

    // 2025-11-02 23:00:00 UTC
    // 2025-11-02 18:00:00-05:00 local, evening in day of DST shift.
    long evening = 1762124400000L;

    String result;

    result = untilString(midnight, first0159, zone);
    assertEquals(result, "1H 59M");

    result = untilString(first0159, midnight, zone);
    assertEquals(result, "-1H -59M");

    result = untilString(midnight, second0159, zone);
    assertEquals(result, "2H 59M");

    result = untilString(second0159, midnight, zone);
    assertEquals(result, "-2H -59M");

    result = untilString(first0159, second0159, zone);
    assertEquals(result, "1H");

    result = untilString(second0159, first0159, zone);
    assertEquals(result, "-1H");

    result = untilString(first0159, evening, zone);
    assertEquals(result, "17H 1M");

    result = untilString(evening, first0159, zone);
    assertEquals(result, "-17H -1M");

    result = untilString(second0159, evening, zone);
    assertEquals(result, "16H 1M");

    result = untilString(evening, second0159, zone);
    assertEquals(result, "-16H -1M");
  }

  @Test
  public void testDateOnlyUtc() {
    // Format for each test case below:
    // [date1, days-added, fields, date2, duration-since]
    //
    //  date1 + days-added = date2
    //  date1.since(date2, { fields }) == duration-since
    //
    // Empty duration '' means all fields in the result were zero.
    //
    Case1[] cases = new Case1[] {
      // POSITIVE DURATIONS: date1.until(date2) where date1 < date2

      new Case1(JAN_30_2024, 0, fields(), "2024-01-30", ""),

      new Case1(JAN_30_2024, 28, fields(), "2024-02-27", "28d"),
      new Case1(JAN_30_2024, 29, fields(), "2024-02-28", "29d"),
      new Case1(JAN_30_2024, 30, fields(), "2024-02-29", "30d"),
      new Case1(JAN_30_2024, 31, fields(), "2024-03-01", "1m 1d"),
      new Case1(JAN_30_2024, 32, fields(), "2024-03-02", "1m 2d"),
      new Case1(JAN_30_2024, 33, fields(), "2024-03-03", "1m 3d"),

      new Case1(FEB_28_2024, 28, fields(), "2024-03-27", "28d"),
      new Case1(FEB_28_2024, 29, fields(), "2024-03-28", "1m"),
      new Case1(FEB_28_2024, 30, fields(), "2024-03-29", "1m 1d"),
      new Case1(FEB_28_2024, 31, fields(), "2024-03-30", "1m 2d"),
      new Case1(FEB_28_2024, 32, fields(), "2024-03-31", "1m 3d"),
      new Case1(FEB_28_2024, 33, fields(), "2024-04-01", "1m 4d"),

      new Case1(FEB_29_2024, 28, fields(), "2024-03-28", "28d"),
      new Case1(FEB_29_2024, 29, fields(), "2024-03-29", "1m"),
      new Case1(FEB_29_2024, 30, fields(), "2024-03-30", "1m 1d"),
      new Case1(FEB_29_2024, 31, fields(), "2024-03-31", "1m 2d"),
      new Case1(FEB_29_2024, 32, fields(), "2024-04-01", "1m 3d"),
      new Case1(FEB_29_2024, 33, fields(), "2024-04-02", "1m 4d"),

      new Case1(FEB_29_2024, 28, fields(TimePeriodField.DAY), "2024-03-28", "28d"),
      new Case1(FEB_29_2024, 29, fields(TimePeriodField.DAY), "2024-03-29", "29d"),
      new Case1(FEB_29_2024, 30, fields(TimePeriodField.DAY), "2024-03-30", "30d"),
      new Case1(FEB_29_2024, 31, fields(TimePeriodField.DAY), "2024-03-31", "31d"),
      new Case1(FEB_29_2024, 32, fields(TimePeriodField.DAY), "2024-04-01", "32d"),
      new Case1(FEB_29_2024, 33, fields(TimePeriodField.DAY), "2024-04-02", "33d"),

      // NEGATIVE DURATIONS: date1.until(date2) where date1 > date2

      new Case1(FEB_28_2024, -33, fields(), "2024-01-26", "-1m -2d"),

      new Case1(MAR_03_2024, -28, fields(), "2024-02-04", "-28d"),
      new Case1(MAR_03_2024, -29, fields(), "2024-02-03", "-1m"),
      new Case1(MAR_03_2024, -30, fields(), "2024-02-02", "-1m -1d"),
      new Case1(MAR_03_2024, -31, fields(), "2024-02-01", "-1m -2d"),
      new Case1(MAR_03_2024, -32, fields(), "2024-01-31", "-1m -3d"),
      new Case1(MAR_03_2024, -33, fields(), "2024-01-30", "-1m -4d"),

      // ROLLDOWNS

      new Case1(FEB_29_2024, 33, fields(TimePeriodField.YEAR, TimePeriodField.MONTH), "2024-04-02", "1m"),
      new Case1(FEB_29_2024, 33, fields(TimePeriodField.MONTH), "2024-04-02", "1m"),
      new Case1(FEB_29_2024, 33, fields(TimePeriodField.MONTH, TimePeriodField.DAY), "2024-04-02", "1m 4d"),
      new Case1(FEB_29_2024, 33, fields(TimePeriodField.DAY), "2024-04-02", "33d"),

      new Case1(FEB_29_2024, 400, fields(TimePeriodField.YEAR, TimePeriodField.MONTH), "2025-04-04", "1y 1m"),
      new Case1(FEB_29_2024, 400, fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY), "2025-04-04", "1y 1m 6d"),
      new Case1(FEB_29_2024, 400, fields(TimePeriodField.MONTH, TimePeriodField.DAY), "2025-04-04", "13m 6d"),
      new Case1(FEB_29_2024, 400, fields(TimePeriodField.DAY), "2025-04-04", "400d"),
    };

    for (Case1 c : cases) {
      GregorianDate base = gregorian(c.epoch, "UTC");
      GregorianDate date = base.add(TimePeriod.build().day(c.day));
      assertEquals(dateString(date), c.expDate, "add " + c.day + " days");
      assertEquals(periodString(base.until(date, options().fields(asList(c.fields)))), c.expDiff);
    }
  }

  @Test
  public void testDateTimeUtc() {
    // Format for each test case below:
    // [date1, days-added, fields, date2, duration-since]
    //
    //  date1 + days-added = date2
    //  date1.since(date2, { fields }) == duration-since
    //
    // Empty duration '' means all fields in the result were zero.
    //
    Case2[] cases = new Case2[] {
      // POSITIVE DURATIONS: date1.until(date2) where date1 < date2

      new Case2(JAN_30_2024, day(28).hour(1), "2024-02-27 14:30:00", "28d 1H"),
      new Case2(JAN_30_2024, day(29).hour(2), "2024-02-28 15:30:00", "29d 2H"),
      new Case2(JAN_30_2024, day(30).hour(2), "2024-02-29 15:30:00", "30d 2H"),
      new Case2(JAN_30_2024, day(31).hour(1), "2024-03-01 14:30:00", "1m 1d 1H"),
      new Case2(JAN_30_2024, day(32), "2024-03-02 13:30:00", "1m 2d"),
      new Case2(JAN_30_2024, day(33), "2024-03-03 13:30:00", "1m 3d"),

      new Case2(JAN_30_2024, day(30).hour(2).minute(12).second(34).millis(567), "2024-02-29 15:42:34.567", "30d 2H 12M 34S 567ms"),

      new Case2(FEB_28_2024, day(28).hour(3), "2024-03-27 16:30:00", "28d 3H"),
      new Case2(FEB_28_2024, day(29).hour(1), "2024-03-28 14:30:00", "1m 1H"),
      new Case2(FEB_28_2024, day(30), "2024-03-29 13:30:00", "1m 1d"),
      new Case2(FEB_28_2024, day(31), "2024-03-30 13:30:00", "1m 2d"),
      new Case2(FEB_28_2024, day(32), "2024-03-31 13:30:00", "1m 3d"),
      new Case2(FEB_28_2024, day(33), "2024-04-01 13:30:00", "1m 4d"),

      new Case2(FEB_29_2024, day(28), "2024-03-28 13:30:00", "28d"),
      new Case2(FEB_29_2024, day(29), "2024-03-29 13:30:00", "1m"),
      new Case2(FEB_29_2024, day(30), "2024-03-30 13:30:00", "1m 1d"),
      new Case2(FEB_29_2024, day(31), "2024-03-31 13:30:00", "1m 2d"),
      new Case2(FEB_29_2024, day(32), "2024-04-01 13:30:00", "1m 3d"),
      new Case2(FEB_29_2024, day(33), "2024-04-02 13:30:00", "1m 4d"),

      // NEGATIVE DURATIONS: date1.until(date2) where date1 > date2

      new Case2(FEB_28_2024, day(-33), "2024-01-26 13:30:00", "-1m -2d"),

      new Case2(MAR_03_2024, day(-28), "2024-02-04 13:30:00", "-28d"),
      new Case2(MAR_03_2024, day(-29), "2024-02-03 13:30:00", "-1m"),
      new Case2(MAR_03_2024, day(-30), "2024-02-02 13:30:00", "-1m -1d"),
      new Case2(MAR_03_2024, day(-31), "2024-02-01 13:30:00", "-1m -2d"),
      new Case2(MAR_03_2024, day(-32), "2024-01-31 13:30:00", "-1m -3d"),
      new Case2(MAR_03_2024, day(-33), "2024-01-30 13:30:00", "-1m -4d"),
    };

    for (Case2 c : cases) {
      GregorianDate cldr1 = gregorian(c.epoch, "UTC");
      GregorianDate cldr2 = cldr1.add(c.added);
      assertEquals(dateTimeString(cldr2, false, true), c.expDate, "add " + c.added);
      assertEquals(periodString(cldr1.until(cldr2, options())), c.expDiff);
    }
  }

  @Test
  public void testRolldownUtc() {
    // Format for each test case below:
    // [date1, days-added, fields, date2, duration-since]
    //
    //  date1 + days-added = date2
    //  date1.since(date2, { fields }) == duration-since
    //
    // Empty duration '' means all fields in the result were zero.
    //
    Case3[] cases = new Case3[] {
      // POSITIVE DURATIONS: date1.until(date2) where date1 < date2

      new Case3(JAN_30_2024, day(28).hour(1), fields(), "2024-02-27 14:30:00", "28d 1H"),
      new Case3(JAN_30_2024, day(29).hour(2), fields(), "2024-02-28 15:30:00", "29d 2H"),
      new Case3(JAN_30_2024, day(30).hour(2), fields(), "2024-02-29 15:30:00", "30d 2H"),
      new Case3(JAN_30_2024, day(31).hour(1), fields(), "2024-03-01 14:30:00", "1m 1d 1H"),
      new Case3(JAN_30_2024, day(32), fields(), "2024-03-02 13:30:00", "1m 2d"),
      new Case3(JAN_30_2024, day(33), fields(), "2024-03-03 13:30:00", "1m 3d"),

      new Case3(JAN_30_2024, day(30).hour(2).minute(12).second(34).millis(567), fields(), "2024-02-29 15:42:34.567", "30d 2H 12M 34S 567ms"),

      new Case3(FEB_28_2024, day(28).hour(3), fields(), "2024-03-27 16:30:00", "28d 3H"),
      new Case3(FEB_28_2024, day(29).hour(1), fields(), "2024-03-28 14:30:00", "1m 1H"),
      new Case3(FEB_28_2024, day(30), fields(), "2024-03-29 13:30:00", "1m 1d"),
      new Case3(FEB_28_2024, day(31), fields(), "2024-03-30 13:30:00", "1m 2d"),
      new Case3(FEB_28_2024, day(32), fields(), "2024-03-31 13:30:00", "1m 3d"),
      new Case3(FEB_28_2024, day(33), fields(), "2024-04-01 13:30:00", "1m 4d"),

      new Case3(FEB_29_2024, day(28), fields(), "2024-03-28 13:30:00", "28d"),
      new Case3(FEB_29_2024, day(29), fields(), "2024-03-29 13:30:00", "1m"),
      new Case3(FEB_29_2024, day(30), fields(), "2024-03-30 13:30:00", "1m 1d"),
      new Case3(FEB_29_2024, day(31), fields(), "2024-03-31 13:30:00", "1m 2d"),
      new Case3(FEB_29_2024, day(32), fields(), "2024-04-01 13:30:00", "1m 3d"),
      new Case3(FEB_29_2024, day(33), fields(), "2024-04-02 13:30:00", "1m 4d"),

      new Case3(FEB_29_2024, day(28), fields(TimePeriodField.DAY), "2024-03-28 13:30:00", "28d"),
      new Case3(FEB_29_2024, day(29), fields(TimePeriodField.DAY), "2024-03-29 13:30:00", "29d"),
      new Case3(FEB_29_2024, day(30), fields(TimePeriodField.DAY), "2024-03-30 13:30:00", "30d"),
      new Case3(FEB_29_2024, day(31), fields(TimePeriodField.DAY), "2024-03-31 13:30:00", "31d"),
      new Case3(FEB_29_2024, day(32), fields(TimePeriodField.DAY), "2024-04-01 13:30:00", "32d"),
      new Case3(FEB_29_2024, day(33), fields(TimePeriodField.DAY), "2024-04-02 13:30:00", "33d"),

      // NEGATIVE DURATIONS: date1.until(date2) where date1 > date2

      new Case3(FEB_28_2024, day(-33), fields(), "2024-01-26 13:30:00", "-1m -2d"),

      new Case3(MAR_03_2024, day(-28), fields(), "2024-02-04 13:30:00", "-28d"),
      new Case3(MAR_03_2024, day(-29), fields(), "2024-02-03 13:30:00", "-1m"),
      new Case3(MAR_03_2024, day(-30), fields(), "2024-02-02 13:30:00", "-1m -1d"),
      new Case3(MAR_03_2024, day(-31), fields(), "2024-02-01 13:30:00", "-1m -2d"),
      new Case3(MAR_03_2024, day(-32), fields(), "2024-01-31 13:30:00", "-1m -3d"),
      new Case3(MAR_03_2024, day(-33), fields(), "2024-01-30 13:30:00", "-1m -4d"),

      // ROLLDOWNS

      new Case3(FEB_29_2024, day(33), fields(TimePeriodField.YEAR, TimePeriodField.MONTH), "2024-04-02 13:30:00", "1m"),
      new Case3(FEB_29_2024, day(33), fields(TimePeriodField.MONTH), "2024-04-02 13:30:00", "1m"),
      new Case3(FEB_29_2024, day(33), fields(TimePeriodField.MONTH, TimePeriodField.DAY), "2024-04-02 13:30:00", "1m 4d"),
      new Case3(FEB_29_2024, day(33), fields(TimePeriodField.DAY), "2024-04-02 13:30:00", "33d"),

      new Case3(FEB_29_2024, day(400), fields(TimePeriodField.YEAR, TimePeriodField.MONTH), "2025-04-04 13:30:00", "1y 1m"),
      new Case3(FEB_29_2024, day(400), fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY), "2025-04-04 13:30:00", "1y 1m 6d"),
      new Case3(FEB_29_2024, day(400), fields(TimePeriodField.MONTH, TimePeriodField.DAY), "2025-04-04 13:30:00", "13m 6d"),
      new Case3(FEB_29_2024, day(400), fields(TimePeriodField.DAY), "2025-04-04 13:30:00", "400d"),
    };

    for (Case3 c : cases) {
      GregorianDate cldr1 = gregorian(c.epoch, "UTC");
      GregorianDate cldr2 = cldr1.add(c.added);
      assertEquals(dateTimeString(cldr2, false, true), c.expDate, "add " + c.added);
      assertEquals(
          periodString(cldr1.until(cldr2, options().fields(asList(c.fields)))),
          c.expDiff);
    }
  }

  @Test
  public void testRolldownCombinations() {
    // One combined case: JAN_30_2024 + { day: 28, hour: 1, minute: 22,
    // second: 33, millis: 444 }, exercised against many field subsets.
    long epoch = JAN_30_2024;
    TimePeriod added = day(28).hour(1).minute(22).second(33).millis(444);
    String expDate = "2024-02-27 14:52:33.444";

    Object[][] subs = new Object[][] {
      new Object[] { fields(), "28d 1H 22M 33S 444ms" },
      new Object[] { fields(TimePeriodField.DAY), "28d" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.HOUR), "28d 1H" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.MINUTE), "28d 1H 22M" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND), "28d 1H 22M 33S" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS), "28d 1H 22M 33S 444ms" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.SECOND, TimePeriodField.MILLIS), "28d 1H 1353S 444ms" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.MILLIS), "28d 1H 1353444ms" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS), "28d 82M 33S 444ms" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.SECOND, TimePeriodField.MILLIS), "28d 4953S 444ms" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.MILLIS), "28d 4953444ms" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.MINUTE), "28d 82M" },
      new Object[] { fields(TimePeriodField.DAY, TimePeriodField.MINUTE, TimePeriodField.SECOND), "28d 82M 33S" },

      new Object[] { fields(TimePeriodField.HOUR), "673H" },
      new Object[] { fields(TimePeriodField.HOUR, TimePeriodField.MINUTE), "673H 22M" },
      new Object[] { fields(TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND), "673H 22M 33S" },
      new Object[] { fields(TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS), "673H 22M 33S 444ms" },
      new Object[] { fields(TimePeriodField.HOUR, TimePeriodField.SECOND, TimePeriodField.MILLIS), "673H 1353S 444ms" },
      new Object[] { fields(TimePeriodField.HOUR, TimePeriodField.MILLIS), "673H 1353444ms" },
      new Object[] { fields(TimePeriodField.HOUR, TimePeriodField.SECOND), "673H 1353S" },

      new Object[] { fields(TimePeriodField.MINUTE), "40402M" },
      new Object[] { fields(TimePeriodField.MINUTE, TimePeriodField.SECOND), "40402M 33S" },
      new Object[] { fields(TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS), "40402M 33S 444ms" },
      new Object[] { fields(TimePeriodField.MINUTE, TimePeriodField.MILLIS), "40402M 33444ms" },
    };

    GregorianDate cldr1 = gregorian(epoch, "UTC");
    GregorianDate cldr2 = cldr1.add(added);
    assertEquals(dateTimeString(cldr2, false, true), expDate);

    for (Object[] sub : subs) {
      @SuppressWarnings("unchecked")
      TimePeriodField[] fs = (TimePeriodField[]) sub[0];
      String duration = (String) sub[1];
      assertEquals(
          periodString(cldr1.until(cldr2, options().fields(asList(fs)))),
          duration);
    }
  }

  @Test
  public void testWeeks() {
    // Ensure the multiple ways of requesting WEEKS, either by requesting
    // the field explicitly or enabling the 'includeWeeks' option.
    CalendarDate end;
    TimePeriod until;
    CalendarDate start = gregorian(JAN_30_2024, "America/New_York");

    end = start.add(TimePeriod.build().day(21));
    assertEquals(dateTimeString(end, false, true), "2024-02-20 08:30:00");
    until = start.until(end, options().fields(Collections.singletonList(TimePeriodField.WEEK)));
    assertEquals(periodString(until), "3w");

    end = start.add(TimePeriod.build().day(21));
    assertEquals(dateTimeString(end, false, true), "2024-02-20 08:30:00");
    until = start.until(end, options().includeWeeks(true));
    assertEquals(periodString(until), "3w");
  }

  @Test
  public void testCoverageAdjustDays() {
    // Cases to ensure adjustDays function coverage
    CalendarDate end;
    TimePeriod until;
    CalendarDate start = gregorian(FEB_29_2024, "America/New_York");

    long[] hours = new long[] { -29L * 24, -30L * 24, -40L * 24, -500L, -524L, -695L };
    String[] expected = new String[] { "-29d", "-30d", "-1m -9d", "-20d -20H", "-21d -20H", "-28d -23H" };

    for (int i = 0; i < hours.length; i++) {
      long hour = hours[i];
      end = start.add(TimePeriod.build().hour(hour));
      until = start.until(end, options());
      assertEquals(periodString(until), expected[i]);
    }
  }

  @Test
  public void testRollupFractions() {
    // These cases perform a "rollup" which rolls smaller fields into larger ones.
    // This is only really useful for relative time formatting where we express things like
    // "In 2.5 years", and the caller may want control over fractional values and rounding.
    Case3[] cases = new Case3[] {
      // POSITIVE DURATIONS: date1.until(date2) where date1 < date2

      new Case3(JAN_30_2024, year(1).month(3), fields(TimePeriodField.YEAR), "2025-04-30 13:30:00", "1.25y"),
      new Case3(JAN_30_2024, year(1).month(3), fields(TimePeriodField.MONTH), "2025-04-30 13:30:00", "15m"),
      new Case3(JAN_30_2024, year(1).month(3).day(91), fields(TimePeriodField.MONTH), "2025-07-30 13:30:00", "18m"),
      new Case3(JAN_30_2024, year(1).month(3).day(91), fields(TimePeriodField.YEAR), "2025-07-30 13:30:00", "1.5y"),
      new Case3(JAN_30_2024, year(1).month(3).day(98), fields(TimePeriodField.MONTH, TimePeriodField.WEEK), "2025-08-06 13:30:00", "18m 1w"),
      new Case3(JAN_30_2024, year(1).month(3).day(99), fields(TimePeriodField.MONTH, TimePeriodField.WEEK, TimePeriodField.DAY), "2025-08-07 13:30:00", "18m 1w 1d"),
      new Case3(JAN_30_2024, year(1).month(3).day(99), fields(TimePeriodField.MONTH, TimePeriodField.DAY), "2025-08-07 13:30:00", "18m 8d"),
      new Case3(JAN_30_2024, year(1).month(3).day(99), fields(TimePeriodField.MONTH, TimePeriodField.HOUR), "2025-08-07 13:30:00", "18m 192H"),
      new Case3(JAN_30_2024, year(1).month(3).day(99), fields(TimePeriodField.MONTH, TimePeriodField.WEEK), "2025-08-07 13:30:00", "18m 1.143w"),

      new Case3(JAN_30_2024, month(1).hour(15), fields(TimePeriodField.DAY), "2024-03-01 04:30:00", "30.625d"),
      new Case3(JAN_30_2024, month(3), fields(TimePeriodField.YEAR), "2024-04-30 13:30:00", "0.25y"),
    };

    for (Case3 c : cases) {
      GregorianDate base = gregorian(c.epoch, "UTC");
      GregorianDate date = base.add(c.added);
      assertEquals(dateTimeString(date, false, true), c.expDate, "add " + c.added);
      assertEquals(
          periodString(base.until(date, options().fields(asList(c.fields)).rollupFractional(true))),
          c.expDiff);
    }
  }

  private static String untilString(long epoch1, long epoch2, String zone) {
    GregorianDate cldr1 = gregorian(epoch1, zone);
    GregorianDate cldr2 = gregorian(epoch2, zone);
    return periodString(cldr1.until(cldr2, options()));
  }

  private static void untilEpochs(long epoch1, long epoch2, String zone, String expected) {
    assertEquals(untilString(epoch1, epoch2, zone), expected);
  }

  private static TimePeriod year(double v) {
    return TimePeriod.build().year(v);
  }

  private static TimePeriod month(double v) {
    return TimePeriod.build().month(v);
  }

  private static TimePeriod day(double v) {
    return TimePeriod.build().day(v);
  }

  private static TimePeriodField[] fields(TimePeriodField... fs) {
    return fs;
  }

  private static List<TimePeriodField> asList(TimePeriodField[] fs) {
    return fs.length == 0 ? Collections.<TimePeriodField>emptyList() : Arrays.asList(fs);
  }

  private static DateDiffOptions options() {
    return DateDiffOptions.build();
  }

}
