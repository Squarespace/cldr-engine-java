package com.squarespace.cldrengine.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.CalendarConstants;
import com.squarespace.cldrengine.api.DateField;
import com.squarespace.cldrengine.api.Pair;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;
import com.squarespace.cldrengine.calendars.ZoneInfo;

/**
 * Spot cases for CalendarMath, oracle-derived from the 1.15.0 TS engine
 * (codegen/node_modules @phensley/cldr-core/lib/systems/calendars/math).
 */
public class CalendarMathTest {

  @Test
  public void testClamp() {
    assertEquals(CalendarMath.clamp(5, 1, 10), 5);
    assertEquals(CalendarMath.clamp(0, 1, 10), 1);
    assertEquals(CalendarMath.clamp(11, 1, 10), 10);
    assertEquals(CalendarMath.clamp(-5, -3, -1), -3);
    assertEquals(CalendarMath.clamp(3, -3, -1), -1);
    assertEquals(CalendarMath.clamp(3, 10, 1), 1);
  }

  @Test
  public void testClampYMD() {
    ckYM(2020, 13, 40, 2020, 12, 31, "month+day clamp");
    ckYM(2020, 0, 0, 2020, 1, 1, "month+day floor");
    ckYM(2019, 2, 31, 2019, 2, 28, "Feb non-leap day clamp");
    ckYM(2000, 2, 31, 2000, 2, 29, "Feb leap day clamp");
    ckYM(1900, 2, 31, 1900, 2, 28, "Feb century day clamp");
    ckYM(-1, 2, 31, -1, 2, 28, "BC Feb day clamp");
    ckYM(-72, 12, 29, -72, 12, 29, "BC in-range");
    ckYM(0, 0, 0, 0, 1, 1, "1 BC floor");
    ckYM(2000, 1, 1, 2000, 1, 1, "in-range");
    ckYM(2020, 7, 31, 2020, 7, 31, "month with 31 days");
    ckYM(2020, 7, 0, 2020, 7, 1, "day floor");
    ckYM(2019, 14, 15, 2019, 12, 15, "month at 12");
  }

  private void ckYM(long y, long m, long d, long ey, long em, long ed, String label) {
    CalendarMath.YearMonthDay res = CalendarMath.clampYMD(y, m, d);
    assertEquals(res.year, ey, label + " year");
    assertEquals(res.month, em, label + " month");
    assertEquals(res.day, ed, label + " day");
  }

  @Test
  public void testAdjustYM() {
    ckAY(2020, 0, 2019, 12, "month below 1");
    ckAY(2020, 12, 2020, 12, "December of same year");
    ckAY(2020, 13, 2021, 1, "next year January");
    ckAY(2020, 14, 2021, 2, "next year February");
    ckAY(2020, -1, 2019, 11, "negative month");
    ckAY(2020, 24, 2021, 12, "two years of months");
    ckAY(2020, 25, 2022, 1, "two years plus one month");
    ckAY(2020, -12, 2018, 12, "exactly minus one year");
    ckAY(2020, -13, 2018, 11, "minus one year minus a month");
    ckAY(-1, 1, -1, 1, "BC in-range");
    ckAY(0, 11, 0, 11, "BC November");
    ckAY(0, 12, 0, 12, "BC December");
    ckAY(0, 13, 1, 1, "BC to AD rollover");
    ckAY(2000, 23, 2001, 11, "23 months");
    ckAY(2000, 24, 2001, 12, "24 months");
  }

  private void ckAY(long y, long m, long ey, long em, String label) {
    CalendarMath.YearMonth res = CalendarMath.adjustYM(y, m);
    assertEquals(res.year, ey, label + " year");
    assertEquals(res.month, em, label + " month");
  }

  @Test
  public void testTimeToMillis() {
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build()), 0.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().hour(1)), 3600000.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().hour(1).minute(30)), 5400000.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().minute(0)), 0.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().millis(500)), 500.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().hour(24)), 86400000.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().second(90)), 90000.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().hour(0.5)), 1800000.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().minute(1.5)), 90000.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().hour(-1)), -3600000.0);
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().millis(-1)), -1.0);
    // day/week/year/month are not time fields
    assertEquals(CalendarMath.timeToMillis(TimePeriod.build().day(2)), 0.0);
  }

  @Test
  public void testAdjustTime() {
    ckAT(0, TimePeriod.build().hour(25), 1, 3600000.0);
    ckAT(86399999, TimePeriod.build().hour(1), 1, 3599999.0);
    ckAT(86400000, TimePeriod.build().hour(25), 2, 3600000.0);
    ckAT(0, TimePeriod.build(), 0, 0.0);
    ckAT(0, TimePeriod.build().millis(1), 0, 1.0);
    ckAT(1000, TimePeriod.build().second(1), 0, 2000.0);
    ckAT(-1000, TimePeriod.build(), 0, -1000.0);
    ckAT(-1000, TimePeriod.build().second(1), 0, 0.0);
    ckAT(1000, TimePeriod.build().minute(-1), 0, -59000.0);
    ckAT(0, TimePeriod.build().millis(-1000), 0, -1000.0);
    ckAT(500, TimePeriod.build().millis(0.5), 0, 500.5);
  }

  private void ckAT(long millis, TimePeriod added, long days, double ms) {
    Pair<Long, Double> res = CalendarMath.adjustTime(millis, added);
    assertEquals(res._1.longValue(), days, "days");
    assertEquals(res._2.doubleValue(), ms, 0.0001, "ms");
  }

  @Test
  public void testTruncateFields() {
    TimePeriod res = CalendarMath.truncateFields(TimePeriod.build()
        .hour(1.9).minute(1.1).second(-1.1).millis(3.999));
    assertEquals((double)res.year.or(0.0), 0.0);
    assertEquals((double)res.month.or(0.0), 0.0);
    assertEquals((double)res.week.or(0.0), 0.0);
    assertEquals((double)res.day.or(0.0), 0.0);
    assertEquals((double)res.hour.or(0.0), 1.0);
    assertEquals((double)res.minute.or(0.0), 1.0);
    assertEquals((double)res.second.or(0.0), -1.0);
    assertEquals((double)res.millis.or(0.0), 3.0);

    res = CalendarMath.truncateFields(TimePeriod.build().hour(-1.9));
    assertEquals((double)res.hour.or(0.0), -1.0);
    assertEquals((double)res.year.or(0.0), 0.0);
    assertEquals((double)res.month.or(0.0), 0.0);
    assertEquals((double)res.week.or(0.0), 0.0);
    assertEquals((double)res.day.or(0.0), 0.0);
    assertEquals((double)res.minute.or(0.0), 0.0);
    assertEquals((double)res.second.or(0.0), 0.0);
    assertEquals((double)res.millis.or(0.0), 0.0);

    // Unset fields are truncated from 0 and the result has all fields set.
    res = CalendarMath.truncateFields(TimePeriod.build());
    assertEquals((double)res.year.or(0.0), 0.0);
    assertEquals((double)res.month.or(0.0), 0.0);
    assertEquals((double)res.week.or(0.0), 0.0);
    assertEquals((double)res.day.or(0.0), 0.0);
    assertEquals((double)res.hour.or(0.0), 0.0);
    assertEquals((double)res.minute.or(0.0), 0.0);
    assertEquals((double)res.second.or(0.0), 0.0);
    assertEquals((double)res.millis.or(0.0), 0.0);
    assertTrue(res.year.ok());
    assertTrue(res.month.ok());
    assertTrue(res.week.ok());
    assertTrue(res.day.ok());
    assertTrue(res.hour.ok());
    assertTrue(res.minute.ok());
    assertTrue(res.second.ok());
    assertTrue(res.millis.ok());

    res = CalendarMath.truncateFields(TimePeriod.build()
        .day(0.9).week(1.5).year(2.7).month(-1.7));
    assertEquals((double)res.year.or(0.0), 2.0);
    assertEquals((double)res.month.or(0.0), -1.0);
    assertEquals((double)res.week.or(0.0), 1.0);
    assertEquals((double)res.day.or(0.0), 0.0);
    assertEquals((double)res.hour.or(0.0), 0.0);
    assertEquals((double)res.minute.or(0.0), 0.0);
    assertEquals((double)res.second.or(0.0), 0.0);
    assertEquals((double)res.millis.or(0.0), 0.0);
  }

  @Test
  public void testJdFromUnixEpoch() {
    ckJDE(0, 2440588L, 0L);
    ckJDE(-1, 2440587L, 86399999L);
    ckJDE(-1000, 2440587L, 86399000L);
    ckJDE(86400000, 2440589L, 0L);
    ckJDE(946684800000L, 2451545L, 0L);
    ckJDE(1000000000000L, 2452162L, 6400000L);
    ckJDE(-1000000000000L, 2429013L, 80000000L);
  }

  private void ckJDE(long ms, long jd, long msDay) {
    Pair<Long, Long> res = CalendarMath.jdFromUnixEpoch(ms);
    assertEquals(res._1.longValue(), jd, "jd");
    assertEquals(res._2.longValue(), msDay, "msDay");
  }

  @Test
  public void testUnixEpochFromJD() {
    assertEquals(CalendarMath.unixEpochFromJD(2440588L, 0L), 0L);
    assertEquals(CalendarMath.unixEpochFromJD(0L, 0L), -210866803200000L);
    assertEquals(CalendarMath.unixEpochFromJD(2451545L, 0L), 946684800000L);
    assertEquals(CalendarMath.unixEpochFromJD(2299161L, 0L), -12219292800000L);
  }

  @Test
  public void testComputeBaseFields() {
    ckCBF(0L, new long[] {
        0, 2440588, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
    ckCBF(-1000L, new long[] {
        -1000, 2440587, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 86399000, 1, 23, 11, 59, 59, 0, 0, 0, 0, 0, 0 });
    ckCBF(946684800000L, new long[] {
        946684800000L, 2451545, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
  }

  private void ckCBF(long ms, long[] expected) {
    long[] f = new long[DateField.LENGTH];
    CalendarMath.fieldsFromUnixEpoch(ms, f);
    CalendarMath.computeBaseFields(f);
    assertEquals(f, expected);
  }

  @Test
  public void testInitBaseFromUnixEpoch() {
    // UTC at epoch
    long[] f = new long[DateField.LENGTH];
    ZoneInfo zi = CalendarMath.initBaseFromUnixEpoch(f, 0L, "Etc/UTC");
    assertEquals(zi.offset, 0);
    assertEquals(f[DateField.LOCAL_MILLIS], 0L);
    assertEquals(f[DateField.JULIAN_DAY], 2440588L);
    assertEquals(f[DateField.MILLIS_IN_DAY], 0L);
    assertEquals(f[DateField.DAY_OF_WEEK], 5L);
    assertEquals(f[DateField.HOUR_OF_DAY], 0L);

    // America/New_York winter (EST -5h) at Jan 1 2000 UTC =
    // Dec 31 1999 19:00 local
    f = new long[DateField.LENGTH];
    ZoneInfo zi2 = CalendarMath.initBaseFromUnixEpoch(f, 946684800000L, "America/New_York");
    assertEquals(zi2.offset, -18000000);
    assertEquals(f[DateField.LOCAL_MILLIS], 946666800000L);
    assertEquals(f[DateField.JULIAN_DAY], 2451544L);
    assertEquals(f[DateField.MILLIS_IN_DAY], 68400000L);
    assertEquals(f[DateField.DAY_OF_WEEK], 6L);
    assertEquals(f[DateField.AM_PM], 1L);
    assertEquals(f[DateField.HOUR_OF_DAY], 19L);
    assertEquals(f[DateField.HOUR], 7L);
    assertEquals(f[DateField.MINUTE], 0L);
    assertEquals(f[DateField.SECOND], 0L);
    assertEquals(f[DateField.MILLIS], 0L);
  }

}
