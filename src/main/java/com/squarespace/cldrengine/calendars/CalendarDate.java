package com.squarespace.cldrengine.calendars;

import static com.squarespace.compiler.parse.Pair.pair;

import java.util.Arrays;
import java.util.List;

import com.squarespace.cldrengine.internal.DateTimePatternFieldType;
import com.squarespace.compiler.parse.Pair;

public abstract class CalendarDate {

  private static final long NULL = Long.MAX_VALUE;

  private static final List<Pair<Integer, DateTimePatternFieldType>> DIFFERENCE_FIELDS = Arrays.asList(
      pair(DateField.YEAR, DateTimePatternFieldType.YEAR),
      pair(DateField.MONTH, DateTimePatternFieldType.MONTH),
      pair(DateField.DAY_OF_MONTH, DateTimePatternFieldType.DAY),
      pair(DateField.AM_PM, DateTimePatternFieldType.DAYPERIOD),
      pair(DateField.HOUR, DateTimePatternFieldType.HOUR),
      pair(DateField.MINUTE, DateTimePatternFieldType.MINUTE)
      );

  protected final long[] fields = new long[DateField.LENGTH];
  protected final CalendarType type;
  protected final int firstDay;
  protected final int minDays;
  protected ZoneInfo zoneInfo; // set in subclass

  protected CalendarDate(CalendarType type, int firstDay, int minDays) {
    this.type = type;
    this.firstDay = firstDay;
    this.minDays = minDays;

    // Compute week fields on demand.
    this.fields[DateField.WEEK_OF_YEAR] = NULL;
    this.fields[DateField.YEAR_WOY] = NULL;
  }

  /**
   * Calendar type for this date, e.g. 'gregory' for Gregorian.
   */
  public CalendarType type() {
    return this.type;
  }

  /**
   * Unix epoch with no timezone offset.
   */
  public long unixEpoch() {
    return this.fields[DateField.LOCAL_MILLIS] + this.zoneInfo.offset;
  }


  public int firstDayOfWeek() {
    return this.firstDay;
  }

  public int minDaysInFirstWeek() {
    return this.minDays;
  }

  /**
   * Returns a floating point number representing the real Julian Day, UTC.
   */
  public double julianDay() {
    double ms = (this.fields[DateField.MILLIS_IN_DAY] + this.zoneInfo.offset) / (double)CalendarConstants.ONE_DAY_MS;
    return ((double)this.fields[DateField.JULIAN_DAY] - 0.5) + ms;
  }

  /**
   * CLDR's modified Julian day used as the basis for all date calculations.
   */
  public long modifiedJulianDay() {
    return this.fields[DateField.JULIAN_DAY];
  }

  public long era() {
    return this.fields[DateField.ERA];
  }

  public long extendedYear() {
    return this.fields[DateField.EXTENDED_YEAR];
  }

  public long year() {
    return this.fields[DateField.YEAR];
  }

  public long relatedYear() {
    return this.fields[DateField.EXTENDED_YEAR];
  }

  public long yearOfWeekOfYear()  {
    this.computeWeekFields();
    return this.fields[DateField.YEAR_WOY];
  }

  public long weekOfYear() {
    this.computeWeekFields();
    return this.fields[DateField.WEEK_OF_YEAR];
  }

  /**
   * Ordinal month, one-based, e.g. Gregorian JANUARY = 1.
   */
  public long month() {
    return this.fields[DateField.MONTH];
  }

  /**
   * Returns the week of the month computed using the locale's 'first day
   * of week' and 'minimal days in first week' where applicable.
   *
   * For example, for the United States, weeks start on Sunday.
   * Saturday 9/1/2018 would be in week 1, and Sunday 9/2/2018 would
   * begin week 2.
   *
   *         September
   *   Su Mo Tu We Th Fr Sa
   *                      1
   *    2  3  4  5  6  7  8
   *    9 10 11 12 13 14 15
   *   16 17 18 19 20 21 22
   *   23 24 25 26 27 28 29
   *   30
   */
  public long weekOfMonth() {
    this.computeWeekFields();
    return this.fields[DateField.WEEK_OF_MONTH];
  }

  public long dayOfYear() {
    return this.fields[DateField.DAY_OF_YEAR];
  }

  /**
   * Day of the week. 1 = SUNDAY, 2 = MONDAY, ..., 7 = SATURDAY
   */
  public long dayOfWeek() {
    return this.fields[DateField.DAY_OF_WEEK];
  }

  /**
   * Ordinal day of the week. 1 if this is the 1st day of the week,
   * 2 if the 2nd, etc. Depends on the local starting day of the week.
   */
  public long ordinalDayOfWeek() {
    long weekday = this.dayOfWeek();
    long firstDay = this.firstDayOfWeek();
    return (7 - firstDay + weekday) % 7 + 1;
  }

  /**
   * Ordinal number indicating the day of the week in the current month.
   * The result of this method can be used to format messages like
   * "2nd Sunday in August".
   */
  public long dayOfWeekInMonth() {
    this.computeWeekFields();
    return this.fields[DateField.DAY_OF_WEEK_IN_MONTH];
  }

  public long dayOfMonth() {
    return this.fields[DateField.DAY_OF_MONTH];
  }

  public boolean isAM() {
    return this.fields[DateField.AM_PM] == 0;
  }

  /**
   * Indicates the hour of the morning or afternoon, used for the 12-hour
   * clock (0 - 11). Noon and midnight are 0, not 12.
   */
  public long hour() {
    return this.fields[DateField.HOUR];
  }

  /**
   * Indicates the hour of the day, used for the 24-hour clock (0 - 23).
   * Noon is 12 and midnight is 0.
   */
  public long hourOfDay() {
    return this.fields[DateField.HOUR_OF_DAY];
  }

  /**
   * Indicates the minute of the hour (0 - 59).
   */
  public long minute() {
    return this.fields[DateField.MINUTE];
  }

  /**
   * Indicates the second of the minute (0 - 59).
   */
  public long second() {
    return this.fields[DateField.SECOND];
  }

  public long milliseconds() {
    return this.fields[DateField.MILLIS];
  }

  public long millisecondsInDay() {
    return this.fields[DateField.MILLIS_IN_DAY];
  }

  public String metaZoneId() {
    return this.zoneInfo.metaZoneId;
  }

  public String timeZoneId() {
    return this.zoneInfo.timeZoneId;
  }

  public String timeZoneStableId() {
    return this.zoneInfo.stableId;
  }

  public int timeZoneOffset() {
    return this.zoneInfo.offset;
  }

  public boolean isLeapYear() {
    return this.fields[DateField.IS_LEAP] == 1;
  }

  public boolean isDaylightSavings() {
    return this.zoneInfo.dst;
  }

  /**
   * Computes the field of visual difference between the two dates.
   * Note: This assumes the dates are of the same type and have the same
   * timezone offset.
   */
  public DateTimePatternFieldType fieldOfVisualDifference(CalendarDate other) {
    long[] a = this.fields;
    long[] b = other.fields;
    for (Pair<Integer, DateTimePatternFieldType> pair : DIFFERENCE_FIELDS) {
      int key = pair._1;
      if (a[key] != b[key]) {
        return pair._2;
      }
    }
    return DateTimePatternFieldType.SECOND;
  }

  public abstract CalendarDate add(CalendarDateFields fields);

  protected abstract int monthCount();

  protected Pair<Long, Long> _add(CalendarDateFields fields) {
    // All day calculations will be relative to the current day of the month.
    double dom = this.fields[DateField.DAY_OF_MONTH] + fields.day + (fields.week * 7);

    // Adjust the extended year and month. Note: month may be fractional here,
    // but will be <= 12 after modulus the year.
    int mc = this.monthCount();
    long months = (long)Math.floor(fields.year * mc);
    double month = (this.fields[DateField.MONTH] - 1) + fields.month + months;

    long yadd = (long) Math.floor(month / mc);
    long year = this.fields[DateField.EXTENDED_YEAR] + yadd;
    month -= yadd * mc;

    Pair<Long, Double> time = this._addTime(fields);
    double jd = this.monthStart(year, month, false) + dom + time._1;
    long ijd = (long)Math.floor(jd);
    double djd = jd - ijd;

    // Calculate ms and handle rollover
    long _ms = Math.round(time._2 * (djd * CalendarConstants.ONE_DAY_MS));
    if (_ms >= CalendarConstants.ONE_DAY_MS) {
      ijd++;
      _ms -= CalendarConstants.ONE_DAY_MS;
    }

    return Pair.pair(ijd, _ms);
  }

  protected Pair<Long, Double> _addTime(CalendarDateFields fields) {
    double msDay = this.fields[DateField.MILLIS_IN_DAY] - this.timeZoneOffset();
    msDay += (fields.hour * CalendarConstants.ONE_HOUR_MS)
        + (fields.millis * CalendarConstants.ONE_MINUTE_MS)
        + (fields.second * CalendarConstants.ONE_SECOND_MS)
        + fields.millis;
    long days = (long)Math.floor(msDay / CalendarConstants.ONE_DAY_MS);
    double ms = msDay - (days * CalendarConstants.ONE_DAY_MS);
    return Pair.pair(days, ms);
  }

  protected void initFromUnixEpoch(long ms, String zoneId) {
    zoneId = TimeZoneData.substituteZoneAlias(zoneId);
    this.zoneInfo = TimeZoneData.zoneInfoFromUTC(zoneId, ms);
    jdFromUnixEpoch(ms + this.zoneInfo.offset, this.fields);
    computeBaseFields(this.fields);
  }

  protected void initFromJD(long jd, long msDay, String zoneId) {
    long unixEpoch = unixEpochFromJD(jd, msDay);
    this.initFromUnixEpoch(unixEpoch, zoneId);
  }

  protected String _toString(String type) {
    return this._toString(type, Long.toString(this.year()));
  }

  protected String _toString(String type, String year) {
    year = year == null ? Long.toString(this.year()) : year;
    return String.format("%s %s-%02d-%02d %02d:%02d:%02d.%03d %s",
        type,
        year,
        this.month(),
        this.dayOfMonth(),
        this.hourOfDay(),
        this.minute(),
        this.second(),
        this.milliseconds(),
        this.zoneInfo.timeZoneId);
  }

  /**
   * Compute WEEK_OF_YEAR and YEAR_WOY on demand.
   */
  protected void computeWeekFields() {
    long[] f = this.fields;
    if (f[DateField.YEAR_WOY] != NULL) {
      return;
    }

    long eyear = f[DateField.EXTENDED_YEAR];
    long dow = f[DateField.DAY_OF_WEEK];
    long dom = f[DateField.DAY_OF_MONTH];
    long doy = f[DateField.DAY_OF_YEAR];

    long ywoy = eyear;
    long rdow = (dow + 7 - this.firstDay) % 7;
    long rdowJan1 = (dow - doy + 7001 - this.firstDay) % 7;
    long woy = Math.floorDiv((doy - 1 + rdowJan1), 7);
    if ((7 - rdowJan1) >= this.minDays) {
      woy++;
    }

    if (woy == 0) {
      long prevDay = doy + this.yearLength(eyear - 1);
      woy = this.weekNumber(prevDay, prevDay, dow);
      ywoy--;
    } else {
      long lastDoy = this.yearLength(eyear);
      if (doy >= (lastDoy - 5)) {
        long lastRdow = (rdow + lastDoy - doy) % 7;
        if (lastRdow < 0) {
          lastRdow += 7;
        }
        if (((6 - lastRdow) >= this.minDays) && ((doy + 7 - rdow) > lastDoy)) {
          woy = 1;
          ywoy++;
        }
      }
    }
    f[DateField.WEEK_OF_MONTH] = this.weekNumber(dom, dom, dow);
    f[DateField.WEEK_OF_YEAR] = woy;
    f[DateField.YEAR_WOY] = ywoy;
    f[DateField.DAY_OF_WEEK_IN_MONTH] = ((dom - 1) / 7 | 0) + 1;
  }

  protected long yearLength(long y) {
    return this.monthStart(y + 1, 0, false) - this.monthStart(y, 0, false);
  }

  protected long weekNumber(long desiredDay, long dayOfPeriod, long dayOfWeek) {
    long psow = (dayOfWeek - this.firstDay - dayOfPeriod + 1) % 7;
    if (psow < 0) {
      psow += 7;
    }
    long weekNo = Math.floorDiv(desiredDay + psow - 1, 7);
    return ((7 - psow) >= this.minDays) ? weekNo + 1 : weekNo;
  }

  protected abstract long monthStart(long eyear, double month, boolean useMonth);

  /**
   * Compute Julian day from timezone-adjusted Unix epoch milliseconds.
   */
  protected void jdFromUnixEpoch(long ms, long[] f) {
    long oneDayMS = CalendarConstants.ONE_DAY_MS;
    long days = Math.floorDiv(ms, oneDayMS);
    long jd = days + CalendarConstants.JD_UNIX_EPOCH;
    long msDay = ms - (days * oneDayMS);

    f[DateField.JULIAN_DAY] = jd;
    f[DateField.MILLIS_IN_DAY] = msDay;
  }

  /**
   * Given a Julian day and local milliseconds (in UTC), return the Unix
   * epoch milliseconds UTC.
   */
  protected long unixEpochFromJD (long jd, long msDay) {
    long days = jd - CalendarConstants.JD_UNIX_EPOCH;
    return (days * CalendarConstants.ONE_DAY_MS) + msDay;
  }

  /**
   * Compute fields common to all calendars. Before calling this, we must
   * have the JULIAN_DAY and MILLIS_IN_DAY fields set. Every calculation
   * is relative to these.
   */
  protected void computeBaseFields(long[] f) {
    long jd = f[DateField.JULIAN_DAY];
    checkJDRange(jd);

    long msDay = f[DateField.MILLIS_IN_DAY];
    long ms = msDay + ((jd - CalendarConstants.JD_UNIX_EPOCH) * CalendarConstants.ONE_DAY_MS);

    f[DateField.LOCAL_MILLIS] = ms;
    f[DateField.JULIAN_DAY] = jd;
    f[DateField.MILLIS_IN_DAY] = msDay;
    f[DateField.MILLIS] = msDay % 1000;

    msDay = msDay / 1000 | 0;
    f[DateField.SECOND] = msDay % 60;

    msDay = msDay / 60 | 0;
    f[DateField.MINUTE] = msDay % 60;

    msDay = msDay / 60 | 0;
    f[DateField.HOUR_OF_DAY] = msDay;
    f[DateField.AM_PM] = msDay / 12 | 0;
    f[DateField.HOUR] = msDay % 12;

    long dow = (jd + DayOfWeek.MONDAY) % 7;
    if (dow < DayOfWeek.SUNDAY) {
      dow += 7;
    }
    f[DateField.DAY_OF_WEEK] = dow;
  }

  protected long checkJDRange(long jd) {
    // TODO: emit warning?

//  throw new Error(
//  `Julian day ${jd} is outside the supported range of this library: ` +
//  `${ConstantsDesc.JD_MIN} to ${ConstantsDesc.JD_MAX}`);

    if (jd < CalendarConstants.JD_MIN) {
      return CalendarConstants.JD_MIN;
    }
    return jd > CalendarConstants.JD_MAX ? CalendarConstants.JD_MAX : jd;
  }

}
