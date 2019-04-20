package com.squarespace.cldrengine.calendars;

import com.squarespace.compiler.parse.Pair;

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
  public BuddhistDate add(CalendarDateFields fields) {
    String zoneId = fields.zoneId == null ? this.timeZoneId() : fields.zoneId;
    Pair<Long, Long> result = this._add(fields);
    return new BuddhistDate(this.firstDay, this.minDays)
        ._initFromJD(result._1, result._2, zoneId);
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
    computeBuddhistFields(this.fields);
    return this;
  }

  protected BuddhistDate _initFromJD(long jd, long msDay, String zoneId) {
    super.initFromJD(jd, msDay, zoneId);
    computeBuddhistFields(this.fields);
    return this;
  }

  private void computeBuddhistFields(long[] f) {
    f[DateField.ERA] = 0;
    f[DateField.YEAR] = f[DateField.EXTENDED_YEAR] - CalendarConstants.BUDDHIST_ERA_START;
  }
}
