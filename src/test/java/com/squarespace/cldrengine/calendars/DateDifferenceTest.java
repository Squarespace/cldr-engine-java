package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_11_2000;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.NEW_YORK;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.dateTimeString;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.gregorian;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateDiffOptions;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/datediff.test.ts
 * (tag v1.15.0). Case tables copied verbatim.
 */
public class DateDifferenceTest {

  // February 29, 2024 8:30:00 AM GMT-05:00
  private static final long FEB_29_2024 = 1709213400000L;

  private final String NY = NEW_YORK;

  @Test
  public void testNegativeTime() {
    GregorianDate end;

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");

    end = start.add(TimePeriod.build().day(2).hour(-7));
    assertEquals(dateTimeString(end, true, true), "2024-03-02 01:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().day(1).hour(17));
  }

  @Test
  public void testDifferenceMonthVsDay() {
    GregorianDate end;

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");

    end = start.add(TimePeriod.build().month(11).day(-28));
    assertEquals(dateTimeString(end, true, true), "2025-01-01 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(10).day(3));

    end = start.add(TimePeriod.build().year(1).month(-1));
    assertEquals(dateTimeString(end, true, true), "2025-01-29 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(11));

    end = start.add(TimePeriod.build().year(1).day(-5));
    assertEquals(dateTimeString(end, true, true), "2025-02-23 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(11).day(25));
  }

  @Test
  public void testAllFlags() {
    GregorianDate end;
    TimePeriodField[] fields = new TimePeriodField[] {
      TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.WEEK, TimePeriodField.DAY,
      TimePeriodField.HOUR, TimePeriodField.MINUTE, TimePeriodField.SECOND, TimePeriodField.MILLIS,
    };

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");

    end = start.add(TimePeriod.build().year(1).month(1));
    assertEquals(dateTimeString(end, true, true), "2025-03-29 08:30:00-04:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().year(1).month(1));
    assertEquals(start.difference(end, options().fields(Arrays.asList(fields))), TimePeriod.build().year(1).month(1));
  }

  @Test
  public void testWeeks() {
    GregorianDate end;

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");

    end = start.add(TimePeriod.build().day(15));
    assertEquals(
        start.difference(end, options().fields(Arrays.asList(TimePeriodField.WEEK, TimePeriodField.DAY))),
        TimePeriod.build().week(2).day(1));

    end = start.add(TimePeriod.build().day(15));
    assertEquals(
        start.difference(end, options().fields(Collections.singletonList(TimePeriodField.DAY)).includeWeeks(true)),
        TimePeriod.build().week(0).day(15));
  }

  @Test
  public void testLeapYear() {
    GregorianDate end;

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");

    end = start.add(TimePeriod.build().year(1));
    assertEquals(dateTimeString(end, true, true), "2025-02-28 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(11).day(30));

    end = start.add(TimePeriod.build().year(1).month(1));
    assertEquals(dateTimeString(end, true, true), "2025-03-29 08:30:00-04:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().year(1).month(1));

    end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(dateTimeString(end, true, true), "2025-05-02 08:30:00-04:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().year(1).month(2).day(3));

    end = start.add(TimePeriod.build().year(1).month(2).day(17));
    assertEquals(dateTimeString(end, true, true), "2025-05-16 08:30:00-04:00");
    assertEquals(
        start.difference(end, options().includeWeeks(true)),
        TimePeriod.build().year(1).month(2).week(2).day(3));
  }

  @Test
  public void testMonthBackwards() {
    GregorianDate end;

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");

    end = start.add(TimePeriod.build().year(1).month(-1));
    assertEquals(dateTimeString(end, true, true), "2025-01-29 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(11));

    end = start.add(TimePeriod.build().year(1).day(-5));
    assertEquals(dateTimeString(end, true, true), "2025-02-23 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(11).day(25));

    end = start.add(TimePeriod.build().year(1).month(-1).day(-5));
    assertEquals(dateTimeString(end, true, true), "2025-01-24 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(10).day(26));

    end = start.add(TimePeriod.build().year(1).month(-3));
    assertEquals(dateTimeString(end, true, true), "2024-11-29 08:30:00-05:00");
    assertEquals(start.difference(end, options()), TimePeriod.build().month(9));
  }

  @Test
  public void testBasicDifference() {
    TimePeriod t;
    CalendarDate end;
    CalendarDate start = gregorian(MAR_11_2000, NEW_YORK);

    end = start.add(TimePeriod.build().day(369)); // Mar 15, 2001

    t = start.difference(end, options());
    assertEquals(t, TimePeriod.build().year(1).day(4));

    end = start.add(TimePeriod.build().day(419)); // May 4, 2001

    t = start.difference(end, options().includeWeeks(true));
    assertEquals(t, TimePeriod.build().year(1).month(1).week(3).day(2));
    t = start.difference(end, options());
    assertEquals(t, TimePeriod.build().year(1).month(1).day(23));

    end = start.add(TimePeriod.build().year(3).month(17)); // Aug 11, 2004

    t = start.difference(end, options());
    assertEquals(t, TimePeriod.build().year(4).month(5));

    end = start.add(TimePeriod.build().day(45)); // April 25, 2000

    t = start.difference(end, options().includeWeeks(true));
    assertEquals(t, TimePeriod.build().month(1).week(2));

    end = start.add(TimePeriod.build().hour(48)); // March 13, 2000

    t = start.difference(end, options());
    assertEquals(t, TimePeriod.build().day(2));

    end = start.add(TimePeriod.build().day(8).hour(12)); // March 19, 2000

    t = start.difference(end, options().includeWeeks(true));
    assertEquals(t, TimePeriod.build().week(1).day(1).hour(12));

    end = start.add(TimePeriod.build().hour(24 * 10)); // March 21, 2000

    t = start.difference(end, options().includeWeeks(true));
    assertEquals(t, TimePeriod.build().week(1).day(3));
  }

  @Test
  public void testSignedDifference1() {
    TimePeriod t;
    CalendarDate end;
    CalendarDate start = gregorian(MAR_11_2000, NEW_YORK);

    end = start.add(TimePeriod.build().day(369)); // Mar 15, 2001

    t = end.differenceSigned(start, options());
    assertEquals(t, TimePeriod.build().year(-1).day(-4));

    t = start.differenceSigned(end, options());
    assertEquals(t, TimePeriod.build().year(1).day(4));

    end = start.add(TimePeriod.build().day(419)); // May 4, 2001
    assertEquals(dateTimeString(end, true, true), "2001-05-04 03:00:25-04:00");
    assertEquals(
        end.differenceSigned(start, options().includeWeeks(true)),
        TimePeriod.build().year(-1).month(-1).week(-3).day(-3));
    assertEquals(
        end.differenceSigned(start, options().includeWeeks(false)),
        TimePeriod.build().year(-1).month(-1).day(-24));

    assertEquals(
        start.differenceSigned(end, options().includeWeeks(true)),
        TimePeriod.build().year(1).month(1).week(3).day(2));
    assertEquals(
        start.differenceSigned(end, options().includeWeeks(false)),
        TimePeriod.build().year(1).month(1).day(23));
  }

  @Test
  public void testBasicDifference2() {
    CalendarDate end;
    CalendarDate start = gregorian(MAR_11_2000, NEW_YORK);
    assertEquals(dateTimeString(start, true, true), "2000-03-11 03:00:25-05:00");

    end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(dateTimeString(end, true, true), "2001-05-14 03:00:25-04:00");
    assertEquals(
        start.until(end, options().fields(Collections.singletonList(TimePeriodField.MONTH))),
        TimePeriod.build().month(14));
    assertEquals(
        start.until(end, options().fields(Collections.singletonList(TimePeriodField.DAY))),
        TimePeriod.build().day(429));
    assertEquals(
        start.until(end, options().fields(Arrays.asList(TimePeriodField.YEAR, TimePeriodField.DAY))),
        TimePeriod.build().year(1).day(64));
  }

  @Test
  public void testDiffAllFields() {
    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    GregorianDate end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(start.until(end, options()), TimePeriod.build().year(1).month(2).day(3));

    end = start.add(TimePeriod.build().year(1).month(2).day(17));
    assertEquals(
        start.until(end, options().includeWeeks(true)),
        TimePeriod.build().year(1).month(2).week(2).day(3));

    assertEquals(
        start.until(end, options().fields(Arrays.asList(
            TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.WEEK))),
        TimePeriod.build().year(1).month(2).week(2).day(3));
  }

  @Test
  public void testDiffYMD() {
    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    GregorianDate end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(
        start.until(end, options().fields(Arrays.asList(
            TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY))),
        TimePeriod.build().year(1).month(2).day(3));

    end = start.add(TimePeriod.build().year(1).month(2).day(17));
    assertEquals(
        start.until(end, options().fields(Arrays.asList(
            TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY))),
        TimePeriod.build().year(1).month(2).day(17));

    assertEquals(
        start.until(end, options().fields(Arrays.asList(
            TimePeriodField.YEAR, TimePeriodField.MONTH, TimePeriodField.DAY, TimePeriodField.WEEK))),
        TimePeriod.build().year(1).month(2).week(2).day(3));
  }

  @Test
  public void testDiffY() {
    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    GregorianDate end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(
        start.until(end, options().fields(Collections.singletonList(TimePeriodField.YEAR))),
        TimePeriod.build().year(1));
  }

  @Test
  public void testDiffM() {
    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    GregorianDate end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(
        start.until(end, options().fields(Collections.singletonList(TimePeriodField.MONTH))),
        TimePeriod.build().month(14));
  }

  @Test
  public void testDiffYM() {
    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    GregorianDate end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(dateTimeString(end, true, true), "2025-05-02 08:30:00-04:00");
    assertEquals(
        start.until(end, options().fields(Arrays.asList(TimePeriodField.YEAR, TimePeriodField.MONTH))),
        TimePeriod.build().year(1).month(2));
  }

  @Test
  public void testDiffD() {
    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    GregorianDate end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(dateTimeString(end, true, true), "2025-05-02 08:30:00-04:00");
    assertEquals(
        start.until(end, options().fields(Collections.singletonList(TimePeriodField.DAY))),
        TimePeriod.build().day(428));
  }

  @Test
  public void testDiffYD() {
    GregorianDate end;

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    end = start.add(TimePeriod.build().year(1));
    assertEquals(dateTimeString(end, true, true), "2025-02-28 08:30:00-05:00");
    assertEquals(
        start.until(end, options().fields(Arrays.asList(TimePeriodField.YEAR, TimePeriodField.DAY))),
        TimePeriod.build().day(365));

    end = start.add(TimePeriod.build().year(1).month(2));
    assertEquals(dateTimeString(end, true, true), "2025-04-29 08:30:00-04:00");
    assertEquals(
        start.until(end, options().fields(Arrays.asList(TimePeriodField.YEAR, TimePeriodField.DAY))),
        TimePeriod.build().year(1).day(60));
  }

  @Test
  public void testDiffMD() {
    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");
    GregorianDate end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(dateTimeString(end, true, true), "2025-05-02 08:30:00-04:00");
    assertEquals(
        start.until(end, options().fields(Arrays.asList(TimePeriodField.MONTH, TimePeriodField.DAY))),
        TimePeriod.build().month(14).day(3));
  }

  @Test
  public void testDiffDH() {
    GregorianDate end;

    GregorianDate start = gregorian(FEB_29_2024, NY);
    assertEquals(dateTimeString(start, true, true), "2024-02-29 08:30:00-05:00");

    end = start.add(TimePeriod.build().year(1).month(2).day(3).hour(15));
    assertEquals(dateTimeString(end, true, true), "2025-05-02 23:30:00-04:00");
    assertEquals(
        start.until(end, options().fields(Arrays.asList(TimePeriodField.DAY, TimePeriodField.HOUR))),
        TimePeriod.build().month(0).day(428).hour(15));

    end = start.add(TimePeriod.build().year(1).month(2).day(3));
    assertEquals(dateTimeString(end, true, true), "2025-05-02 08:30:00-04:00");
    assertEquals(
        start.until(end, options().fields(Arrays.asList(TimePeriodField.DAY, TimePeriodField.HOUR))),
        TimePeriod.build().month(0).day(428));
  }

  private static DateDiffOptions options() {
    return DateDiffOptions.build();
  }

}
