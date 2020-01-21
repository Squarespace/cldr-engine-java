package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.utils.Pair;

/**
 * A date in the Buddhist calendar.
 *
 * type: buddhist
 */
public class BuddhistDate extends GregorianDate {

  protected BuddhistDate(int firstDay, int minDays) {
    super(CalendarType.BUDDHIST, firstDay, minDays);
  }

  @Override
  public BuddhistDate add(TimePeriod fields) {
    Pair<Long, Double> result = this._add(fields);
    return new BuddhistDate(this.firstDay, this.minDays)
        ._initFromJD(result._1, (long)result._2.doubleValue(), this.timeZoneId());
  }

  @Override
  public String toString() {
    return this._toString("Buddhist");
  }

  public static BuddhistDate fromUnixEpoch(long epoch, String zoneId, int firstDay, int minDays) {
    return new BuddhistDate(firstDay, minDays)._initFromUnixEpoch(epoch, zoneId);
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
