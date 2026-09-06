package com.squarespace.cldrengine.internal;

import com.squarespace.cldrengine.api.CalendarConstants;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.CalendarDateFields;
import com.squarespace.cldrengine.api.DateField;
import com.squarespace.cldrengine.api.Pair;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;
import com.squarespace.cldrengine.calendars.DayOfWeek;
import com.squarespace.cldrengine.calendars.TimeZoneData;
import com.squarespace.cldrengine.calendars.ZoneInfo;

/**
 * Port of @phensley/cldr-core systems/calendars/math.ts (internal in TS).
 */
public class CalendarMath {

  /**
   * Year, month and day fields.
   */
  public static class YearMonthDay {
    public final long year;
    public final long month;
    public final long day;

    public YearMonthDay(long year, long month, long day) {
      this.year = year;
      this.month = month;
      this.day = day;
    }
  }

  /**
   * Year and month fields.
   */
  public static class YearMonth {
    public final long year;
    public final long month;

    public YearMonth(long year, long month) {
      this.year = year;
      this.month = month;
    }
  }

  /**
   * Extract year, month, and day fields.
   */
  public static YearMonthDay toYMD(CalendarDate d) {
    return new YearMonthDay(d.extendedYear(), d.month(), d.dayOfMonth());
  }

  /**
   * Given a Julian day and local milliseconds (in UTC), return the Unix
   * epoch milliseconds UTC.
   */
  public static long unixEpochFromJD(long jd, long msDay) {
    long days = jd - CalendarConstants.JD_UNIX_EPOCH;
    // TS: Math.round(msDay) — msDay is already integral in Java so the
    // round is an identity, keep it for parity.
    return days * CalendarConstants.ONE_DAY_MS + msDay;
  }

  /**
   * Ensure n falls between min and max.
   */
  public static long clamp(long n, long min, long max) {
    return Math.min(max, Math.max(min, n));
  }

  /**
   * Ensure that the year, month, and day represent a valid date, clamping the
   * month and day fields to a valid range.
   */
  public static YearMonthDay clampYMD(long year, long monthArg, long d) {
    long month = clamp(monthArg, 1, 12);
    long day = clamp(d, 1, GregorianMath.daysInMonth(year, month - 1));
    return new YearMonthDay(year, month, day);
  }

  /**
   * Adjust the year and month to ensure they represent part of a valid date.
   */
  public static YearMonth adjustYM(long yearp, long monthp) {
    long year = yearp;
    long month = monthp;
    month -= 1;
    year += MathFix.floorDiv(month, 12);
    month %= 12;
    if (month < 0) {
      month += 12;
    }
    month += 1;
    return new YearMonth(year, month);
  }

  /**
   * Add time fields in milliseconds.
   */
  public static Pair<Long, Double> adjustTime(double millis, TimePeriod added) {
    double ms = millis + timeToMillis(added);

    // Extract days from the time fields and add them.
    // TS: (ms / ONE_DAY_MS) | 0 — ToInt32 truncation; (int) cast on the
    // double quotient is identical for the magnitudes involved.
    long days = (int) (ms / CalendarConstants.ONE_DAY_MS);
    ms -= days * CalendarConstants.ONE_DAY_MS;
    return Pair.of(days, ms);
  }

  /**
   * Convert the time fields of a period to milliseconds.
   */
  public static double timeToMillis(TimePeriod period) {
    return (period.hour * CalendarConstants.ONE_HOUR_MS +
        period.minute * CalendarConstants.ONE_MINUTE_MS +
        period.second * CalendarConstants.ONE_SECOND_MS +
        period.millis);
  }

  /**
   * Add years, days, hours, etc, to dates. This function always returns
   * the result in UTC.
   *
   * Compatible with the Temporal API (except for rounding; we only truncate)
   * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Temporal/ZonedDateTime/add
   *
   * Port of @phensley/cldr-core systems/calendars/add.ts:31-101. The
   * {@code monthStart} callback is the calendar-specific protected
   * monthStart(eyear, month, useMonth) hook bound with useMonth=false
   * (TS: {@code internal.monthStart(year, month - 1, false)}).
   */
  public static Pair<Long, Long> add(CalendarDate orig, TimePeriod added, MonthStartFn monthStart) {
    // Check if only date or time fields are present.
    boolean hasTime = added.hour != 0.0 || added.minute != 0.0 || added.second != 0.0 || added.millis != 0.0;
    boolean hasDate = added.year != 0.0 || added.month != 0.0 || added.week != 0.0 || added.day != 0.0;

    // Procedure:
    // 1. Add YEARS and MONTHS to base date.
    // 2. Clamp the original day to ensure the new date is valid, e.g.
    //    "Jan 31, 2024" + 1 month gives "Feb 29, 2024" instead of "Feb 31, 2024"
    //    or "Mar 2, 2024".
    // 3. Add the WEEKS and DAYS to the new date.
    // 4. Combine the new date with the original time-of-day. This finishes adding
    //    all of the date fields.
    // 3. Calculate the UNIX epoch timestamp for the new date.
    // 4. Disambiguate the epoch wall time to select the correct timezone offset.
    // 5. Finally add the time to the new epoch.

    // Get Julian day and milliseconds in UTC
    long jd = orig.modifiedJulianDay();
    long millis = orig.millisecondsInDay();
    long offset = orig.timeZoneOffset();

    // If nothing is being added, return the original date.
    if (!hasDate && !hasTime) {
      return Pair.of(jd, millis - offset);
    }

    // Add integers only
    TimePeriod truncated = truncateFields(added);

    // If only time is being added, skip the year/month logic
    // and add directly to the timestamp.
    if (!hasDate) {
      Pair<Long, Double> r = adjustTime(millis - offset, truncated);
      jd += r._1;
      return Pair.of(jd, r._2.longValue());
    }

    // Work in UTC then translate back later
    CalendarDate date = orig.set(CalendarDateFields.build().zoneId("UTC"));

    // Add years and months directly to the date fields.
    YearMonthDay ymd = toYMD(date);

    // Break milliseconds down into days, hours, minutes, etc.
    TimePeriod time = millisToTimePeriod(millis);

    // Add date fields
    ymd = dateAdd(ymd, truncated);

    // Compute the Julian day of the year, month, and add days.
    jd = monthStart.apply(ymd.year, ymd.month - 1) + ymd.day + (long) time.day;

    // Convert Julian day and milliseconds to Unix epoch to find timezone offset.
    long epoch = unixEpochFromJD(jd, millis);

    // Get the wall clock timezone offset. We use the CalendarDate's timezoneId
    // knowing it will always resolve to a valid timezone record, since constructing
    // an instance must pass through the timezone resolution process.
    Pair<Long, ?> wall = TimeZoneData.fromWall(orig.timeZoneId(), epoch);
    epoch = wall != null ? wall._1.longValue() : epoch;

    // Add the time components.
    epoch += (long) timeToMillis(truncated);

    // Convert the UNIX epoch back to Julian day and milliseconds-in-day.
    return jdFromUnixEpoch(epoch);
  }

  /**
   * Add the date parts of a TimePeriod to a date.
   *
   * Port of add.ts:96-116. The added fields are expected to be integral
   * (callers pass the result of {@link #truncateFields}).
   */
  public static YearMonthDay dateAdd(YearMonthDay date, TimePeriod added) {
    long year = date.year;
    long month = date.month;
    long day = date.day;

    year += (long) added.year;
    month += (long) added.month;

    // Add excess months to years
    YearMonth ym = adjustYM(year, month);
    year = ym.year;
    month = ym.month;

    // Ensure the day falls into a valid range for the new year and month.
    YearMonthDay ymd = clampYMD(year, month, day);
    year = ymd.year;
    month = ymd.month;
    day = ymd.day;

    // Add days from all sources
    day += (long) added.week * 7 + (long) added.day;
    return new YearMonthDay(year, month, day);
  }

  /**
   * Break milliseconds down into hours, minutes, seconds, and a milliseconds
   * remainder.
   *
   * Port of add.ts:118-128. TS: {@code (r.millis / ONE_HOUR_MS) | 0} — ToInt32
   * truncation toward zero; the {@code (int)} cast on the double quotient is
   * identical for the magnitudes involved (the input is always in
   * [0, ONE_DAY_MS)).
   */
  public static TimePeriod millisToTimePeriod(long millis) {
    TimePeriod r = new TimePeriod();

    r.millis = millis;
    r.hour = (int) (r.millis / CalendarConstants.ONE_HOUR_MS);
    r.millis -= r.hour * CalendarConstants.ONE_HOUR_MS;
    r.minute = (int) (r.millis / CalendarConstants.ONE_MINUTE_MS);
    r.millis -= r.minute * CalendarConstants.ONE_MINUTE_MS;
    r.second = (int) (r.millis / CalendarConstants.ONE_SECOND_MS);
    r.millis -= r.second * CalendarConstants.ONE_SECOND_MS;

    return r;
  }

  /**
   * Functional form of the per-calendar monthStart(eyear, month, useMonth)
   * hook (protected on CalendarDate), bound with useMonth=false by callers.
   */
  public interface MonthStartFn {
    long apply(long year, double month);
  }

  /**
   * Keep the integral part of each field and replace unset fields with zero.
   */
  public static TimePeriod truncateFields(TimePeriod fields) {
    TimePeriod result = new TimePeriod();
    for (TimePeriodField key : TimePeriodField.values()) {
      setValue(result, key, value(fields, key));
    }
    return result;
  }

  private static double value(TimePeriod period, TimePeriodField key) {
    switch (key) {
      case YEAR:
        return period.year;
      case MONTH:
        return period.month;
      case WEEK:
        return period.week;
      case DAY:
        return period.day;
      case HOUR:
        return period.hour;
      case MINUTE:
        return period.minute;
      case SECOND:
        return period.second;
      default:
        return period.millis;
    }
  }

  private static void setValue(TimePeriod result, TimePeriodField key, double value) {
    value = trunc(value);
    switch (key) {
      case YEAR:
        result.year(value);
        break;
      case MONTH:
        result.month(value);
        break;
      case WEEK:
        result.week(value);
        break;
      case DAY:
        result.day(value);
        break;
      case HOUR:
        result.hour(value);
        break;
      case MINUTE:
        result.minute(value);
        break;
      case SECOND:
        result.second(value);
        break;
      default:
        result.millis(value);
        break;
    }
  }

  /**
   * TS: Math.trunc — drop the fractional part, keeping the sign.
   */
  private static double trunc(double value) {
    return value < 0 ? Math.ceil(value) : Math.floor(value);
  }

  /**
   * Compute Julian day from timezone-adjusted Unix epoch milliseconds.
   */
  public static void fieldsFromUnixEpoch(long ms, long[] f) {
    Pair<Long, Long> res = jdFromUnixEpoch(ms);
    f[DateField.JULIAN_DAY] = res._1;
    f[DateField.MILLIS_IN_DAY] = res._2;
  }

  /**
   * Compute the Julian day and the milliseconds remainder from a Unix epoch timestamp.
   */
  public static Pair<Long, Long> jdFromUnixEpoch(long ms) {
    long days = MathFix.floorDiv(ms, CalendarConstants.ONE_DAY_MS);
    long jd = days + CalendarConstants.JD_UNIX_EPOCH;
    // TS: floor(ms - days * ONE_DAY_MS) — days is the floor division result so
    // the remainder is already integral and non-negative, floor is an identity.
    long msDay = ms - days * CalendarConstants.ONE_DAY_MS;
    return Pair.of(jd, msDay);
  }

  /**
   * Initialize base calendar fields.
   */
  public static ZoneInfo initBaseFromUnixEpoch(long[] f, long ms, String zoneId) {
    zoneId = TimeZoneData.substituteZoneAlias(zoneId);
    ZoneInfo zoneInfo = TimeZoneData.zoneInfoFromUTC(zoneId, ms);
    fieldsFromUnixEpoch(ms + zoneInfo.offset, f);
    computeBaseFields(f);
    return zoneInfo;
  }

  /**
   * Compute fields common to all calendars. Before calling this, we must
   * have the JULIAN_DAY and MILLIS_IN_DAY fields set. Every calculation
   * is relative to these.
   */
  public static void computeBaseFields(long[] f) {
    long jd = clamp(f[DateField.JULIAN_DAY], CalendarConstants.JD_MIN, CalendarConstants.JD_MAX);
    // checkJDRange(jd);

    long msDay = f[DateField.MILLIS_IN_DAY];
    long ms = msDay + (jd - CalendarConstants.JD_UNIX_EPOCH) * CalendarConstants.ONE_DAY_MS;

    f[DateField.LOCAL_MILLIS] = ms;
    f[DateField.JULIAN_DAY] = jd;
    f[DateField.MILLIS_IN_DAY] = msDay;
    f[DateField.MILLIS] = msDay % 1000;

    // TS: (msDay / 1000) | 0 — msDay is non-negative here so truncation
    // toward zero and integer division are identical.
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

}
