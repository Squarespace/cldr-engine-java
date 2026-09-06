package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.internal.CalendarMath;

/**
 * A date in the Buddhist calendar.
 *
 * type: buddhist
 */
public class BuddhistDate extends GregorianDate {

  // TS buddhist.ts ZEROS: defaults applied by fromFields (no zoneId).
  private static final CalendarDateFields ZEROS = CalendarDateFields.build()
      .year(1).month(1).day(1).hour(0).minute(0).second(0).millis(0);

  protected BuddhistDate(int firstDay, int minDays) {
    super(CalendarType.BUDDHIST, firstDay, minDays);
  }

  @Override
  public BuddhistDate add(TimePeriod fields) {
    Pair<Long, Long> result = CalendarMath.add(this, fields, (y, m) -> this.monthStart(y, m, false));
    return this._new()._initFromJD(result._1, result._2, this.timeZoneId());
  }

  @Override
  public BuddhistDate subtract(TimePeriod fields) {
    return add(invertPeriod(fields));
  }

  @Override
  public String toString() {
    return this._toString("Buddhist");
  }

  @Override
  public BuddhistDate withZone(String zoneId) {
    return this._new()._initFromUnixEpoch(this.unixEpoch(), zoneId);
  }

  /**
   * Construct a new date from the given fields.
   */
  public static BuddhistDate fromFields(CalendarDateFields fields, int firstDay, int minDays) {
    return new BuddhistDate(firstDay, minDays)._set(merge(ZEROS, fields));
  }

  public static BuddhistDate fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    return new BuddhistDate(firstDay, minDays)._initFromUnixEpoch(epoch, zoneId);
  }

  @Override
  protected BuddhistDate _new() {
    return new BuddhistDate(this.firstDay, this.minDays);
  }

  @Override
  protected BuddhistDate _set(CalendarDateFields f) {
    // TS buddhist.ts casts `_set(...) as BuddhistDate`; `_new()` inside
    // `_set` dispatches virtually so the result is always a BuddhistDate.
    return (BuddhistDate) super._set(f);
  }

  protected BuddhistDate _initFromUnixEpoch(long epoch, String zoneId) {
    super.initFromUnixEpoch(epoch, zoneId);
    this.initFields(this.fields);
    return this;
  }

  protected BuddhistDate _initFromJD(long jd, long msDay, String zoneId) {
    super.initFromJD(jd, msDay, zoneId);
    this.initFields(this.fields);
    return this;
  }

  protected void initFields(long[] f) {
    super.initFields(f);
    computeBuddhistFields(f);
  }

  private void computeBuddhistFields(long[] f) {
    f[DateField.ERA] = 0;
    f[DateField.YEAR] = f[DateField.EXTENDED_YEAR] - CalendarConstants.BUDDHIST_ERA_START;
  }
}
