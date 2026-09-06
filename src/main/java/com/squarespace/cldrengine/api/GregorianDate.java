package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.calendars.TimeZoneData;
import com.squarespace.cldrengine.internal.CalendarMath;
import com.squarespace.cldrengine.internal.GregorianMath;

/**
 * Construct a date using the rules of the Gregorian calendar.
 *
 * type: gregory
 */
public class GregorianDate extends CalendarDate {

  // TS gregorian.ts ZEROS: defaults applied by fromFields (no zoneId).
  private static final CalendarDateFields ZEROS = CalendarDateFields.build()
      .year(1970).month(1).day(1).hour(0).minute(0).second(0).millis(0);

  protected GregorianDate(CalendarType type, int firstDay, int minDays) {
    super(type, firstDay, minDays);
  }

  public static GregorianDate fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    return new GregorianDate(CalendarType.GREGORY, firstDay, minDays)._initFromUnixEpoch(epoch, zoneId);
  }

  @Override
  public GregorianDate add(TimePeriod fields) {
    Pair<Long, Long> result = CalendarMath.add(this, fields, (y, m) -> this.monthStart(y, m, false));
    return this._new()._initFromJD(result._1, result._2, this.timeZoneId());
  }

  @Override
  public GregorianDate subtract(TimePeriod fields) {
    return add(invertPeriod(fields));
  }

  /**
   * Set fields.
   */
  @Override
  public GregorianDate set(CalendarDateFields fields) {
    return this._set(merge(this.fields(), fields));
  }

  /**
   * Construct a new date from the given fields.
   */
  public static GregorianDate fromFields(CalendarDateFields fields, int firstDay, int minDays) {
    return new GregorianDate(CalendarType.GREGORY, firstDay, minDays)._set(merge(ZEROS, fields));
  }

  @Override
  public GregorianDate withZone(String zoneId) {
    return this._new()._initFromUnixEpoch(this.unixEpoch(), zoneId);
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

  protected GregorianDate _new() {
    return new GregorianDate(CalendarType.GREGORY, this.firstDay, this.minDays);
  }

  /**
   * Set fields, applying timezone-aware wall clock resolution.
   */
  protected GregorianDate _set(CalendarDateFields f) {
    long jd = GregorianMath.ymdToJD(
        f.year.or(0.0).longValue(), f.month.or(0.0).longValue(), f.day.or(0.0).longValue(), this.monthCount());
    double ms = this._timeToMs(f);
    long epoch = CalendarMath.unixEpochFromJD(jd, Math.round(ms));
    String zoneId = f.zoneId.ok() ? f.zoneId.get() : this.timeZoneId();
    // Find UTC epoch for wall clock time in the requested timezone
    Pair<Long, ?> r = TimeZoneData.fromWall(zoneId, epoch);
    return this._new()._initFromUnixEpoch(r != null ? r._1.longValue() : epoch, zoneId);
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
