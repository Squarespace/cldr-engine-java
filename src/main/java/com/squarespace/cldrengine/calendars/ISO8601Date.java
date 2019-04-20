package com.squarespace.cldrengine.calendars;

import com.squarespace.compiler.parse.Pair;

public class ISO8601Date extends GregorianDate {

  protected ISO8601Date() {
    // ISO-8601 dates use hard-coded firstDay and minDays
    super(CalendarType.ISO8601, DayOfWeek.MONDAY, CalendarConstants.ISO8601_MIN_DAYS);
  }

  @Override
  public ISO8601Date add(CalendarDateFields fields) {
    String zoneId = fields.zoneId == null ? this.timeZoneId() : fields.zoneId;
    Pair<Long, Long> result = this._add(fields);
    return new ISO8601Date()._initFromJD(result._1, result._2, zoneId);
  }

  @Override
  public String toString() {
    return this._toString("ISO8601");
  }

  public static ISO8601Date fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    return new ISO8601Date()._initFromUnixEpoch(epoch, zoneId);
  }

  protected ISO8601Date _initFromJD(long jd, long msDay, String zoneId) {
    super.initFromJD(jd, msDay, zoneId);
    super.initGregorian();
    return this;
  }

  protected ISO8601Date _initFromUnixEpoch(long epoch, String zoneId) {
    super.initFromUnixEpoch(epoch, zoneId);
    return this;
  }
}

