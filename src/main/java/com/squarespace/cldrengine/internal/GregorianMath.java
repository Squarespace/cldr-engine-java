package com.squarespace.cldrengine.internal;

import static com.squarespace.cldrengine.api.CalendarConstants.JD_GREGORIAN_CUTOVER;
import static com.squarespace.cldrengine.api.CalendarConstants.JD_GREGORIAN_CUTOVER_DAY;
import static com.squarespace.cldrengine.api.CalendarConstants.JD_GREGORIAN_CUTOVER_MONTH;
import static com.squarespace.cldrengine.api.CalendarConstants.JD_GREGORIAN_CUTOVER_YEAR;
import static com.squarespace.cldrengine.api.CalendarConstants.JD_GREGORIAN_EPOCH;

import com.squarespace.cldrengine.api.DateField;
import com.squarespace.cldrengine.utils.MathUtil;

/**
 * Port of @phensley/cldr-core systems/calendars/gregorianmath.ts (internal in TS).
 */
public class GregorianMath {

  private static final int[][] MONTH_COUNT = new int[][] {
    new int[] { 31,  31,   0,   0 }, // Jan
    new int[] { 28,  29,  31,  31 }, // Feb
    new int[] { 31,  31,  59,  60 }, // Mar
    new int[] { 30,  30,  90,  91 }, // Apr
    new int[] { 31,  31, 120, 121 }, // May
    new int[] { 30,  30, 151, 152 }, // Jun
    new int[] { 31,  31, 181, 182 }, // Jul
    new int[] { 31,  31, 212, 213 }, // Aug
    new int[] { 30,  30, 243, 244 }, // Sep
    new int[] { 31,  31, 273, 274 }, // Oct
    new int[] { 30,  30, 304, 305 }, // Nov
    new int[] { 31,  31, 334, 335 }  // Dec
  };

  /**
   * Initialize the fields for a Gregorian calendar date. For dates on or after
   * the Gregorian cutover the proleptic Gregorian calendar is used, otherwise
   * dates are computed using the proleptic Julian calendar.
   */
  public static void initGregorianFields(long[] f) {
    if (f[DateField.JULIAN_DAY] >= JD_GREGORIAN_CUTOVER) {
      computeGregorianFields(f);
    } else {
      // We use Julian calendar for dates before the Gregorian cutover
      computeJulianFields(f);
    }

    // Set era and year based on extended year
    long year = f[DateField.EXTENDED_YEAR];
    long era = 1; // AD
    if (year < 1) {
      era = 0;
      year = 1 - year;
    }
    f[DateField.ERA] = era;
    f[DateField.YEAR] = year;
  }

  /**
   * Compute fields for dates on or after the Gregorian cutover.
   */
  public static void computeGregorianFields(long[] f) {
    long ged = f[DateField.JULIAN_DAY] - JD_GREGORIAN_EPOCH;
    long[] rem = new long[1];
    long n400 = MathUtil.floorDiv(ged, 146097, rem);
    long n100 = MathUtil.floorDiv(rem[0], 36524, rem);
    long n4 = MathUtil.floorDiv(rem[0], 1461, rem);
    long n1 = MathUtil.floorDiv(rem[0], 365, rem);

    long year = 400 * n400 + 100 * n100 + 4 * n4 + n1;
    long doy = rem[0]; // 0-based day of year
    if (n100 == 4 || n1 == 4) {
      doy = 365;
    } else {
      ++year;
    }
    boolean isLeap = leapGregorian(year);
    long corr = 0;
    long mar1 = isLeap ? 60 : 59;
    if (doy >= mar1) {
      corr = isLeap ? 1 : 2;
    }
    long month = MathFix.floorDiv(12 * (doy + corr) + 6, 367);
    long dom = doy - MONTH_COUNT[(int)month][isLeap ? 3 : 2] + 1;

    f[DateField.EXTENDED_YEAR] = year;
    f[DateField.MONTH] = month + 1;
    f[DateField.DAY_OF_MONTH] = dom;
    f[DateField.DAY_OF_YEAR] = doy + 1;
    f[DateField.IS_LEAP] = isLeap ? 1 : 0;
  }

  /**
   * Compute fields for dates before the Gregorian cutover using the proleptic
   * Julian calendar. Any Gregorian date before October 15, 1582 is really a
   * date on the proleptic Julian calendar, with leap years every 4 years.
   */
  public static void computeJulianFields(long[] f) {
    long jed = f[DateField.JULIAN_DAY] - (JD_GREGORIAN_EPOCH - 2);
    long eyear = MathFix.floorDiv(4 * jed + 1464, 1461);
    long jan1 = 365 * (eyear - 1) + MathFix.floorDiv(eyear - 1, 4);
    long doy = jed - jan1;
    boolean isLeap = eyear % 4 == 0;
    long corr = 0;
    long mar1 = isLeap ? 60 : 59;
    if (doy >= mar1) {
      corr = isLeap ? 1 : 2;
    }

    long month = MathFix.floorDiv(12 * (doy + corr) + 6, 367);
    long dom = doy - MONTH_COUNT[(int)month][isLeap ? 3 : 2] + 1;

    f[DateField.EXTENDED_YEAR] = eyear;
    f[DateField.MONTH] = month + 1;
    f[DateField.DAY_OF_MONTH] = dom;
    f[DateField.DAY_OF_YEAR] = doy + 1;
    f[DateField.IS_LEAP] = isLeap ? 1 : 0;
  }

  /**
   * Returns the number of days in the given month for the given year. The month must be 0-based.
   */
  public static int daysInMonth(long y, long m) {
    return MONTH_COUNT[(int)m][leapGregorian(y) ? 1 : 0];
  }

  /**
   * The Julian day number one day before the start of the given month.
   */
  public static long monthStart(long eyear, double month, boolean useMonth) {
    // TS: (eyear | 0) — ToInt32 wrap; (int) cast gives the identical low-32-bit wrap
    boolean isLeap = ((int) eyear) % 4 == 0;
    long y = eyear - 1;
    long jd = 365 * y + MathFix.floorDiv(y, 4) + (JD_GREGORIAN_EPOCH - 3);
    if (eyear >= JD_GREGORIAN_CUTOVER_YEAR) {
      isLeap = isLeap && (eyear % 100 != 0 || eyear % 400 == 0);
      jd += MathFix.floorDiv(y, 400) - MathFix.floorDiv(y, 100) + 2;
    }
    if (month != 0) {
      int m = (int)Math.floor(month);
      double d = month - m;
      jd += MONTH_COUNT[m][isLeap ? 3 : 2];

      // TODO: we never reach the block below since all internal uses of monthStart
      // pass in an integer

      // Check if there is a fractional month part, and if so add the number
      // of the days in the next month multiplied by the fraction
      if (d != 0) {
        // note: the 'month' parameter must always be <= # months in the calendar
        // year, so <= 12 in this case.
        jd += d * MONTH_COUNT[m + 1][isLeap ? 1 : 0];
      }
    }
    return jd;
  }

  /**
   * Return true if the given year is a leap year in the Gregorian calendar; false otherwise.
   * Note that we switch to the Julian calendar at the Gregorian cutover year.
   */
  public static boolean leapGregorian(long y) {
    boolean r = y % 4 == 0;
    if (y >= JD_GREGORIAN_CUTOVER_YEAR) {
      r = r && (y % 100 != 0 || y % 400 == 0);
    }
    return r;
  }

  /**
   * Convert integer (year, month, day) to Julian day.
   */
  public static long ymdToJD(long y, long m, long d, long monthCount) {
    // TS: y |= 0 — ToInt32 wrap; a long->int cast gives the identical low-32-bit wrap
    y = (int) y;
    boolean leap = leapGregorian(y);
    m = m < 1 ? 1 : m > monthCount ? monthCount : m;
    long dc = MONTH_COUNT[(int)m - 1][leap ? 1 : 0];
    d = d < 1 ? 1 : d > dc ? dc : d;

    // Adjustment due to Gregorian calendar switch on Oct 4, 1582 -> Oct 15, 1582
    if (y < JD_GREGORIAN_CUTOVER_YEAR
        || (y == JD_GREGORIAN_CUTOVER_YEAR
            && (m < JD_GREGORIAN_CUTOVER_MONTH
                || (m == JD_GREGORIAN_CUTOVER_MONTH && d < JD_GREGORIAN_CUTOVER_DAY)))) {
      if (m < 3) {
        m += 12;
        y -= 1;
      }
      return 1721117 + MathFix.floorDiv(1461 * y, 4) + MathFix.floorDiv(153 * m - 457, 5) + d;
    }

    // TS: ((14 - m) / 12) | 0 — truncation toward zero; the quotient is a small
    // non-negative integer, identical to long division.
    long a = (14 - m) / 12;
    y = y + 4800 - a;
    m = m + 12 * a - 3;
    // TS: | 0 idioms below are truncation-toward-zero. This branch only executes
    // for years >= 1582, so every operand is positive and Java long division is
    // identical to ToInt32(trunc(x)) for the magnitudes involved.
    return d + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
  }

}
