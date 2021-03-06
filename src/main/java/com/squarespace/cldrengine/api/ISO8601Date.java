package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.calendars.DayOfWeek;

public class ISO8601Date extends GregorianDate {

  protected ISO8601Date() {
    // ISO-8601 dates use hard-coded firstDay and minDays
    super(CalendarType.ISO8601, DayOfWeek.MONDAY, CalendarConstants.ISO8601_MIN_DAYS);
  }

  @Override
  public ISO8601Date add(TimePeriod fields) {
    Pair<Long, Double> result = this._add(fields);
    ISO8601Date d = new ISO8601Date();
    d._initFromJD(result._1, Math.round(result._2), this.timeZoneId());
    return d;
  }

  @Override
  public String toString() {
    return this._toString("ISO8601");
  }

  public static ISO8601Date fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    ISO8601Date d = new ISO8601Date();
    d._initFromUnixEpoch(epoch, zoneId);
    return d;
  }

}

