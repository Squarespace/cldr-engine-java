package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.calendars.DayOfWeek;
import com.squarespace.cldrengine.internal.CalendarMath;

public class ISO8601Date extends GregorianDate {

  protected ISO8601Date() {
    // ISO-8601 dates use hard-coded firstDay and minDays
    super(CalendarType.ISO8601, DayOfWeek.MONDAY, CalendarConstants.ISO8601_MIN_DAYS);
  }

  @Override
  public ISO8601Date add(TimePeriod fields) {
    Pair<Long, Long> result = CalendarMath.add(this, fields, (y, m) -> this.monthStart(y, m, false));
    return this._new()._initFromJD(result._1, result._2, this.timeZoneId());
  }

  @Override
  public ISO8601Date subtract(TimePeriod fields) {
    return add(invertPeriod(fields));
  }

  @Override
  public String toString() {
    return this._toString("ISO8601");
  }

  @Override
  public ISO8601Date withZone(String zoneId) {
    return this._new()._initFromUnixEpoch(this.unixEpoch(), zoneId);
  }

  /**
   * Construct a new date from the given fields. Note this is a copy of the
   * TS 1.15.0 behavior: no ZEROS defaults are merged, so unset fields flow
   * into the ymdToJD clamps exactly as in TS.
   */
  public static ISO8601Date fromFields(CalendarDateFields fields, int firstDay, int minDays) {
    return new ISO8601Date()._set(fields);
  }

  @Override
  protected ISO8601Date _new() {
    return new ISO8601Date();
  }

  @Override
  protected ISO8601Date _initFromUnixEpoch(long epoch, String zoneId) {
    // TS iso8601.ts casts `super.initFromUnixEpoch(...) as ISO8601Date`
    return (ISO8601Date) super._initFromUnixEpoch(epoch, zoneId);
  }

  @Override
  protected ISO8601Date _initFromJD(long jd, long msDay, String zoneId) {
    // TS iso8601.ts casts `super.initFromJD(...) as ISO8601Date`
    return (ISO8601Date) super._initFromJD(jd, msDay, zoneId);
  }

  @Override
  protected ISO8601Date _set(CalendarDateFields f) {
    // TS iso8601.ts casts `_set(...) as ISO8601Date`; `_new()` inside
    // `_set` dispatches virtually so the result is always an ISO8601Date.
    return (ISO8601Date) super._set(f);
  }

  public static ISO8601Date fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    ISO8601Date d = new ISO8601Date();
    d._initFromUnixEpoch(epoch, zoneId);
    return d;
  }

}

