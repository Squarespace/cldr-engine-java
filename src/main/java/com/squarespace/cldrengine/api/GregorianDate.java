package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.internal.GregorianMath;

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
        ._initFromJD(result._1, Math.round(result._2), this.timeZoneId());
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
    return GregorianMath.daysInMonth(year, month);
  }

  @Override
  protected int daysInYear(long year) {
    return GregorianMath.leapGregorian(year) ? 366 : 365;
  }

  @Override
  protected int monthCount() {
    return 12;
  }

  @Override
  public String toString() {
    return this._toString("Gregorian");
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

  @Override
  protected void initFields(long[] f) {
    GregorianMath.initGregorianFields(f);
  }

  @Override
  protected long monthStart(long eyear, double month, boolean useMonth) {
    return GregorianMath.monthStart(eyear, month, useMonth);
  }

}
