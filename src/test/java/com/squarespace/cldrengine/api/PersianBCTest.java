package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

/**
 * Regression tests for Persian (AP) calendar date arithmetic across the
 * BC year boundary, where monthStart()'s truncated division used to diverge
 * from the TS oracle's Math.floor of real division.
 *
 * All expected values verified against the TS oracle
 * (cldr-engine @ v1.15.0, packages/cldr-core/src/systems/calendars/persian.ts):
 *   CLDRFramework.get('en').Calendars.newPersianDate({ year, month, day })
 *     .add({ year: 1 })
 */
public class PersianBCTest {

  private static final String UTC = "Etc/UTC";

  private static final CLDR EN = CLDR.get("en");

  private static PersianDate date(long year, int month, int day) {
    return EN.Calendars.newPersianDate(CalendarDateFields.build()
        .year(year).month(month).day(day).zoneId(UTC));
  }

  private static String fmt(PersianDate d) {
    // Same rendering as _toString but without time/zone noise
    String y = String.format("%04d", Math.abs(d.year()));
    return String.format("%s%s-%02d-%02d", d.year() < 0 ? "-" : "", y,
        d.month(), d.dayOfMonth());
  }

  /**
   * Audit repro 1: audit pre-fix Java gave "-0070-01-02" vs TS "-0071-12-29".
   * Oracle (v1.15.0): newPersianDate({year:-72, month:12, day:29}).add({year:1})
   * => year -71, month 12, day 29.
   */
  @Test
  public void testAddYearBC0072() {
    PersianDate d = date(-72, 12, 29);
    assertEquals(fmt(d), "-0072-12-29");
    PersianDate q = d.add(TimePeriod.build().year(1));
    assertEquals(fmt(q), "-0071-12-29");
    assertEquals(q.toString(), "Persian -0071-12-29 00:00:00.000 Etc/UTC");
  }

  /**
   * Audit repro 2: audit pre-fix Java gave "-0021-10-13" vs TS "-0021-10-11".
   * Oracle (v1.15.0): newPersianDate({year:-22, month:10, day:11}).add({year:1})
   * => year -21, month 10, day 11.
   */
  @Test
  public void testAddYearBC0022() {
    PersianDate d = date(-22, 10, 11);
    assertEquals(fmt(d), "-0022-10-11");
    PersianDate q = d.add(TimePeriod.build().year(1));
    assertEquals(fmt(q), "-0021-10-11");
    assertEquals(q.toString(), "Persian -0021-10-11 00:00:00.000 Etc/UTC");
  }

  /**
   * monthStart boundary case: last-ish day of a BC year landing on the next
   * Farvardin 1 via 366-day leap.
   * Oracle (v1.15.0): newPersianDate({year:-73, month:12, day:30}).add({year:1})
   * => year -71, month 1, day 1.
   */
  @Test
  public void testAddYearBCLeapRollover() {
    PersianDate d = date(-73, 12, 30);
    assertEquals(fmt(d), "-0073-12-30");
    PersianDate q = d.add(TimePeriod.build().year(1));
    assertEquals(fmt(q), "-0071-01-01");
  }

  /**
   * Positive control in CE years.
   * Oracle (v1.15.0): newPersianDate({year:1400, month:1, day:1}).add({year:1})
   * => year 1401, month 1, day 1.
   */
  @Test
  public void testAddYearCE1400() {
    PersianDate d = date(1400, 1, 1);
    assertEquals(fmt(d), "1400-01-01");
    PersianDate q = d.add(TimePeriod.build().year(1));
    assertEquals(fmt(q), "1401-01-01");
  }

  /**
   * BC leap-year normalization: the TS oracle's leapPersian reads the C-style
   * remainder of floorDiv(25*y+11, 33, rem) (rem = n % d, sign of dividend),
   * so year -2 is a leap year (rem -6 < 8) and its Esfand has 30 days.
   * newPersianDate({year:-2, month:12, day:30}) therefore rolls to the next
   * Farvardin 1 (JD -730 == year -1 start), and +1 day lands on -1-01-02.
   * Oracle (v1.15.0): base => {year:-1, month:1, day:1}; +1 day => -1-01-02.
   */
  @Test
  public void testBCYearMinus2EsfandLeapRollover() {
    PersianDate d = date(-2, 12, 30);
    assertEquals(d.year(), -1);
    assertEquals(d.month(), 1);
    assertEquals(d.dayOfMonth(), 1);
    PersianDate q = d.add(TimePeriod.build().day(1));
    assertEquals(q.year(), -1);
    assertEquals(q.month(), 1);
    assertEquals(q.dayOfMonth(), 2);
  }

  /**
   * Boundary control: year -2 Esfand day 29 exists in both engines and
   * +1 year lands on -1-12-29 (no Esfand-length dependence).
   * Oracle (v1.15.0): newPersianDate({year:-2, month:12, day:29}).add({year:1})
   * => year -1, month 12, day 29.
   */
  @Test
  public void testAddYearBC0002() {
    PersianDate d = date(-2, 12, 29);
    assertEquals(d.year(), -2);
    assertEquals(d.month(), 12);
    assertEquals(d.dayOfMonth(), 29);
    PersianDate q = d.add(TimePeriod.build().year(1));
    assertEquals(q.year(), -1);
    assertEquals(q.month(), 12);
    assertEquals(q.dayOfMonth(), 29);
  }

  /**
   * CE non-leap Esfand clamp control (unchanged behavior for positive years:
   * both remainder conventions agree there). 1400 is not a leap year, so
   * day 30 clamps to 29.
   * Oracle (v1.15.0): newPersianDate({year:1400, month:12, day:30})
   * => year 1400, month 12, day 29.
   */
  @Test
  public void testCEYear1400EsfandClamp() {
    PersianDate d = date(1400, 12, 30);
    assertEquals(d.year(), 1400);
    assertEquals(d.month(), 12);
    assertEquals(d.dayOfMonth(), 29);
  }
}
