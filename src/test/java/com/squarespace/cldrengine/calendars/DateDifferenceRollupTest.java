package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_11_2000;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.NEW_YORK;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.SEP_01_2000;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.SEP_15_2000;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.UTC;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.dateTimeString;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.gregorian;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateDiffOptions;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/datediff.rollup.test.ts
 * (tag v1.15.0). Case tables copied verbatim.
 */
public class DateDifferenceRollupTest {

  @Test
  public void testDifferenceYearMonth() {
    TimePeriod t;
    CalendarDate end;

    // Mar 11 2000
    CalendarDate start = gregorian(MAR_11_2000, NEW_YORK);
    assertEquals(dateTimeString(start, true, true), "2000-03-11 03:00:25-05:00");

    end = start.add(TimePeriod.build().year(3).day(35));
    assertEquals(dateTimeString(end, true, true), "2003-04-15 03:00:25-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(37).day(4));

    end = start.add(TimePeriod.build().month(37).day(4));
    assertEquals(dateTimeString(end, true, true), "2003-04-15 03:00:25-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().year(3).day(35));

    // Sept 15 2000
    start = gregorian(SEP_15_2000, UTC);
    assertEquals(dateTimeString(start, true, true), "2000-09-15 12:00:00+00:00");

    end = start.add(TimePeriod.build().month(6).day(2));
    assertEquals(dateTimeString(end, true, true), "2001-03-17 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(6).day(2));

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(183));

    end = start.add(TimePeriod.build().month(6).day(-5));
    assertEquals(dateTimeString(end, true, true), "2001-03-10 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(176));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(176));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(5).day(23));

    end = start.add(TimePeriod.build().month(6));
    assertEquals(dateTimeString(end, true, true), "2001-03-15 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR)).rollupFractional(true));
    assertEquals(t, TimePeriod.build().year(0.5));

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(181));

    // Sept 01 2000
    start = gregorian(SEP_01_2000, UTC);
    assertEquals(dateTimeString(start, true, true), "2000-09-01 12:00:00+00:00");

    end = start.add(TimePeriod.build().month(4).day(-5));
    assertEquals(dateTimeString(end, true, true), "2000-12-27 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(117));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(3).day(26));

    end = start.add(TimePeriod.build().month(5).day(-5));
    assertEquals(dateTimeString(end, true, true), "2001-01-27 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(148));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(4).day(26));

    end = start.add(TimePeriod.build().month(6).day(-5));
    assertEquals(dateTimeString(end, true, true), "2001-02-24 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(176));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(5).day(23));

    end = start.add(TimePeriod.build().day(176));
    assertEquals(dateTimeString(end, true, true), "2001-02-24 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(5).day(23));

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(176));
  }

  @Test
  public void testDifferenceYearMonthDay() {
    TimePeriod t;
    CalendarDate end;
    CalendarDate start = gregorian(MAR_11_2000, NEW_YORK);

    // In the examples below, the start year is a leap year 2000
    // which has 366 days, and the next year 2001 has 365 days.

    end = start.add(TimePeriod.build().day(52));
    assertEquals(dateTimeString(end, true, true), "2000-05-02 03:00:25-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(52));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(1).day(21));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().year(0).day(52));

    end = start.add(TimePeriod.build().day(183));
    assertEquals(dateTimeString(end, true, true), "2000-09-10 03:00:25-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(5).day(30));

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(183));

    end = start.add(TimePeriod.build().day(183).hour(77).minute(1000));
    assertEquals(dateTimeString(end, true, true), "2000-09-14 00:40:25-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.HOUR)));
    assertEquals(t, TimePeriod.build().month(6).day(2).hour(21));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(6).day(2));

    // Next year

    end = start.add(TimePeriod.build().day(365 + 183));
    assertEquals(dateTimeString(end, true, true), "2001-09-10 03:00:25-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().year(1).month(5).day(30));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(17).day(30));

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(548));

    end = start.add(TimePeriod.build().day(365 + 182));
    assertEquals(dateTimeString(end, true, true), "2001-09-09 03:00:25-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR)));
    assertEquals(t, TimePeriod.build().year(1).month(0));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().year(1).month(5).day(29));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(17).day(29));

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(365 + 182));
  }

  @Test
  public void testDifferenceHourMinuteSecondMillis() {
    TimePeriod t;
    CalendarDate end;

    // Saturday, June 10, 2000 12:00:00 PM
    GregorianDate start = gregorian(960638400000L, "UTC");
    assertEquals(start.toString(), "Gregorian 2000-06-10 12:00:00.000 Etc/UTC");

    end = start.add(TimePeriod.build().day(4));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR)));
    assertEquals(t, TimePeriod.build().hour(96));

    end = start.add(TimePeriod.build().day(4).minute(45));
    assertEquals(dateTimeString(end, true, true), "2000-06-14 12:45:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR)));
    assertEquals(t, TimePeriod.build().hour(96));

    end = start.add(TimePeriod.build().day(1));
    assertEquals(dateTimeString(end, true, true), "2000-06-11 12:00:00+00:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR)));
    assertEquals(t, TimePeriod.build().hour(24));

    end = start.add(TimePeriod.build().hour(6).minute(30));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR, TimePeriodField.MINUTE)));
    assertEquals(t, TimePeriod.build().hour(6).minute(30));

    end = start.add(TimePeriod.build().hour(6).minute(15).second(30));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR, TimePeriodField.MINUTE)));
    assertEquals(t, TimePeriod.build().hour(6).minute(15));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND)));
    assertEquals(t, TimePeriod.build().hour(6).minute(15).second(30));

    end = start.add(TimePeriod.build().hour(6).minute(15).second(5));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND)));
    assertEquals(t, TimePeriod.build().hour(6).minute(15).second(5));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS)));
    assertEquals(t, TimePeriod.build().hour(6).minute(15).second(5).millis(0));

    end = start.add(TimePeriod.build().millis(5503));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS)));
    assertEquals(t, TimePeriod.build().second(5).millis(503));

    end = start.add(TimePeriod.build().second(1).millis(5503));

    t = start.difference(end, options().fields(fields(TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS)));
    assertEquals(t, TimePeriod.build().second(6).millis(503));
  }

  @Test
  public void testDifferenceSubDay() {
    TimePeriod t;
    CalendarDate end;

    // Saturday, June 10, 2000 12:00:00 PM
    GregorianDate start = gregorian(960638400000L, "UTC");
    assertEquals(start.toString(), "Gregorian 2000-06-10 12:00:00.000 Etc/UTC");

    end = start.add(TimePeriod.build().day(4).minute(-120));

    t = start.difference(end, options().fields(fields(TimePeriodField.DAY, TimePeriodField.HOUR)));
    assertEquals(t, TimePeriod.build().day(3).hour(22));
  }

  @Test
  public void testDifferenceYearWrap() {
    TimePeriod t;
    CalendarDate end;

    // Saturday, Dec 31, 1999 12:00:00 PM
    GregorianDate start = gregorian(946641600000L, "UTC");
    assertEquals(start.toString(), "Gregorian 1999-12-31 12:00:00.000 Etc/UTC");

    end = start.add(TimePeriod.build().day(28));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(28));
  }

  @Test
  public void testAllFields() {
    TimePeriod t;
    CalendarDate end;
    CalendarDate start = gregorian(MAR_11_2000, NEW_YORK);
    assertEquals(dateTimeString(start, true, true), "2000-03-11 03:00:25-05:00");

    end = start.add(TimePeriod.build().year(1).month(1).day(1).hour(12).minute(30).second(30).millis(15000));
    assertEquals(dateTimeString(end, true, true), "2001-04-12 15:31:10-04:00");

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND)));
    assertEquals(t, TimePeriod.build().year(1).month(1).day(1).hour(12).minute(30).second(45));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.SECOND)));
    assertEquals(t, TimePeriod.build().year(1).month(1).day(1).hour(12).second(30 * 60 + 45));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.SECOND)));
    assertEquals(t, TimePeriod.build().year(1).month(1).day(1).second(12 * 3600 + 30 * 60 + 45));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.HOUR, TimePeriodField.MINUTE)));
    assertEquals(t, TimePeriod.build().year(1).month(1).day(1).hour(12).minute(30));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.MINUTE)));
    assertEquals(t, TimePeriod.build().year(1).month(1).day(1).minute(12 * 60 + 30));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.HOUR)));
    assertEquals(t, TimePeriod.build().year(1).month(1).day(1).hour(12));

    end = start.add(TimePeriod.build().year(1).month(1).day(1).hour(12));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().year(1).month(1).day(1));

    end = start.add(TimePeriod.build().year(1).month(1).day(15).hour(12));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.MONTH)));
    assertEquals(t, TimePeriod.build().year(1).month(1));
  }

  @Test
  public void testDifferenceEdgeCases() {
    TimePeriod t;
    CalendarDate end;

    CalendarDate start = gregorian(MAR_11_2000, "UTC");

    end = start.add(TimePeriod.build().month(7).day(82));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(296));

    end = start.add(TimePeriod.build().month(7).day(92));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(306));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(10).day(0));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH)));
    assertEquals(t, TimePeriod.build().month(10));

    end = start.add(TimePeriod.build().month(-3).day(-3));

    t = start.difference(end, options().fields(fields(TimePeriodField.YEAR, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().day(94));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(3).day(3));

    end = start.add(TimePeriod.build().month(0).day(76));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH)).rollupFractional(true));
    assertEquals(t, TimePeriod.build().month(2.492772667542707));

    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(2).day(15));

    // Invalid fields are ignored (TS passes 'foobar' which the FIELDMAP
    // skips, producing the same flags as ['month', 'day']).
    t = start.difference(end, options().fields(fields(TimePeriodField.MONTH, TimePeriodField.DAY)));
    assertEquals(t, TimePeriod.build().month(2).day(15));

    // If no valid fields are passed in, it will return the raw difference with no
    // field rollup (TS: ['foobar', 'baz'] -> flags 0 -> DEFAULT_FLAGS).
    t = start.difference(end, options().fields(fields()));
    assertEquals(t, TimePeriod.build().month(2).day(15));
  }

  private static java.util.List<TimePeriodField> fields(TimePeriodField... fs) {
    return Arrays.asList(fs);
  }

  private static DateDiffOptions options() {
    return DateDiffOptions.build();
  }

}
