package com.squarespace.cldrengine.api;


import static com.squarespace.cldrengine.api.Pair.of;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.calendars.DayOfWeek;
import com.squarespace.cldrengine.calendars.TimeZoneData;
import com.squarespace.cldrengine.calendars.ZoneInfo;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;

import lombok.AllArgsConstructor;

public abstract class CalendarDate {

  private static final long NULL = Long.MAX_VALUE;

  private static final List<Pair<Integer, DateTimePatternFieldType>> DIFFERENCE_FIELDS = Arrays.asList(
      of(DateField.YEAR, DateTimePatternFieldType.YEAR),
      of(DateField.MONTH, DateTimePatternFieldType.MONTH),
      of(DateField.DAY_OF_MONTH, DateTimePatternFieldType.DAY),
      of(DateField.AM_PM, DateTimePatternFieldType.DAYPERIOD),
      of(DateField.HOUR, DateTimePatternFieldType.HOUR),
      of(DateField.MINUTE, DateTimePatternFieldType.MINUTE)
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
   * the field of greatest difference will be used.
   */
  public Pair<TimePeriodField, Double> relativeTime(CalendarDate other, TimePeriodField field) {
    Swap swap = this.swap(other);
    TimePeriod diff = this._diff(swap.start, swap.startFields, swap.endFields);
    TimePeriodField _field = field;
    if (_field == null) {
      _field = largestRelativeField(diff);
    }
    TimePeriod res = this._rollup(diff, swap.startFields, swap.endFields, Arrays.asList(_field));
    Double value = getRelativeField(res, _field);
    return Pair.of(_field, value);
  }

  /**
   * Calculate the time period between two dates. Note this returns the
   * absolute value.
   */
  public TimePeriod difference(CalendarDate other, List<TimePeriodField> fields) {
    Swap swap = this.swap(other);
    TimePeriod res = this._diff(swap.start, swap.startFields, swap.endFields);
    System.out.println("res " + res);
    if (fields != null) {
      return this._rollup(res, swap.startFields, swap.endFields, fields);
    }
    return res;
  }

  public abstract CalendarDate add(TimePeriod fields);
  public abstract CalendarDate subtract(TimePeriod fields);
  public abstract CalendarDate withZone(String zoneId);

  protected abstract void initFields(long[] f);
  protected abstract int monthCount();
  protected abstract int daysInMonth(long year, int month);
  protected abstract int daysInYear(long year);
  protected abstract long monthStart(long eyear, double month, boolean useMonth);

  protected TimePeriodField largestRelativeField(TimePeriod p) {
    if (p.year.ok() && p.year.get() != 0.0) {
      return TimePeriodField.YEAR;
    }
    if (p.month.ok() && p.month.get() != 0.0) {
      return TimePeriodField.MONTH;
    }
    if (p.week.ok() && p.week.get() != 0.0) {
      return TimePeriodField.WEEK;
    }
    if (p.day.ok() && p.day.get() != 0.0) {
      return TimePeriodField.DAY;
    }
    if (p.hour.ok() && p.hour.get() != 0.0) {
      return TimePeriodField.HOUR;
    }
    if (p.minute.ok() && p.minute.get() != 0.0) {
      return TimePeriodField.MINUTE;
    }
    if (p.second.ok() && p.second.get() != 0.0) {
      return TimePeriodField.SECOND;
    }
    return TimePeriodField.MILLIS;
  }

  protected double getRelativeField(TimePeriod period, TimePeriodField field) {
    switch (field) {
      case YEAR:
        return period.year.or(0.0);
      case MONTH:
        return period.month.or(0.0);
      case WEEK:
        return period.week.or(0.0);
      case DAY:
        return period.day.or(0.0);
      case HOUR:
        return period.hour.or(0.0);
      case MINUTE:
        return period.minute.or(0.0);
      case SECOND:
        return period.second.or(0.0);
      default:
        return period.millis.or(0.0);
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

  private Double invert(Option<Double> d) {
    if (d.ok()) {
      double v = d.get();
      return v == 0 ? v : -v;
    }
    return null;
  }

  @AllArgsConstructor
  protected static class Swap {
    CalendarDate start;
    long[] startFields;
    CalendarDate end;
    long[] endFields;
  }

  protected Swap swap(CalendarDate other) {
    CalendarDate start = this;
    CalendarDate end = other;
    if (this.compare(other) == 1) {
      CalendarDate tmp = start;
      start = end;
      end = tmp;
    }
    return new Swap(start, start.utcfields(), end, end.utcfields());
  }

  /**
   * Roll up time period fields into a subset of fields.
   */
  protected TimePeriod _rollup(TimePeriod span, long[] sf, long[] ef, List<TimePeriodField> fields) {
    int f = timePeriodFieldFlags(fields);
    if (f == 0) {
      return span;
    }

    int mc = this.monthCount();

    double year = span.year.or(0.0);
    double month = span.month.or(0.0);
    double day = (span.week.or(0.0) * 7) + span.day.or(0.0);
    double ms = (span.hour.or(0.0) * CalendarConstants.ONE_HOUR_MS) +
        (span.minute.or(0.0) * CalendarConstants.ONE_MINUTE_MS) +
        (span.second.or(0.0) * CalendarConstants.ONE_SECOND_MS) +
        span.millis.or(0.0);

    if (((f & FLAG_YEAR) != 0) && ((f & FLAG_MONTH) != 0)) {
      // Both year and month were requested, so use their integer values.

    } else if ((f & FLAG_MONTH) != 0) {
      // Month was requested so convert years into months
      month += year * mc;
      year = 0;

    } else if ((f & FLAG_YEAR) != 0 && month > 0) {
      // Year was requested so convert months into days

      // This is a little more verbose but necessary to accurately convert
      // months into days. Example:
      //
      //  2001-03-11  and 2001-09-09   5 months and 29 days apart
      //  == (last month days) + (full month days) + (first month days)
      //  == 9 + 31 + 31 + 30 + 31 + 30 + (31 - 11)
      //  == 182 days

      long endy = ef[DateField.EXTENDED_YEAR];
      long endm = ef[DateField.MONTH] - 1;

      // TODO: create a cursor for year/month calculations to reduce
      // the verbosity of this block

      // Subtract the number of days to find the "day of month"
      // relative to each of the months to be converted.
      double dom = ef[DateField.DAY_OF_MONTH] - day;
      if (dom < 0) {
        endm--;
        if (endm < 0) {
          endm += mc;
          endy--;
        }
        dom += this.daysInMonth(endy, (int)endm);
      }

      // Convert each month except the last into days
      double tmpd = dom;
      while (month > 1) {
        endm--;
        if (endm < 0) {
          endm += mc;
          endy--;
        }
        tmpd += this.daysInMonth(endy, (int)endm);
        month--;
      }

      // Convert the last month into days
      endm--;
      if (endm < 0) {
        endm += mc;
        endy--;
      }

      tmpd += this.daysInMonth(endy, (int)endm) - dom;
      day += tmpd;
      month = 0;

    } else {
      // Neither year nor month were requested, so we ignore those parts
      // of the time period, and re-calculate the days directly from the
      // original date fields.
      day = ef[DateField.JULIAN_DAY] - sf[DateField.JULIAN_DAY];
      ms = ef[DateField.MILLIS_IN_DAY] - sf[DateField.MILLIS_IN_DAY];
      if (ms < 0) {
        day--;
        ms += CalendarConstants.ONE_DAY_MS;
      }
      year = month = 0;
    }

    // We have integer year, month, and millis computed at this point

    ms += CalendarConstants.ONE_DAY_MS * day;
    day = 0;

    long onedy = CalendarConstants.ONE_DAY_MS;
    long onewk = onedy * 7;
    long onehr = CalendarConstants.ONE_HOUR_MS;
    long onemn = CalendarConstants.ONE_MINUTE_MS;

    double week = 0;
    double hour = 0;
    double minute = 0;
    double second = 0;
    double millis = 0;

    // Roll down
    if ((f & FLAG_WEEK) != 0) {
      week = ms / onewk;
      ms -= week * onewk;
    }
    if ((f & FLAG_DAY) != 0) {
      day = ms / onedy;
      ms -= day * onedy;
    }
    if ((f & FLAG_HOUR) != 0) {
      hour = ms / onehr;
      ms -= hour * onehr;
    }
    if ((f & FLAG_MINUTE) != 0) {
      minute = ms / onemn;
      ms -= minute * onemn;
    }
    if ((f & FLAG_SECOND) != 0) {
      second = ms / 1000;
      ms -= second * 1000;
    }
    if ((f & FLAG_MILLIS) != 0) {
      millis = ms;
    }

    double dayms = ms / CalendarConstants.ONE_DAY_MS;

    // Roll up fractional
    if (f < FLAG_MONTH) {
      // Days in the last year before adding the remaining fields
      long diy = this.daysInYear((long) (sf[DateField.EXTENDED_YEAR] + year));
      year += (day + dayms) / diy;
      day = 0;
    } else if (f < FLAG_WEEK) {
      long ey = ef[DateField.YEAR];
      long em = ef[DateField.MONTH] - 2;
      if (em < 0) {
        em += mc;
        ey--;
      }
      int dim = this.daysInMonth(ey, (int)em);
      month += (day + dayms) / dim;
    } else if (f < FLAG_DAY) {
      week += (day + dayms) / 7;
    } else if (f < FLAG_HOUR) {
      day += dayms;
    } else if (f < FLAG_MINUTE) {
      hour += ms / onehr;
    } else if (f < FLAG_SECOND) {
      minute += ms / onemn;
    } else if (f < FLAG_MILLIS) {
      second += ms / 1000;
    }

    return TimePeriod.build()
        .year(year)
        .month(month)
        .week(week)
        .day(day)
        .hour(hour)
        .minute(minute)
        .second(second)
        .millis(millis);
  }

  /**
   * Compute the number of years, months, days, etc, between two dates. The result will
   * have all fields as integers.
   */
  protected TimePeriod _diff(CalendarDate s, long[] sf, long[] ef) {
    // Use a borrow-based method to compute fields. If a field X is negative, we borrow
    // from the next-higher field until X is positive. Repeat until all fields are
    // positive.
    long millis = ef[DateField.MILLIS_IN_DAY] - sf[DateField.MILLIS_IN_DAY];
    long day = ef[DateField.DAY_OF_MONTH] - sf[DateField.DAY_OF_MONTH];
    long month = ef[DateField.MONTH] - sf[DateField.MONTH];
    long year = ef[DateField.EXTENDED_YEAR] - sf[DateField.EXTENDED_YEAR];

    // Convert days into milliseconds
    if (millis < 0) {
      millis += CalendarConstants.ONE_DAY_MS;
      day--;
    }

    // Convert months into days
    // This is a little more complex since months can have 28, 29 30 or 31 days.
    // We work backwards from the current month and successively convert months
    // into days until days are positive.
    int mc = s.monthCount();
    long m = ef[DateField.MONTH] - 1; // convert to 0-based month
    long y = ef[DateField.EXTENDED_YEAR];
    while (day < 0) {
      // move to previous month
      m--;
      // add back the number of days in the current month, wrapping around to December
      if (m < 0) {
        m += mc;
        y--;
      }
      int dim = this.daysInMonth(y, (int)m);
      day += dim;
      month--;
    }

    // Convert years into months
    if (month < 0) {
      month += mc;
      year--;
    }

    // Convert days to weeks
    long week = day > 0 ? day / 7 : 0;
    if (week > 0) {
      day -= week * 7;
    }

    // Break down milliseconds into components
    long hour = millis / CalendarConstants.ONE_HOUR_MS;
    millis -= hour * CalendarConstants.ONE_HOUR_MS;
    long minute = millis / CalendarConstants.ONE_MINUTE_MS;
    millis -= minute * CalendarConstants.ONE_MINUTE_MS;
    long second = millis / CalendarConstants.ONE_SECOND_MS;
    millis -= second * CalendarConstants.ONE_SECOND_MS;

    return TimePeriod.build()
        .year((double)year)
        .month((double)month)
        .week((double)week)
        .day((double)day)
        .hour((double)hour)
        .minute((double)minute)
        .second((double)second)
        .millis((double)millis);
  }

  protected int timePeriodFieldFlags(List<TimePeriodField> fields) {
    int flags = 0;
    for (TimePeriodField field : fields) {
      flags |= FIELDMAP.get(field);
    }
    return flags;
  }

  private static final Map<TimePeriodField, Integer> FIELDMAP = new EnumMap<>(TimePeriodField.class);

  private static final int FLAG_YEAR = 1;
  private static final int FLAG_MONTH = 2;
  private static final int FLAG_WEEK = 4;
  private static final int FLAG_DAY = 8;
  private static final int FLAG_HOUR = 16;
  private static final int FLAG_MINUTE = 32;
  private static final int FLAG_SECOND = 64;
  private static final int FLAG_MILLIS = 128;

  static {
    FIELDMAP.put(TimePeriodField.YEAR, FLAG_YEAR);
    FIELDMAP.put(TimePeriodField.MONTH, FLAG_MONTH);
    FIELDMAP.put(TimePeriodField.WEEK, FLAG_WEEK);
    FIELDMAP.put(TimePeriodField.DAY, FLAG_DAY);
    FIELDMAP.put(TimePeriodField.HOUR, FLAG_HOUR);
    FIELDMAP.put(TimePeriodField.MINUTE, FLAG_MINUTE);
    FIELDMAP.put(TimePeriodField.SECOND, FLAG_SECOND);
    FIELDMAP.put(TimePeriodField.MILLIS, FLAG_MILLIS);
  }

  /**
   * Compute a new Julian day and milliseconds UTC by updating one or more fields.
   */
  protected Pair<Long, Double> _add(TimePeriod fields) {
    long[] f = this.utcfields();

    long jd;
    double ms;
    long year;
    double yearf;

    long ydays;
    double ydaysf;

    long month;
    double monthf;

    long day;
    double dayf;

    // Capture days and time fields (in milliseconds) for future use.
    // We do this here since we'll be re-initializing the date fields below.
    Pair<Long, Double> daysms = this._addTime(fields);
    double _days = daysms._1;
    double _ms = daysms._2;
    _days += fields.day.or(0.0) + (fields.week.or(0.0) * 7);

    // YEARS

    // Split off the fractional part of the years. Add the integer
    // years to the extended year. Then get the number of days in that
    // year and multiply that by the fractional part.
    // Example: In a Gregorian leap year we'll have 366 days. If the fractional
    // year is 0.25 we'll get 91.5 days.
    Pair<Long, Double> split = splitfrac(fields.year.or(0.0));
    year = split._1;
    yearf = split._2;
    year += f[DateField.EXTENDED_YEAR];

    split = splitfrac(this.daysInYear(year) * yearf);
    ydays = split._1;
    ydaysf = split._2;

    // Add day fractions from year calculation to milliseconds
    ms = ydaysf * CalendarConstants.ONE_DAY_MS;

    // Calculate the julian day for the year, month and day-of-month combination,
    // adding in the days due to fractional year
    jd = this.monthStart(year, f[DateField.MONTH] - 1, false) + f[DateField.DAY_OF_MONTH] + ydays;

    // Initialize fields from the julian day
    f[DateField.JULIAN_DAY] = jd;
    f[DateField.MILLIS_IN_DAY] = 0;
    this.initFields(f);

    year = f[DateField.EXTENDED_YEAR];

    // MONTHS

    // Get integer and fractional months
    split = splitfrac((f[DateField.MONTH] - 1) + fields.month.or(0.0));
    month = split._1;
    monthf = split._2;

    // Add back years by dividing by month count
    int mc = this.monthCount();
    split = splitfrac(month / 12); // ignore fraction here
    long myears = split._1;
    month -= myears * mc;
    year += myears;

    // Take away a year if the month pointer went negative
    if (month < 0) {
      month += mc;
      year--;
    }

    // Compute updated julian day from year and fractional month
    double dim = this.daysInMonth(year, (int)month) * monthf;
    split = splitfrac(_days + dim);
    day = split._1;
    dayf = split._2;
    jd = this.monthStart(year, month, false) + f[DateField.DAY_OF_MONTH];

    // DAY AND TIME FIELDS

    // Adjust julian day by fractional day and time fields
    ms += Math.round(_ms + (dayf * CalendarConstants.ONE_DAY_MS));
    if (ms >= CalendarConstants.ONE_DAY_MS) {
      double d = Math.floor(ms / CalendarConstants.ONE_DAY_MS);
      ms -= d * CalendarConstants.ONE_DAY_MS;
      day += d;
    }
    return Pair.of(jd + day, ms);
  }

  /**
   * Converts all time fields into [days, milliseconds].
   */
  protected Pair<Long, Double> _addTime(TimePeriod fields) {
    double msDay = this.fields[DateField.MILLIS_IN_DAY] - this.timeZoneOffset();
    msDay += (fields.hour.or(0.0) * CalendarConstants.ONE_HOUR_MS) +
        (fields.minute.or(0.0) * CalendarConstants.ONE_MINUTE_MS) +
        (fields.second.or(0.0) * CalendarConstants.ONE_SECOND_MS) +
        fields.millis.or(0.0);
    long days = (long) Math.floor(msDay / CalendarConstants.ONE_DAY_MS);
    double ms = msDay - (days * CalendarConstants.ONE_DAY_MS);
    return Pair.of(days, ms);
  }

  protected Pair<Long, Double> splitfrac(double n) {
    double t = Math.abs(n);
    int sign = n < 0 ? -1 : 1;
    long r = (long)Math.floor(t);
    return Pair.of(sign * r, sign * (t - r));
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

  protected long[] utcfields() {
    long u = this.unixEpoch();
    long[] f = Arrays.copyOf(this.fields, this.fields.length);
    jdFromUnixEpoch(u, f);
    computeBaseFields(f);
    initFields(f);
    return f;
  }

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

    msDay = msDay / 1000;
    f[DateField.SECOND] = msDay % 60;

    msDay = msDay / 60;
    f[DateField.MINUTE] = msDay % 60;

    msDay = msDay / 60;
    f[DateField.HOUR_OF_DAY] = msDay;
    f[DateField.AM_PM] = msDay / 12;
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
