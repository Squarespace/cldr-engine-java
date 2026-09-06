package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.calendars.DiffTestUtils.FEB_28_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.FEB_29_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.JAN_30_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_03_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_10_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_30_2024;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.NEW_YORK;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.PARIS;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.TOKYO;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.UTC;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.dateString;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.gregorian;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.periodString;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.DateDiffOptions;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/since.test.ts
 * (tag v1.15.0). Case tables copied verbatim.
 *
 * The upstream tests compare the CLDR result against the
 * @js-temporal/polyfill (durationToPeriod / TemporalFormatter) when no rollup
 * fields are defined; those dual-engine asserts are not portable and are
 * dropped here (the CLDR-side literal assertions cover the behavior).
 */
public class SinceTest {

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

  private static class Case2 {
    final long epoch;
    final TimePeriod added;
    final TimePeriodField[] fields;
    final String zone;
    final String expDate;
    final String expDiff;

    Case2(long epoch, TimePeriod added, String zone, String expDate, String expDiff) {
      this.epoch = epoch;
      this.added = added;
      this.fields = new TimePeriodField[0];
      this.zone = zone;
      this.expDate = expDate;
      this.expDiff = expDiff;
    }

    Case2(long epoch, TimePeriod added, TimePeriodField[] fields, String zone, String expDate, String expDiff) {
      this.epoch = epoch;
      this.added = added;
      this.fields = fields;
      this.zone = zone;
      this.expDate = expDate;
      this.expDiff = expDiff;
    }
  }

  @Test
  public void testApiCommentExamples() {
    long epoch1 = 1706634000000L; // 2024-01-30
    long epoch2 = 1709485200000L; // 2024-03-03

    // "2024-03-03".since("2024-01-30") == "1m 4d"
    assertEquals(
        periodString(gregorian(epoch2, NEW_YORK).since(gregorian(epoch1, NEW_YORK), options())),
        "1m 4d");
    // "2024-01-30".since("2024-03-03") == "-1m -3d"
    assertEquals(
        periodString(gregorian(epoch1, NEW_YORK).since(gregorian(epoch2, NEW_YORK), options())),
        "-1m -3d");
  }

  @Test
  public void testDates() {
    // Format of each test case below:
    // [date1, days-added, fields, date2, duration-since]
    //
    //  date1 + days-added = date2
    //  date1.since(date2, { fields }) == duration-since
    //
    // Empty duration '' means all fields in the result were zero.
    //
    Case1[] cases = new Case1[] {
      // POSITIVE DURATIONS: date1.since(date2) where date1 > date2

      new Case1(MAR_30_2024, -27, fields(), "2024-03-03", "27d"),
      new Case1(MAR_30_2024, -28, fields(), "2024-03-02", "28d"),
      new Case1(MAR_30_2024, -29, fields(), "2024-03-01", "29d"),
      new Case1(MAR_30_2024, -30, fields(), "2024-02-29", "1m"),
      new Case1(MAR_30_2024, -31, fields(), "2024-02-28", "1m 1d"),
      new Case1(MAR_30_2024, -32, fields(), "2024-02-27", "1m 2d"),
      new Case1(MAR_30_2024, -33, fields(), "2024-02-26", "1m 3d"),

      new Case1(MAR_03_2024, -27, fields(), "2024-02-05", "27d"),
      new Case1(MAR_03_2024, -28, fields(), "2024-02-04", "28d"),
      new Case1(MAR_03_2024, -29, fields(), "2024-02-03", "1m"),
      new Case1(MAR_03_2024, -30, fields(), "2024-02-02", "1m 1d"),
      new Case1(MAR_03_2024, -31, fields(), "2024-02-01", "1m 2d"),
      new Case1(MAR_03_2024, -32, fields(), "2024-01-31", "1m 3d"),
      new Case1(MAR_03_2024, -33, fields(), "2024-01-30", "1m 4d"),

      // NEGATIVE DURATIONS: date1.since(date2) where date1 < date2

      new Case1(JAN_30_2024, 28, fields(), "2024-02-27", "-28d"),
      new Case1(JAN_30_2024, 29, fields(), "2024-02-28", "-29d"),
      new Case1(JAN_30_2024, 30, fields(), "2024-02-29", "-30d"),
      new Case1(JAN_30_2024, 31, fields(), "2024-03-01", "-1m -1d"),
      new Case1(JAN_30_2024, 32, fields(), "2024-03-02", "-1m -2d"),
      new Case1(JAN_30_2024, 33, fields(), "2024-03-03", "-1m -3d"),

      new Case1(FEB_28_2024, 28, fields(), "2024-03-27", "-28d"),
      new Case1(FEB_28_2024, 29, fields(), "2024-03-28", "-1m"),
      new Case1(FEB_28_2024, 30, fields(), "2024-03-29", "-1m -1d"),
      new Case1(FEB_28_2024, 31, fields(), "2024-03-30", "-1m -2d"),
      new Case1(FEB_28_2024, 32, fields(), "2024-03-31", "-1m -3d"),
      new Case1(FEB_28_2024, 33, fields(), "2024-04-01", "-1m -4d"),

      new Case1(FEB_29_2024, 28, fields(), "2024-03-28", "-28d"),
      new Case1(FEB_29_2024, 29, fields(), "2024-03-29", "-1m"),
      new Case1(FEB_29_2024, 30, fields(), "2024-03-30", "-1m -1d"),
      new Case1(FEB_29_2024, 31, fields(), "2024-03-31", "-1m -2d"),
      new Case1(FEB_29_2024, 32, fields(), "2024-04-01", "-1m -3d"),
      new Case1(FEB_29_2024, 33, fields(), "2024-04-02", "-1m -4d"),

      // ROLLDOWNS

      new Case1(FEB_29_2024, -400, fields(TimePeriodField.YEAR), "2023-01-25", "1y"),
      new Case1(FEB_29_2024, -400, fields(TimePeriodField.YEAR, TimePeriodField.MONTH), "2023-01-25", "1y 1m"),
      new Case1(FEB_29_2024, -400, fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY), "2023-01-25", "1y 1m 4d"),
      new Case1(FEB_29_2024, -400, fields(TimePeriodField.MONTH), "2023-01-25", "13m"),
      new Case1(FEB_29_2024, -400, fields(TimePeriodField.MONTH, TimePeriodField.DAY), "2023-01-25", "13m 4d"),
      new Case1(FEB_29_2024, -400, fields(TimePeriodField.DAY), "2023-01-25", "400d"),

      new Case1(FEB_29_2024, -33, fields(TimePeriodField.YEAR), "2024-01-27", ""),
      new Case1(FEB_29_2024, -33, fields(TimePeriodField.YEAR, TimePeriodField.MONTH), "2024-01-27", "1m"),
      new Case1(FEB_29_2024, -33, fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY), "2024-01-27", "1m 2d"),
      new Case1(FEB_29_2024, -33, fields(TimePeriodField.MONTH), "2024-01-27", "1m"),
      new Case1(FEB_29_2024, -33, fields(TimePeriodField.MONTH, TimePeriodField.DAY), "2024-01-27", "1m 2d"),
      new Case1(FEB_29_2024, -33, fields(TimePeriodField.DAY), "2024-01-27", "33d"),
    };

    String[] zones = new String[] { NEW_YORK, PARIS, TOKYO };

    for (Case1 c : cases) {
      for (String zone : zones) {
        GregorianDate cldr1 = gregorian(c.epoch, zone);
        GregorianDate cldr2 = cldr1.add(TimePeriod.build().day(c.day));
        assertEquals(dateString(cldr2), c.expDate, "days " + c.day);
        assertEquals(
            periodString(cldr1.since(cldr2, options().fields(asList(c.fields)))),
            c.expDiff);
      }
    }
  }

  @Test
  public void testDatesAndTimes() {
    // Two cases for time adjustment:
    //
    // date1 < date2; date1.since(date2) (result negative)
    //
    // [JAN_30_2024, 29, [], '2024-02-28', '-29d'],
    //
    // 2024-01-30 8:30am  2024-02-28 8:30am  == -29d
    // 2024-01-30 8:30am  2024-02-28 6:30am  == -28d -22H
    // 2024-01-30 8:30am  2024-02-28 10:30am == -29d -2H
    //
    // date1 > date2; date1.since(date2) (result positive)
    //
    // [MAR_30_2024, -29, [], '2024-03-01', '29d'],
    //
    // 2024-03-30 8:30am  2024-03-01 8:30am  == 29d
    // 2024-03-30 8:30am  2024-03-01 6:30am  == 28d 22H
    // 2024-03-30 8:30am  2024-03-01 10:30am == 29d 2H

    // Format of each test case below:
    // [date1, fields-added, fields, date2, duration-since]
    //
    //  date1 + days-added = date2
    //  date1.since(date2, { fields }) == duration-since
    //
    // Empty duration '' means all fields in the result were zero.
    //
    Case2[] cases = new Case2[] {
      // POSITIVE DURATIONS: date1.since(date2) where date1 > date2

      // Spring DST
      new Case2(MAR_10_2024, minute(0), UTC, "2024-03-10", ""),
      new Case2(MAR_10_2024, minute(1), UTC, "2024-03-10", "-1M"),
      new Case2(MAR_10_2024, hour(1), UTC, "2024-03-10", "-1H"),

      new Case2(MAR_10_2024, day(-10).hour(-10), UTC, "2024-02-28", "10d 10H"),

      new Case2(JAN_30_2024, day(28).hour(1), UTC, "2024-02-27", "-28d -1H"),
      new Case2(JAN_30_2024, day(28).hour(1), NEW_YORK, "2024-02-27", "-28d -1H"),
      new Case2(JAN_30_2024, day(28).hour(2), UTC, "2024-02-27", "-28d -2H"),
      new Case2(JAN_30_2024, day(28).hour(2), NEW_YORK, "2024-02-27", "-28d -2H"),

      new Case2(JAN_30_2024, day(-28).hour(-1), UTC, "2024-01-02", "28d 1H"),
      new Case2(JAN_30_2024, day(-28).hour(-1), NEW_YORK, "2024-01-02", "28d 1H"),
      new Case2(JAN_30_2024, day(-28).hour(-2), UTC, "2024-01-02", "28d 2H"),
      new Case2(JAN_30_2024, day(-28).hour(-2), NEW_YORK, "2024-01-02", "28d 2H"),

      new Case2(MAR_30_2024, day(-27).hour(-3), UTC, "2024-03-03", "27d 3H"),
      new Case2(MAR_30_2024, day(-27).hour(-3), NEW_YORK, "2024-03-03", "27d 3H"),
      new Case2(MAR_30_2024, day(-27).hour(-17), UTC, "2024-03-02", "27d 17H"),
      new Case2(MAR_30_2024, day(-27).hour(-17), NEW_YORK, "2024-03-02", "27d 17H"),

      // Days

      new Case2(MAR_30_2024, day(-28), NEW_YORK, "2024-03-02", "28d"),
      new Case2(MAR_30_2024, day(-28), UTC, "2024-03-02", "28d"),
      new Case2(MAR_30_2024, day(-29), NEW_YORK, "2024-03-01", "29d"),
      new Case2(MAR_30_2024, day(-29), UTC, "2024-03-01", "29d"),
      new Case2(MAR_30_2024, day(-30), NEW_YORK, "2024-02-29", "1m"),
      new Case2(MAR_30_2024, day(-30), UTC, "2024-02-29", "1m"),
      new Case2(MAR_30_2024, day(-31), NEW_YORK, "2024-02-28", "1m 1d"),
      new Case2(MAR_30_2024, day(-31), UTC, "2024-02-28", "1m 1d"),
      new Case2(MAR_30_2024, day(-32), NEW_YORK, "2024-02-27", "1m 2d"),
      new Case2(MAR_30_2024, day(-32), UTC, "2024-02-27", "1m 2d"),
      new Case2(MAR_30_2024, day(-33), NEW_YORK, "2024-02-26", "1m 3d"),
      new Case2(MAR_30_2024, day(-33), UTC, "2024-02-26", "1m 3d"),

      new Case2(MAR_03_2024, day(-27), NEW_YORK, "2024-02-05", "27d"),
      new Case2(MAR_03_2024, day(-28), NEW_YORK, "2024-02-04", "28d"),
      new Case2(MAR_03_2024, day(-29), NEW_YORK, "2024-02-03", "1m"),
      new Case2(MAR_03_2024, day(-30), NEW_YORK, "2024-02-02", "1m 1d"),
      new Case2(MAR_03_2024, day(-31), NEW_YORK, "2024-02-01", "1m 2d"),
      new Case2(MAR_03_2024, day(-32), NEW_YORK, "2024-01-31", "1m 3d"),
      new Case2(MAR_03_2024, day(-33), NEW_YORK, "2024-01-30", "1m 4d"),

      // NEGATIVE DURATIONS: date1.since(date2) where date1 < date2

      new Case2(JAN_30_2024, day(29), UTC, "2024-02-28", "-29d"),
      new Case2(JAN_30_2024, day(30), UTC, "2024-02-29", "-30d"),
      new Case2(JAN_30_2024, day(31), UTC, "2024-03-01", "-1m -1d"),
      new Case2(JAN_30_2024, day(32), UTC, "2024-03-02", "-1m -2d"),
      new Case2(JAN_30_2024, day(33), UTC, "2024-03-03", "-1m -3d"),
      new Case2(FEB_28_2024, day(28), UTC, "2024-03-27", "-28d"),
      new Case2(FEB_28_2024, day(29), UTC, "2024-03-28", "-1m"),
      new Case2(FEB_28_2024, day(30), UTC, "2024-03-29", "-1m -1d"),
      new Case2(FEB_28_2024, day(31), UTC, "2024-03-30", "-1m -2d"),
      new Case2(FEB_28_2024, day(32), UTC, "2024-03-31", "-1m -3d"),
      new Case2(FEB_28_2024, day(33), UTC, "2024-04-01", "-1m -4d"),
      new Case2(FEB_29_2024, day(28), UTC, "2024-03-28", "-28d"),
      new Case2(FEB_29_2024, day(29), UTC, "2024-03-29", "-1m"),
      new Case2(FEB_29_2024, day(30), UTC, "2024-03-30", "-1m -1d"),
      new Case2(FEB_29_2024, day(31), UTC, "2024-03-31", "-1m -2d"),
      new Case2(FEB_29_2024, day(32), UTC, "2024-04-01", "-1m -3d"),
      new Case2(FEB_29_2024, day(33), UTC, "2024-04-02", "-1m -4d"),

      // ROLLDOWNS

      new Case2(FEB_29_2024, day(-400), fields(TimePeriodField.YEAR), UTC, "2023-01-25", "1y"),
      new Case2(FEB_29_2024, day(-400), fields(TimePeriodField.YEAR, TimePeriodField.MONTH), UTC, "2023-01-25", "1y 1m"),
      new Case2(FEB_29_2024, day(-400), fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY), UTC, "2023-01-25", "1y 1m 4d"),
      new Case2(FEB_29_2024, day(-400), fields(TimePeriodField.MONTH), UTC, "2023-01-25", "13m"),
      new Case2(FEB_29_2024, day(-400), fields(TimePeriodField.MONTH, TimePeriodField.DAY), UTC, "2023-01-25", "13m 4d"),
      new Case2(FEB_29_2024, day(-400), fields(TimePeriodField.DAY), UTC, "2023-01-25", "400d"),

      new Case2(FEB_29_2024, day(-33), fields(TimePeriodField.YEAR), UTC, "2024-01-27", ""),
      new Case2(FEB_29_2024, day(-33), fields(TimePeriodField.YEAR, TimePeriodField.MONTH), UTC, "2024-01-27", "1m"),
      new Case2(FEB_29_2024, day(-33), fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY), UTC, "2024-01-27", "1m 2d"),
      new Case2(FEB_29_2024, day(-33), fields(TimePeriodField.MONTH), UTC, "2024-01-27", "1m"),
      new Case2(FEB_29_2024, day(-33), fields(TimePeriodField.MONTH, TimePeriodField.DAY), UTC, "2024-01-27", "1m 2d"),
      new Case2(FEB_29_2024, day(-33), fields(TimePeriodField.DAY), UTC, "2024-01-27", "33d"),
    };

    for (Case2 c : cases) {
      String zone = c.zone == null ? "UTC" : c.zone;
      GregorianDate cldr1 = gregorian(c.epoch, zone);
      GregorianDate cldr2 = cldr1.add(c.added);

      assertEquals(dateString(cldr2), c.expDate, zone + " " + c.added);
      assertEquals(
          periodString(cldr1.since(cldr2, options().fields(asList(c.fields)))),
          c.expDiff);
    }
  }

  private static TimePeriod day(double v) {
    return TimePeriod.build().day(v);
  }

  private static TimePeriod hour(double v) {
    return TimePeriod.build().hour(v);
  }

  private static TimePeriod minute(double v) {
    return TimePeriod.build().minute(v);
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
