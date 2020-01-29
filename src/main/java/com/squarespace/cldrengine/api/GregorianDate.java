package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.utils.MathUtil;
import com.squarespace.cldrengine.utils.Pair;

/**
 * Construct a date using the rules of the Gregorian calendar.
 *
 * type: gregory
 */
public class GregorianDate extends CalendarDate {

  protected GregorianDate(CalendarType type, int firstDay, int minDays) {
    super(type, firstDay, minDays);
  }

  public static GregorianDate fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    return new GregorianDate(CalendarType.GREGORY, firstDay, minDays)._initFromUnixEpoch(epoch, zoneId);
  }

  @Override
  public GregorianDate add(TimePeriod fields) {
    Pair<Long, Double> result = this._add(fields);
    return new GregorianDate(CalendarType.GREGORY, this.firstDay, this.minDays)
        ._initFromJD(result._1, (long)result._2.doubleValue(), this.timeZoneId());
  }

  @Override
  public GregorianDate subtract(TimePeriod fields) {
    return add(invertPeriod(fields));
  }

  @Override
  public GregorianDate withZone(String zoneId) {
    GregorianDate d = new GregorianDate(CalendarType.GREGORY, this.firstDay, this.minDays);
    return d._initFromUnixEpoch(this.unixEpoch(), zoneId);
  }

  @Override
  protected int daysInMonth(long year, int month) {
    return MONTH_COUNT[month][leapGregorian(year) ? 1 : 0];
  }

  @Override
  protected int daysInYear(long year) {
    return leapGregorian(year) ? 366 : 365;
  }

  @Override
  protected int monthCount() {
    return 12;
  }

  @Override
  public String toString() {
    return this._toString("Gregorian", null);
  }

  protected GregorianDate _initFromUnixEpoch(long epoch, String zoneId) {
    super.initFromUnixEpoch(epoch, zoneId);
    this.initFields(this.fields);
    return this;
  }

  protected GregorianDate _initFromJD(long jd, long msDay, String zoneId) {
    super.initFromJD(jd, msDay, zoneId);
    this.initFields(this.fields);
    return this;
  }

  protected void initFields(long[] f) {
    if (f[DateField.JULIAN_DAY] >= CalendarConstants.JD_GREGORIAN_CUTOVER) {
      computeGregorianFields(f);
    } else {
      computeJulianFields(f);
    }
    long year = f[DateField.EXTENDED_YEAR];
    long era = 1; // AD;
    if (year < 1) {
      era = 0;
      year = 1 - year;
    }
    f[DateField.ERA] = era;
    f[DateField.YEAR] = year;
  }

  protected long monthStart(long eyear, double month, boolean useMonth) {
    boolean isLeap = eyear % 4 == 0;
    long y = eyear - 1;
    long jd = 365 * y + Math.floorDiv(y, 4) + (CalendarConstants.JD_GREGORIAN_EPOCH - 3);
    if (eyear >= CalendarConstants.JD_GREGORIAN_CUTOVER_YEAR) {
      isLeap = isLeap && ((eyear % 100 != 0) || (eyear % 400 == 0));
      jd += Math.floorDiv(y, 400) - Math.floorDiv(y, 100) + 2;
    }
    if (month != 0) {
      int m = (int)Math.floor(month);
      double d = month - (double)m;
      jd += MONTH_COUNT[m][isLeap ? 3 : 2];

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
   * Compute fields for dates on or after the Gregorian cutover.
   */
  protected void computeGregorianFields(long[] f) {
    long ged = f[DateField.JULIAN_DAY] - CalendarConstants.JD_GREGORIAN_EPOCH;
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
    int month = (int)Math.floor((12 * (doy + corr) + 6) / 367);
    long dom = doy - MONTH_COUNT[month][isLeap ? 3 : 2] + 1;

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
  protected void computeJulianFields(long[] f) {
    long jed = f[DateField.JULIAN_DAY] - (CalendarConstants.JD_GREGORIAN_EPOCH - 2);
    long eyear = (long)Math.floor((4 * jed + 1464) / 1461);
    long jan1 = 365 * (eyear - 1) + (long)Math.floor((eyear - 1) / 4);
    long doy = jed - jan1;
    boolean isLeap = eyear % 4 == 0;
    long corr = 0;
    long mar1 = isLeap ? 60 : 59;
    if (doy >= mar1) {
      corr = isLeap ? 1 : 2;
    }

    int month = (int)Math.floor((12 * (doy + corr) + 6) / 365);
    long dom = doy - MONTH_COUNT[month][isLeap ? 3 : 2] + 1;

    f[DateField.EXTENDED_YEAR] = eyear;
    f[DateField.MONTH] = month + 1;
    f[DateField.DAY_OF_MONTH] = dom;
    f[DateField.DAY_OF_YEAR] = doy + 1;
    f[DateField.IS_LEAP] = isLeap ? 1 : 0;
  }

  /**
   * Return true if the given year is a leap year in the Gregorian calendar; false otherwise.
   * Note that we switch to the Julian calendar at the Gregorian cutover year.
   */
  protected boolean leapGregorian(long year) {
    boolean r = year % 4 == 0;
    if (year >= CalendarConstants.JD_GREGORIAN_CUTOVER_YEAR) {
      r = r && ((year % 100 != 0) || (year % 400 == 0));
    }
    return r;
  }

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

}
