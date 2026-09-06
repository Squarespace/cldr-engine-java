package com.squarespace.cldrengine.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.DateField;

/**
 * Spot cases for GregorianMath, oracle-derived from the 1.15.0 TS engine
 * (codegen/node_modules @phensley/cldr-core/lib/systems/calendars/gregorianmath).
 */
public class GregorianMathTest {

  // ymdToJD(year, month, day, 12) — expected Julian days from TS 1.15.0

  @Test
  public void testYmdToJD() {
    ck(-4712, 1, 1, 0, "Jan 1 4713 BC (JD_MIN)");
    ck(-408, 1, 1, 1572036L, "BC year");
    ck(-72, 12, 29, 1695123L, "BC year, Dec 29");
    ck(-1, 12, 31, 1721057L, "2 BC Dec 31");
    ck(0, 1, 1, 1721058L, "1 BC Jan 1");
    ck(0, 2, 29, 1721117L, "1 BC leap day (Julian)");
    ck(0, 12, 31, 1721423L, "1 BC Dec 31");
    ck(1, 1, 1, 1721424L, "1 AD Jan 1");
    ck(1, 2, 28, 1721482L, "1 AD Feb 28");
    ck(1, 2, 29, 1721482L, "1 AD Feb 29 clamps to Feb 28");
    ck(4, 2, 29, 1722578L, "4 AD Feb 29 (Julian leap)");
    ck(4, 3, 1, 1722579L, "4 AD Mar 1");
    ck(100, 2, 29, 1757642L, "100 AD Feb 29 (Julian leap)");
    ck(-400, 3, 1, 1575018L, "BC year, month >= 3");
    ck(-1, 1, 1, 1720693L, "2 BC Jan 1 (Jan adjustment to prev year)");
    ck(1500, 2, 29, 2268992L, "1500 Feb 29 (pre-cutover Julian leap)");

    // Gregorian cutover boundary, Oct 4 1582 (Julian) -> Oct 15 1582 (Gregorian)
    ck(1582, 1, 1, 2298884L, "1582 Jan 1");
    ck(1582, 9, 30, 2299156L, "1582 Sep 30");
    ck(1582, 10, 4, 2299160L, "1582 Oct 4, last Julian day");
    ck(1582, 10, 5, 2299161L, "1582 Oct 5 Julian (skipped day)");
    ck(1582, 10, 14, 2299170L, "1582 Oct 14 Julian");
    ck(1582, 10, 15, 2299161L, "1582 Oct 15, first Gregorian day");
    ck(1582, 10, 16, 2299162L, "1582 Oct 16 Gregorian");
    ck(1582, 12, 31, 2299238L, "1582 Dec 31");
    ck(1583, 1, 1, 2299239L, "1583 Jan 1 Gregorian");

    // Leap century years
    ck(1900, 2, 28, 2415079L, "1900 Feb 28");
    ck(1900, 2, 29, 2415079L, "1900 Feb 29 clamps to Feb 28 (not leap)");
    ck(1970, 1, 1, 2440588L, "Unix epoch");
    ck(2000, 2, 28, 2451603L, "2000 Feb 28");
    ck(2000, 2, 29, 2451604L, "2000 Feb 29 (leap)");
    ck(2000, 12, 31, 2451910L, "2000 Dec 31");
    ck(8652, 12, 31, 4881503L, "8652 Dec 31 (JD_MAX)");

    // Month/day clamping
    ck(2020, 13, 1, 2459185L, "month 13 clamps to Dec");
    ck(2020, 0, 5, 2458854L, "month 0 clamps to Jan");
    ck(2020, 6, 0, 2459002L, "day 0 clamps to 1");
    ck(2020, 6, 40, 2459031L, "day 40 clamps to 30");
    ck(2019, 2, 31, 2458543L, "2019 Feb 31 clamps to Feb 28");
  }

  private void ck(long y, long m, long d, long expected, String label) {
    assertEquals(GregorianMath.ymdToJD(y, m, d, 12), expected, label);
  }

  @Test
  public void testMonthStart() {
    ckMS(2000, 0, 2451544L, "2000 Jan");
    ckMS(2000, 11, 2451879L, "2000 Dec");
    ckMS(1900, 0, 2415020L, "1900 Jan");
    ckMS(1583, 0, 2299238L, "1583 Jan");
    ckMS(1582, 9, 2299146L, "1582 Oct (Julian)");
    ckMS(1582, 10, 2299177L, "1582 Nov");
    ckMS(1500, 11, 2269267L, "1500 Dec");
    ckMS(1, 0, 1721423L, "1 Jan");
    ckMS(0, 0, 1721057L, "1 BC Jan");
    ckMS(-1, 0, 1720692L, "2 BC Jan");
    ckMS(-4712, 0, -1L, "4713 BC Jan");
  }

  private void ckMS(long eyear, long month, long expected, String label) {
    assertEquals(GregorianMath.monthStart(eyear, month, false), expected, label);
  }

  @Test
  public void testDaysInMonth() {
    ckDIM(2000, 0, 31, "2000 Jan");
    ckDIM(2000, 1, 29, "2000 Feb (leap)");
    ckDIM(1900, 1, 28, "1900 Feb (century, not leap)");
    ckDIM(1582, 9, 31, "1582 Oct");
    ckDIM(1500, 1, 29, "1500 Feb (Julian leap)");
    ckDIM(100, 1, 29, "100 Feb (Julian leap)");
    ckDIM(4, 1, 29, "4 Feb (Julian leap)");
    ckDIM(0, 1, 29, "1 BC Feb (Julian leap)");
    ckDIM(1970, 0, 31, "1970 Jan");
    ckDIM(2020, 11, 31, "2020 Dec");
  }

  private void ckDIM(long y, long m, int expected, String label) {
    assertEquals(GregorianMath.daysInMonth(y, m), expected, label);
  }

  @Test
  public void testLeapGregorian() {
    assertTrue(GregorianMath.leapGregorian(2000), "2000 leap");
    assertFalse(GregorianMath.leapGregorian(1900), "1900 not leap");
    assertTrue(GregorianMath.leapGregorian(1600), "1600 leap");
    assertFalse(GregorianMath.leapGregorian(1700), "1700 not leap");
    assertTrue(GregorianMath.leapGregorian(1972), "1972 leap");
    assertFalse(GregorianMath.leapGregorian(1582), "cutover year not leap");
    assertFalse(GregorianMath.leapGregorian(1), "1 not leap");
    // Before the cutover the Julian calendar applies: leap every 4 years
    assertTrue(GregorianMath.leapGregorian(1500), "1500 Julian leap");
    assertTrue(GregorianMath.leapGregorian(100), "100 Julian leap");
    assertTrue(GregorianMath.leapGregorian(4), "4 Julian leap");
    assertTrue(GregorianMath.leapGregorian(0), "0 Julian leap");
    assertTrue(GregorianMath.leapGregorian(-4), "-4 Julian leap");
    assertTrue(GregorianMath.leapGregorian(-72), "-72 Julian leap");
  }

  // initGregorianFields roundtrips from (julian day -> fields), TS 1.15.0

  @Test
  public void testInitGregorianFields() {
    ckIGF(2299160, 1582, 10, 4, 277, 0, "1582-10-04 Julian cutover day");
    ckIGF(2299161, 1582, 10, 15, 288, 0, "1582-10-15 first Gregorian day");
    ckIGF(0, -4712, 1, 1, 1, 1, "JD_MIN = Jan 1 4713 BC");
    ckIGF(1721058, 0, 1, 1, 1, 1, "1 BC Jan 1");
    ckIGF(1695123, -72, 12, 29, 364, 1, "-72 Dec 29");
    ckIGF(2298884, 1582, 1, 1, 1, 0, "1582-01-01 (Julian)");
    ckIGF(2415020, 1899, 12, 31, 365, 0, "1899-12-31");
    ckIGF(2451545, 2000, 1, 1, 1, 1, "2000-01-01");
    ckIGF(2451604, 2000, 2, 29, 60, 1, "2000-02-29");
    ckIGF(4881503, 8652, 12, 31, 366, 1, "JD_MAX = 8652-12-31");
  }

  private void ckIGF(long jd, long eyear, long month, long dom, long doy, long isLeap, String label) {
    long[] f = new long[DateField.LENGTH];
    f[DateField.JULIAN_DAY] = jd;
    GregorianMath.initGregorianFields(f);
    assertEquals(f[DateField.EXTENDED_YEAR], eyear, label + " extendedYear");
    assertEquals(f[DateField.MONTH], month, label + " month");
    assertEquals(f[DateField.DAY_OF_MONTH], dom, label + " dayOfMonth");
    assertEquals(f[DateField.DAY_OF_YEAR], doy, label + " dayOfYear");
    assertEquals(f[DateField.IS_LEAP], isLeap, label + " isLeap");
    long era = eyear < 1 ? 0 : 1;
    assertEquals(f[DateField.ERA], era, label + " era");
    assertEquals(f[DateField.YEAR], era == 0 ? 1 - eyear : eyear, label + " year");
  }

}
