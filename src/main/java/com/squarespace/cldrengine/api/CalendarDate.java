package com.squarespace.cldrengine.api;


import static com.squarespace.cldrengine.api.Pair.of;

import java.util.Arrays;
import java.util.List;

import com.squarespace.cldrengine.calendars.TimeZoneData;
import com.squarespace.cldrengine.calendars.ZoneInfo;
import com.squarespace.cldrengine.internal.CalendarMath;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;
import com.squarespace.cldrengine.internal.MathFix;


public abstract class CalendarDate {

  private static final long NULL = Long.MAX_VALUE;

  private static final List<Pair<Integer, DateTimePatternFieldType>> DIFFERENCE_FIELDS = Arrays.asList(
      of(DateField.ERA, DateTimePatternFieldType.ERA),
      of(DateField.YEAR, DateTimePatternFieldType.YEAR),
      of(DateField.MONTH, DateTimePatternFieldType.MONTH),
      of(DateField.DAY_OF_MONTH, DateTimePatternFieldType.DAY),
      of(DateField.AM_PM, DateTimePatternFieldType.DAYPERIOD),
      of(DateField.HOUR, DateTimePatternFieldType.HOUR24),
      of(DateField.MINUTE, DateTimePatternFieldType.MINUTE),
      of(DateField.SECOND, DateTimePatternFieldType.SECOND)
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

    // TS initializes _zoneInfo to the UTC zone in the constructor; this
    // keeps set()/fields() usable on a date that has not yet been
    // initialized from an epoch.
    this.zoneInfo = TimeZoneData.zoneInfoFromUTC("UTC", 0);

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
    return this.fields[DateField.LOCAL_MILLIS] - this.zoneInfo.offset;
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
    double ms = (this.fields[DateField.MILLIS_IN_DAY] - this.zoneInfo.offset) / (double)CalendarConstants.ONE_DAY_MS;
    return (this.fields[DateField.JULIAN_DAY] - 0.5) + ms;
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

  public long yearOfWeekOfYearISO() {
    this.computeWeekFields();
    return this.fields[DateField.ISO_YEAR_WOY];
  }

  public long weekOfYearISO() {
    this.computeWeekFields();
    return this.fields[DateField.ISO_WEEK_OF_YEAR];
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

  /**
   * Return all of the date and time field values.
   */
  public CalendarDateFields fields() {
    return CalendarDateFields.build()
        .year(this.extendedYear())
        .month(this.month())
        .day(this.dayOfMonth())
        .hour(this.hourOfDay())
        .minute(this.minute())
        .second(this.second())
        .millis(this.milliseconds())
        .zoneId(this.timeZoneId());
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

  public String timeZoneAbbr() {
    return this.zoneInfo.abbr;
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
    return null;
  }

  /**
   * Compare two dates a and b, returning:
   *   a < b  ->  -1
   *   a = b  ->  0
   *   a > b  ->  1
   */
  public int compare(CalendarDate other) {
    long a = this.unixEpoch();
    long b = other.unixEpoch();
    return a < b ? -1 : a > b ? 1 : 0;
  }

  /**
   * Calculate the relative time between two dates. If a field is specified
   * the time will be calculated in terms of that single field. Otherwise
   * the field will be auto-selected; WEEK is never auto-selected, it only
   * appears when requested via {@code field} or {@code includeWeeks}.
   *
   * The result is signed: positive when {@code other} is after this date,
   * negative when it is before (the 1.13.0 behavior change; callers must
   * not flip the sign themselves).
   *
   * Port of calendar.ts:505-521 (tag v1.15.0).
   */
  public Pair<TimePeriodField, Double> relativeTime(CalendarDate other, TimePeriodField field, DateDiffOptions options) {
    if (options == null) {
      options = DateDiffOptions.build();
    }
    TimePeriod result;

    // If no field is specified, calculate the difference in all fields and select the largest.
    if (field == null) {
      DateDiffOptions copy = options.copy().rollupFractional(false);
      // TS: { ...options, fields: undefined } — clear any fields restriction.
      copy.fields((List<TimePeriodField>) null);
      result = CalendarDifference.difference(this, other, copy);
      field = relativeField(result);
    }

    // Calculate the difference in terms of the requested or auto-selected field.
    result = CalendarDifference.difference(this, other, options.copy().fields(Arrays.asList(field)));
    double value = periodField(result, field);
    return Pair.of(field, value != 0.0 ? value : 0.0);
  }

  /**
   * Calculate the period of time (years, months, days, ..) since the given date.
   *
   * In a normal call, "other is less-than-or-equal-to this".
   * If "this is less-than other" we invert the sign of the result.
   *
   * For example:
   *
   *   "2024-03-03".since("2024-01-30") == "1 month 4 days"
   *   "2024-01-30".since("2024-03-03") == "-1 month -3 days"
   *
   * The durations are not symmetrical due to calendar math, e.g. end-of-month alignment
   * with months of different lengths.
   */
  public TimePeriod since(CalendarDate other, DateDiffOptions options) {
    return this.invertPeriod(CalendarDifference.difference(this, other, options));
  }

  /**
   * Calculate the period of time (years, months, days, ..) until the given date.
   *
   * In a normal call, "this is less-than-or-equal-to other".
   * If "other is less-than this" we invert the sign of the result.
   *
   * For example:
   *
   *   "2024-01-30".until("2024-03-30") == "1 month 3 days"
   *   "2024-03-03".until("2024-01-30") == "-1 month -4 days"
   *
   * The duration are not symmetrical due to calendar math, e.g. end-of-month alignment
   * with months of different lengths.
   */
  public TimePeriod until(CalendarDate other, DateDiffOptions options) {
    return CalendarDifference.difference(this, other, options);
  }

  /**
   * Calculate the time period between two dates. Note this returns the
   * absolute value of the difference.
   */
  public TimePeriod difference(CalendarDate other, DateDiffOptions options) {
    TimePeriod result = CalendarDifference.difference(this, other, options);
    return this.compare(other) == 1 ? this.invertPeriod(result) : result;
  }

  /**
   * Calculate the time period between two dates. If 'other' is before this date,
   * the time period fields will be negative.
   */
  public TimePeriod differenceSigned(CalendarDate other, DateDiffOptions options) {
    return this.until(other, options);
  }

  /**
   * Set one or more fields on this date explicitly, and return a new date.
   *
   * Note: when setting the 'year' field you must use the "extended year".
   * For example, the extended year 0 is 1 B.C in the Gregorian calendar.
   */
  public abstract CalendarDate set(CalendarDateFields fields);

  /**
   * Start of date, e.g. {@code date.startOf('day')}.
   */
  public CalendarDate startOf(CalendarDateModFields field) {
    switch (field) {
      case YEAR:
        return this.set(CalendarDateFields.build()
            .month(0).day(0).hour(0).minute(0).second(0).millis(0));
      case MONTH:
        return this.set(CalendarDateFields.build()
            .day(0).hour(0).minute(0).second(0).millis(0));
      case DAY:
        return this.set(CalendarDateFields.build()
            .hour(0).minute(0).second(0).millis(0));
      case HOUR:
        return this.set(CalendarDateFields.build()
            .minute(0).second(0).millis(0));
      case MINUTE:
        return this.set(CalendarDateFields.build()
            .second(0).millis(0));
      case SECOND:
        return this.set(CalendarDateFields.build().millis(0));
      default:
        throw new IllegalArgumentException("unsupported field: " + field);
    }
  }

  /**
   * End of date, e.g. {@code date.endOf('day')}.
   */
  public CalendarDate endOf(CalendarDateModFields field) {
    switch (field) {
      case YEAR:
        return this.set(CalendarDateFields.build()
            .month(13).day(32).hour(23).minute(59).second(59).millis(999));
      case MONTH:
        return this.set(CalendarDateFields.build()
            .day(32).hour(23).minute(59).second(59).millis(999));
      case DAY:
        return this.set(CalendarDateFields.build()
            .hour(23).minute(59).second(59).millis(999));
      case HOUR:
        return this.set(CalendarDateFields.build()
            .minute(59).second(59).millis(999));
      case MINUTE:
        return this.set(CalendarDateFields.build()
            .second(59).millis(999));
      case SECOND:
        return this.set(CalendarDateFields.build().millis(999));
      default:
        throw new IllegalArgumentException("unsupported field: " + field);
    }
  }

  /**
   * Rollup just the time fields into number of milliseconds. Assumes all
   * time fields are defined, otherwise treated as zero.
   */
  protected double _timeToMs(CalendarDateFields f) {
    // TS: (x || 0) then clamp then | 0. Option.or(0.0) mirrors the short-
    // circuit, the (int) cast mirrors ToInt32 truncation (values are clamped
    // to the integral range 0-999 so the truncation is exact).
    double hour = (int)clamp(f.hour.or(0.0), 0, 23);
    double minute = (int)clamp(f.minute.or(0.0), 0, 59);
    double second = (int)clamp(f.second.or(0.0), 0, 59);
    double millis = (int)clamp(f.millis.or(0.0), 0, 999);
    return (hour * CalendarConstants.ONE_HOUR_MS) +
        (minute * CalendarConstants.ONE_MINUTE_MS) +
        (second * CalendarConstants.ONE_SECOND_MS) +
        millis;
  }

  private static double clamp(double n, double min, double max) {
    return Math.min(max, Math.max(min, n));
  }

  /**
   * TS object-spread merge over CalendarDateFields: copy base, then apply
   * each option that is set on {@code over}.
   */
  public static CalendarDateFields merge(CalendarDateFields base, CalendarDateFields over) {
    CalendarDateFields result = base.copy();
    if (over.year.ok()) {
      result.year(over.year);
    }
    if (over.month.ok()) {
      result.month(over.month);
    }
    if (over.day.ok()) {
      result.day(over.day);
    }
    if (over.hour.ok()) {
      result.hour(over.hour);
    }
    if (over.minute.ok()) {
      result.minute(over.minute);
    }
    if (over.second.ok()) {
      result.second(over.second);
    }
    if (over.millis.ok()) {
      result.millis(over.millis);
    }
    if (over.zoneId.ok()) {
      result.zoneId(over.zoneId);
    }
    return result;
  }

  public abstract CalendarDate add(TimePeriod fields);
  public abstract CalendarDate subtract(TimePeriod fields);
  public abstract CalendarDate withZone(String zoneId);

  protected abstract CalendarDate _new();

  protected abstract void initFields(long[] f);
  protected abstract int monthCount();
  protected abstract int daysInMonth(long year, int month);
  protected abstract int daysInYear(long year);
  protected abstract long monthStart(long eyear, double month, boolean useMonth);

  /**
   * Automatically select the best field to represent the time period.
   * Iterates the fields in TIME_PERIOD_FIELDS order (year, month, week,
   * day, hour, minute, second, millis — the TimePeriodField enum is declared
   * in that order) and returns the first field whose value is non-zero (TS
   * truthiness: -0.0 is falsy, so {@code != 0.0} matches). WEEK is not
   * special-cased: the all-fields difference only computes a week value when
   * weeks were requested.
   *
   * Port of calendar.ts:86-93 (tag v1.15.0).
   */
  private static TimePeriodField relativeField(TimePeriod p) {
    for (TimePeriodField f : TimePeriodField.values()) {
      if (periodField(p, f) != 0.0) {
        return f;
      }
    }
    return TimePeriodField.MILLIS;
  }

  private static double periodField(TimePeriod p, TimePeriodField field) {
    switch (field) {
      case YEAR:
        return p.year;
      case MONTH:
        return p.month;
      case WEEK:
        return p.week;
      case DAY:
        return p.day;
      case HOUR:
        return p.hour;
      case MINUTE:
        return p.minute;
      case SECOND:
        return p.second;
      default:
        return p.millis;
    }
  }

  protected TimePeriod invertPeriod(TimePeriod f) {
    TimePeriod r = new TimePeriod();
    r.year(invert(f.year));
    r.month(invert(f.month));
    r.week(invert(f.week));
    r.day(invert(f.day));
    r.hour(invert(f.hour));
    r.minute(invert(f.minute));
    r.second(invert(f.second));
    r.millis(invert(f.millis));
    return r;
  }

  private double invert(double v) {
    return v == 0 ? v : -v;
  }

  /**
   * Compute a new Julian day and milliseconds UTC by updating one or more fields.
   */
  protected void initFromUnixEpoch(long ms, String zoneId) {
    this.zoneInfo = CalendarMath.initBaseFromUnixEpoch(this.fields, ms, zoneId);
  }

  protected void initFromJD(long jd, long msDay, String zoneId) {
    long unixEpoch = CalendarMath.unixEpochFromJD(jd, msDay);
    this.initFromUnixEpoch(unixEpoch, zoneId);
  }

  protected String _toString(String type) {
    long year = this.extendedYear();
    boolean neg = year < 0;
    return String.format("%s %s%04d-%02d-%02d %02d:%02d:%02d.%03d %s",
        type,
        neg ? "-" : "",
        Math.abs(year),
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

    long dow = f[DateField.DAY_OF_WEEK];
    long dom = f[DateField.DAY_OF_MONTH];
    long doy = f[DateField.DAY_OF_YEAR];
    f[DateField.WEEK_OF_MONTH] = weekNumber(this.firstDay, this.minDays,  dom, dom, dow);
    f[DateField.DAY_OF_WEEK_IN_MONTH] = ((dom - 1) / 7 | 0) + 1;

    // compute locale
    this._computeWeekFields(DateField.WEEK_OF_YEAR, DateField.YEAR_WOY, this.firstDay, this.minDays, dow, dom, doy);

    // compute ISO
    this._computeWeekFields(DateField.ISO_WEEK_OF_YEAR, DateField.ISO_YEAR_WOY, 2, 4, dow, dom, doy);
  }

  protected void _computeWeekFields(int woyfield, int ywoyfield, long firstDay, long minDays, long dow, long dom, long doy) {
    long[] f = this.fields;
    long eyear = f[DateField.EXTENDED_YEAR];

    long ywoy = eyear;
    long rdow = (dow + 7 - firstDay) % 7;
    long rdowJan1 = (dow - doy + 7001 - firstDay) % 7;
    long woy = MathFix.floorDiv((doy - 1 + rdowJan1), 7);
    if (7 - rdowJan1 >= minDays) {
      woy++;
    }

    if (woy == 0) {
      long prevDay = doy + this.yearLength(eyear - 1);
      woy = weekNumber(firstDay, minDays, prevDay, prevDay, dow);
      ywoy--;
    } else {
      long lastDoy = this.yearLength(eyear);
      if (doy >= lastDoy - 5) {
        long lastRdow = (rdow + lastDoy - doy) % 7;
        if (lastRdow < 0) {
          lastRdow += 7;
        }
        if (6 - lastRdow >= minDays && doy + 7 - rdow > lastDoy) {
          woy = 1;
          ywoy++;
        }
      }
    }
    f[woyfield] = woy;
    f[ywoyfield] = ywoy;
  }

  protected long yearLength(long y) {
    return this.monthStart(y + 1, 0, false) - this.monthStart(y, 0, false);
  }

  protected static long weekNumber(long firstDay, long minDays, long desiredDay, long dayOfPeriod, long dayOfWeek) {
    long psow = (dayOfWeek - firstDay - dayOfPeriod + 1) % 7;
    if (psow < 0) {
      psow += 7;
    }
    long weekNo = MathFix.floorDiv(desiredDay + psow - 1, 7);
    return ((7 - psow) >= minDays) ? weekNo + 1 : weekNo;
  }

//  protected long checkJDRange(long jd) {
//    // TODO: emit warning?
//
////  throw new Error(
////  `Julian day ${jd} is outside the supported range of this library: ` +
////  `${ConstantsDesc.JD_MIN} to ${ConstantsDesc.JD_MAX}`);
//
//    if (jd < CalendarConstants.JD_MIN) {
//      return CalendarConstants.JD_MIN;
//    }
//    return jd > CalendarConstants.JD_MAX ? CalendarConstants.JD_MAX : jd;
//  }

}
