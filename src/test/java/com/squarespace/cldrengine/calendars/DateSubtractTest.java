package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;

/**
 * Explicit subtract spot cases. There is no standalone datesubtract.test.ts
 * in the TS repo at v1.15.0; subtract assertions live inside dateadd.test.ts
 * and integers.test.ts (both mirrored separately) and in the datemath
 * goldens. The expected values below were derived from the published
 * @phensley/cldr 1.15.0 oracle (node one-liners against codegen/node_modules).
 */
public class DateSubtractTest {

  private static final String NY = "America/New_York";

  // Sat March 11, 2000 8:00:25 AM UTC (_referencedates.ts MAR_11_2000)
  private static final long MAR_11_2000 = 952761625000L;

  private static GregorianDate gregorian(long epoch, String zoneId) {
    return GregorianDate.fromUnixEpoch(epoch, zoneId, 1, 1);
  }

  @Test
  public void testSubtractMonthAtEndOfMonth() {
    // 2019-04-30 12:30:45 UTC minus 1 month. Temporal-compatible clamping only
    // snaps days that overflow; day 30 is valid in March, so the result is
    // 2019-03-30 (not 03-31) — verified against the 1.15.0 oracle.
    GregorianDate date = gregorian(1556627445000L, "UTC");
    assertEquals(date.toString(), "Gregorian 2019-04-30 12:30:45.000 Etc/UTC");

    GregorianDate q = date.subtract(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Gregorian 2019-03-30 12:30:45.000 Etc/UTC");
  }

  @Test
  public void testSubtractAcrossSpringForward() {
    // 2000-04-10 03:00:25 EDT (result of MAR_11_2000 + 30 days, crossing the
    // Apr 2 NY spring-forward) minus 30 days: wall clock is preserved back
    // across the boundary to 2000-03-11 03:00:25 EDT->EST.
    GregorianDate date = gregorian(MAR_11_2000, NY).add(TimePeriod.build().day(30));
    assertEquals(date.toString(), "Gregorian 2000-04-10 03:00:25.000 America/New_York");

    GregorianDate q = date.subtract(TimePeriod.build().day(30));
    assertEquals(q.toString(), "Gregorian 2000-03-11 03:00:25.000 America/New_York");
  }

  @Test
  public void testSubtractAcrossFallBack() {
    // 2000-11-06 03:00:25 EST minus 30 days crosses the Nov 5 fall-back; the
    // wall time stays 03:00:25.
    GregorianDate date = gregorian(MAR_11_2000, NY).add(TimePeriod.build().day(240));
    assertEquals(date.toString(), "Gregorian 2000-11-06 03:00:25.000 America/New_York");

    GregorianDate q = date.subtract(TimePeriod.build().day(30));
    assertEquals(q.toString(), "Gregorian 2000-10-07 03:00:25.000 America/New_York");

    q = date.subtract(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 1999-11-06 03:00:25.000 America/New_York");
  }

  @Test
  public void testSubtractLeapDayClampNotReversed() {
    // 2024-02-29 + 1y clamps to 2025-02-28; subtracting 1y from there gives
    // 2024-02-28 (clamping is not reversed on subtract).
    GregorianDate date = gregorian(1709213400000L, NY).add(TimePeriod.build().year(1));
    assertEquals(date.toString(), "Gregorian 2025-02-28 08:30:00.000 America/New_York");

    GregorianDate q = date.subtract(TimePeriod.build().year(1));
    assertEquals(q.toString(), "Gregorian 2024-02-28 08:30:00.000 America/New_York");
  }

  @Test
  public void testSubtractMonthClampRoundTrip() {
    // 2019-03-31 + 1m = 2019-04-30 (clamped); - 1m = 2019-03-30, i.e. add +
    // subtract of 1 month is not an identity at a month's end.
    GregorianDate date = gregorian(1554035445000L, "UTC").add(TimePeriod.build().month(1));
    assertEquals(date.toString(), "Gregorian 2019-04-30 12:30:45.000 Etc/UTC");

    GregorianDate q = date.subtract(TimePeriod.build().month(1));
    assertEquals(q.toString(), "Gregorian 2019-03-30 12:30:45.000 Etc/UTC");
  }

  @Test
  public void testSubtractTimeFields() {
    GregorianDate date = gregorian(MAR_11_2000, NY);

    GregorianDate q = date.subtract(TimePeriod.build().hour(24));
    assertEquals(q.toString(), "Gregorian 2000-03-10 03:00:25.000 America/New_York");

    q = date.subtract(TimePeriod.build().week(1));
    assertEquals(q.toString(), "Gregorian 2000-03-04 03:00:25.000 America/New_York");

    q = date.subtract(TimePeriod.build().month(-1)); // subtracting -1 month = +1 month
    assertEquals(q.toString(), "Gregorian 2000-04-11 03:00:25.000 America/New_York");
  }

}
