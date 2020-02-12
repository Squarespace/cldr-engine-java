package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.utils.MathUtil;

/**
 * Construct a date using the rules of the Persian calendar.
 *
 * type: persian
 */
public class PersianDate extends CalendarDate {

  public PersianDate(int firstDay, int minDays) {
    super(CalendarType.PERSIAN, firstDay, minDays);
  }

  @Override
  public long relatedYear() {
    return this.fields[DateField.EXTENDED_YEAR] + 622;
  }

  @Override
  public CalendarDate add(TimePeriod fields) {
    Pair<Long, Double> result = this._add(fields);
    PersianDate d = new PersianDate(this.firstDay, this.minDays);
    d._initFromJD(result._1, Math.round(result._2), this.timeZoneId());
    return d;
  }

  @Override
    public CalendarDate subtract(TimePeriod fields) {
      return this.add(invertPeriod(fields));
    }

  @Override
  public CalendarDate withZone(String zoneId) {
    PersianDate d = new PersianDate(this.firstDay, this.minDays);
    d._initFromUnixEpoch(this.unixEpoch(), zoneId);
    return d;
  }

  @Override
  protected int daysInMonth(long year, int month) {
    return MONTH_COUNT[month][leapPersian(year) ? 1 : 0];
  }

  @Override
  protected int daysInYear(long year) {
    return leapPersian(year) ? 366 : 365;
  }

  @Override
  protected int monthCount() {
    return 12;
  }

  @Override
  public String toString() {
    return _toString("Persian");
  }

  public static PersianDate fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    return new PersianDate(firstDay, minDays)._initFromUnixEpoch(epoch, zoneId);
  }

  protected PersianDate _initFromUnixEpoch(long epoch, String zoneId) {
    super.initFromUnixEpoch(epoch, zoneId);
    this.initFields(this.fields);
    return this;
  }

  protected PersianDate _initFromJD(long jd, long msDay, String zoneId) {
    super.initFromJD(jd, msDay, zoneId);
    this.initFields(this.fields);
    return this;
  }

  @Override
  protected void initFields(long[] f) {
    this.computePersianFields(f);
  }

  @Override
  protected long monthStart(long eyear, double month, boolean useMonth) {
    long jd = CalendarConstants.JD_PERSIAN_EPOCH - 1 + 365 * (eyear - 1) +
          (long)Math.floor((8 * eyear + 21) / 33);
    if (month != 0) {
      int m = (int)Math.floor(month);
      double d = month - m;

      jd += MONTH_COUNT[m][2];
      // Check if there is a fractional month part, and if so add the number
      // of the days in the next month multiplied by the fraction
      if (d != 0) {
        // number of days in Esfand determined by:
        // "number of days between two vernal equinoxes"
        boolean isLeap = leapPersian(eyear - 1);

        // note: the 'month' parameter must always be <= # months in the calendar
        // year, so <= 12 in this case.
        jd += d * MONTH_COUNT[m + 1][isLeap ? 1 : 0];
      }

    }
    return jd;
  }

  private void computePersianFields(long[] f) {
    long jd = f[DateField.JULIAN_DAY];
    long days = jd - CalendarConstants.JD_PERSIAN_EPOCH;
    long year = 1 + (long)Math.floor((33 * days + 3) / 12053);
    long favardin1 = 365 * (year - 1) + (long)Math.floor((8 * year + 21) / 33);
    long doy = days - favardin1;
    int month = (int)Math.floor(doy < 216 ? (doy / 31) : ((doy - 6) / 30));
    long dom = doy - MONTH_COUNT[month][2] + 1;

    f[DateField.ERA] = 0;
    f[DateField.YEAR] = year;
    f[DateField.EXTENDED_YEAR] = year;
    f[DateField.MONTH] = month + 1;
    f[DateField.DAY_OF_MONTH] = dom;
    f[DateField.DAY_OF_YEAR] = doy + 1;
    f[DateField.IS_LEAP] = leapPersian(year) ? 1 : 0;
  }

  private boolean leapPersian(long year) {
    long[] rem = new long[] { 0 };
    MathUtil.floorDiv(25 * year + 11, 33, rem);
    return rem[0] < 8;
  }

  private static final int[][] MONTH_COUNT = new int[][] {
      new int[] { 31, 31, 0   }, // Farvardin
      new int[] { 31, 31, 31  }, // Ordibehesht
      new int[] { 31, 31, 62  }, // Khordad
      new int[] { 31, 31, 93  }, // Tir
      new int[] { 31, 31, 124 }, // Mordad
      new int[] { 31, 31, 155 }, // Shahrivar
      new int[] { 30, 30, 186 }, // Mehr
      new int[] { 30, 30, 216 }, // Aban
      new int[] { 30, 30, 246 }, // Azar
      new int[] { 30, 30, 276 }, // Dey
      new int[] { 30, 30, 306 }, // Bahman
      new int[] { 29, 30, 336 }  // Esfand
  };
}
