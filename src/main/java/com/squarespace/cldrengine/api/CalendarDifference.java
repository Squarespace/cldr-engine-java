package com.squarespace.cldrengine.api;

import java.util.List;

import com.squarespace.cldrengine.calendars.TimeZoneData;
import com.squarespace.cldrengine.internal.CalendarMath;
import com.squarespace.cldrengine.internal.CalendarMath.YearMonth;
import com.squarespace.cldrengine.internal.CalendarMath.YearMonthDay;
import com.squarespace.cldrengine.internal.GregorianMath;

/**
 * Internal; port of @phensley/cldr-core systems/calendars/difference.ts
 * (tag v1.15.0).
 *
 * This class implements Temporal-compatible date difference, underpinning
 * the CalendarDate until/since methods.
 *
 * I've filled a bug against the specification and polyfill library that
 * affects dates spanning the Fall DST boundary, where clocks are set
 * back causing some wall clock times to occur twice, e.g.
 *  2025-11-02 01:01:00-07:00 followed by 01:01:00-08:00 1 hour later.
 *
 * Both the polyfill and Firefox throw a RangeError which seems unexpected
 * as these are otherwise normal zoned timestamps:
 *  https://github.com/js-temporal/temporal-polyfill/issues/347
 *  https://github.com/tc39/proposal-temporal/issues/3141
 *
 * A related issue involves off-by-1-hour results for times surrounding
 * the same boundary.
 *
 * I've solved both of the above issues in this library by detecting whether
 * two CalendarDate occur on the same calendar day, and thus skipping the
 * date-differencing logic entirely opting for a pure time-of-day difference.
 */
class CalendarDifference {

  // TS interval.ts TimePeriodFieldFlag enum values.
  static final int FLAG_YEAR = 1;
  static final int FLAG_MONTH = 2;
  static final int FLAG_WEEK = 4;
  static final int FLAG_DAY = 8;
  static final int FLAG_HOUR = 16;
  static final int FLAG_MINUTE = 32;
  static final int FLAG_SECOND = 64;
  static final int FLAG_MILLIS = 128;

  private static final int DEFAULT_FLAGS =
      FLAG_YEAR |
      FLAG_MONTH |
      // No WEEK by default
      FLAG_DAY |
      FLAG_HOUR |
      FLAG_MINUTE |
      FLAG_SECOND |
      FLAG_MILLIS;

  /**
   * Port of interval.ts:79-91 timePeriodFieldFlags. Returns
   * [flags, smallest] where smallest is the last field matched, or
   * MILLIS when no fields are given.
   */
  static long[] timePeriodFieldFlags(List<TimePeriodField> fields) {
    int flags = 0;
    int smallest = FLAG_MILLIS;
    if (fields != null) {
      for (TimePeriodField field : fields) {
        int flag = fieldFlag(field);
        if (flag != 0) {
          flags |= flag;
          smallest = flag;
        }
      }
    }
    return new long[] { flags, smallest };
  }

  /**
   * FIELDMAP parity (interval.ts:66-73).
   */
  private static int fieldFlag(TimePeriodField field) {
    switch (field) {
      case YEAR:
        return FLAG_YEAR;
      case MONTH:
        return FLAG_MONTH;
      case WEEK:
        return FLAG_WEEK;
      case DAY:
        return FLAG_DAY;
      case HOUR:
        return FLAG_HOUR;
      case MINUTE:
        return FLAG_MINUTE;
      case SECOND:
        return FLAG_SECOND;
      default:
        return FLAG_MILLIS;
    }
  }

  /**
   * State maintained for the difference calculation.
   *
   * Public for testing.
   */
  public static class DifferenceState {
    // Date as YMD
    YearMonthDay one;
    YearMonthDay two;
    final long epoch2;

    // Time of day in milliseconds
    long ms1;
    long ms2;

    // Timezone identifier
    String zoneId;

    // sign equals:
    //  -1 if date1 < date2
    //   1 if date1 > date2
    //   0 if date1 = date2
    final int sign;

    // Increment is the negation of the sign. This is the
    // number of years/months we add to date1 to guess at date2.
    //
    // incr equals:
    //   1 if date1 < date2
    //  -1 if date1 > date2
    final int incr;

    // Flag indicating whether fractional fields are allowed. If the caller
    // requests that the result be expressed in ['year', 'month'] and
    // has enabled fractional fields, any additional days will be added to
    // the months.
    final boolean fractions;

    // Flags determining which fields in the result should have their values set.
    int flags;

    // Smallest flag requested.
    final int smallestFlag;

    // Number of months in a year
    final int monthCount;

    final CalendarDate date1;
    final CalendarDate date2;
    @SuppressWarnings("unused")
    final DateDiffOptions options;

    DifferenceState(CalendarDate date1, CalendarDate date2, DateDiffOptions options) {
      this.date1 = date1;
      this.date2 = date2;
      this.options = options == null ? DateDiffOptions.build() : options;
      this.one = CalendarMath.toYMD(date1);
      this.two = CalendarMath.toYMD(date2);
      this.epoch2 = date2.unixEpoch();
      this.ms1 = date1.millisecondsInDay();
      this.ms2 = date2.millisecondsInDay();
      this.zoneId = date1.timeZoneId();
      this.sign = date1.compare(date2);
      this.incr = -this.sign;
      this.fractions = this.options.rollupFractional.or(false);
      long[] f = timePeriodFieldFlags(this.options.fields.or(null));
      this.flags = (int) f[0];
      this.smallestFlag = (int) f[1];
      if (this.flags == 0) {
        this.flags = DEFAULT_FLAGS;
      }
      // TS: (date1 as any as CalendarDateInternals).monthCount() — protected
      // access is legal from within the api package.
      this.monthCount = date1.monthCount();
    }

    /**
     * Return true if the flag is set.
     */
    boolean has(int flag) {
      return (flag & this.flags) != 0;
    }

    /**
     * Return true if a flag is the smallest field in the set.
     */
    boolean smallest(int flag) {
      return flag == this.smallestFlag;
    }
  }

  /**
   * Calculate the difference between two dates in years, months, days, etc.
   *
   * Specification of algorithm:
   * https://tc39.es/proposal-temporal/#sec-temporal-differencezoneddatetime
   *
   * Compatible with the Temporal API until and since methods:
   * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Temporal/ZonedDateTime/since
   * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Temporal/ZonedDateTime/until
   *
   * Port of difference.ts:135-208, preserved structurally (including the
   * isSameDay shortcut and the 23-25h roundResult hack, which ARE the
   * released 1.15.0 behavior).
   */
  public static TimePeriod difference(CalendarDate date1, CalendarDate date2, DateDiffOptions options) {
    TimePeriod result = TimePeriod.build();

    // Track state across date and time sections
    DifferenceState state = new DifferenceState(date1, date2, options);

    // If equal, return zero.
    if (state.sign == 0) {
      return result;
    }

    boolean sameDay = isSameDay(state.one, state.two);

    // Compute the time difference and adjust the end date while accounting for
    // shifts due to Daylight Savings Time. We do this first since the time difference
    // can shift the days in th date difference.
    long millis;
    if (sameDay) {
      // Special case: found a bug in Temporal that caused it to fail with a
      // "mixed-sign values not allowed as duration fields" error on the following two
      // dates:
      //    zone = "America/Vancouver"
      //   date1 = epoch 1762074000000 "2025-11-02 01:00:00-08:00"
      //   date2 = epoch 1762070460000 "2025-11-02 01:01:00-07:00"
      //   date1.until(date2)
      //
      millis = state.epoch2 - date1.unixEpoch();
    } else {
      // Scan for time difference, adjust end date.
      Pair<YearMonthDay, Long> res = timeDifference(state);
      state.two = res._1;
      millis = res._2;

      // Compute the difference between the date fields and store in the result.
      dateDifference(state, result);
    }

    // ==> At this point we have computed the YEAR, MONTH, WEEK, and DAY fields in the result.

    // Initialize the milliseconds delta
    result.millis = millis;

    // At least one time field was requested.
    if (!state.has(FLAG_DAY)) {
      // If DAY field is not requested, roll days into time
      // TODO: sign
      result.millis += result.day * CalendarConstants.ONE_DAY_MS;
      result.day = 0;
    }

    if (state.fractions) {
      if (state.smallestFlag <= FLAG_DAY) {
        rollDateFractions(state, result);
      } else {
        rollTimeFractions(state, result);
      }
      return result;
    }

    // Hack for rounding edge case
    if (
        state.has(FLAG_DAY) &&
        Math.abs(result.millis) >= 23 * CalendarConstants.ONE_HOUR_MS &&
        Math.abs(result.millis) <= 25 * CalendarConstants.ONE_HOUR_MS
    ) {
      roundResult(state, result);
    }

    // Set all time fields from the milliseconds field.
    rollTime(state, result);

    return result;
  }

  /**
   * Compute date difference.
   *
   * Port of difference.ts:210-289.
   */
  private static void dateDifference(DifferenceState state, TimePeriod result) {
    int flags = state.flags;
    YearMonthDay one = state.one;
    YearMonthDay two = state.two;
    int incr = state.incr;
    DateDiffOptions options = state.options;

    // Seek towards the end date without exceeding it by guessing the year and month.
    YearMonth seek;

    if (state.has(FLAG_YEAR) || state.has(FLAG_MONTH)) {
      // In this block we use date 1 as our base and add or subtract years and months as we
      // approach date 2 without exceeding it.

      // Initial guess for the years difference.
      long guessYears = two.year - one.year;
      if (guessYears != 0) {
        guessYears -= incr;
      }

      // Adjust the year to approach the end date without exceeding it.
      while (!greaterThanYMD(incr, one.year + guessYears, one.month, one.day, two)) {
        result.year = guessYears;
        guessYears += incr;
      }

      // Initial guess for the months difference.
      long guessMonths = incr;
      seek = CalendarMath.adjustYM(one.year + (long) result.year, one.month + guessMonths);

      // Adjust the month to approach the end date without exceeding it.
      while (!greaterThanYMD(incr, seek.year, seek.month, one.day, two)) {
        result.month = guessMonths;
        guessMonths += incr;
        seek = CalendarMath.adjustYM(seek.year, seek.month + incr);
      }

      if (!state.has(FLAG_YEAR)) {
        // Result does not include YEAR, so it must include MONTH due to the condition
        // on the outer block.
        // Result includes MONTH but not YEAR, translate years to months.
        result.month += result.year * 12;
        result.year = 0;
      } else {
        // Result includes YEAR.
        if (!state.has(FLAG_MONTH)) {
          // Result includes YEAR but not MONTH.
          if (
              (!state.fractions && state.smallest(FLAG_YEAR)) ||
              !state.smallest(FLAG_YEAR)
          ) {
            // If YEAR is the smallest field and we're not rolling up fractions, or
            // YEAR is not the smallest field, zero the months. The months will be
            // rolled into the days field later.
            result.month = 0;
          }
        }
      }
    }

    // Finalize values for years and months by clamping them to a valid range.
    seek = CalendarMath.adjustYM(one.year + (long) result.year, one.month + (long) result.month);
    YearMonthDay clamped = CalendarMath.clampYMD(seek.year, seek.month, one.day);

    // Compute final number of days. If the years and months were't set in the
    // result above, the days will span the whole range.
    long day =
        GregorianMath.ymdToJD(two.year, two.month, two.day, state.monthCount) -
            GregorianMath.ymdToJD(clamped.year, clamped.month, clamped.day, state.monthCount);

    // Optionally translate weeks to days. Note: TS has a dead `!flags` term here
    // (flags is always non-zero after the constructor substitutes DEFAULT_FLAGS
    // for 0); dropped.
    long week = 0;
    boolean includeWeeks = options.includeWeeks.or(false);
    if ((includeWeeks && flags == DEFAULT_FLAGS) || state.has(FLAG_WEEK)) {
      // TS: Math.trunc(day / 7) then day %= 7 (truncated division, sign of
      // dividend); Java long division of the double quotient truncates the
      // same way and the % operator shares the JS semantics for negatives.
      week = (long) (day / 7);
      day %= 7;
    }

    // Set the WEEK and DAY fields in the result.
    result.day = day;
    result.week = week;
  }

  /**
   * Compute time difference and adjust date while accounting for DST shifts.
   *
   * Port of difference.ts:291-327.
   */
  private static Pair<YearMonthDay, Long> timeDifference(DifferenceState state) {
    YearMonthDay two = state.two;
    int incr = state.incr;
    long ms2 = state.ms2;
    long ms1 = state.ms1;
    String zoneId = state.zoneId;

    // Intermediate YMD we increment to approach our end date without exeeding it.
    YearMonthDay intermediate = null;

    // Amount to shift the day
    int dayshift = 0;

    // Maximum day shift
    int maxshift = incr == 1 ? 2 : 1;

    // Check if the end time is lower than the start time.
    long delta = ms2 - ms1;
    if (Long.compare(delta, 0) == -incr) {
      dayshift++;
    }

    // Loop, incrementing the end date and calc a difference between start and end times.
    // Guaranteed to loop at least once.
    for (; dayshift <= maxshift; dayshift++) {
      intermediate = adjustDays(two.year, two.month, two.day - dayshift * incr);

      long jd = GregorianMath.ymdToJD(intermediate.year, intermediate.month, intermediate.day, state.monthCount);
      long epoch = CalendarMath.unixEpochFromJD(jd, ms1);
      Pair<Long, ?> wall = TimeZoneData.fromWall(zoneId, epoch);
      if (wall != null) {
        epoch = wall._1.longValue();
      }
      delta = state.epoch2 - epoch;
      if (Long.compare(delta, 0) != -state.incr) {
        break;
      }
    }

    return Pair.of(intermediate, delta);
  }

  /**
   * Return true if dates are on same day.
   */
  private static boolean isSameDay(YearMonthDay one, YearMonthDay two) {
    return one.year == two.year && one.month == two.month && one.day == two.day;
  }

  /**
   * Perform rounding of the result. The goal here is to check if the
   * time component of the result in milliseconds is greater than the
   * length of the last full day period, and increment by 1 day.
   *
   * The length of a day can vary from 23-25 hours depending
   * on shifts due to Daylight Savings Time and the standard offset.
   *
   * Port of difference.ts:340-398.
   */
  private static void roundResult(DifferenceState state, TimePeriod result) {
    // Move to the start of the day.
    //
    // Example "2025-03-09 01:59:00" becomes "2025-03-09 00:00:00"
    YearMonthDay date = CalendarMath.toYMD(state.date1.startOf(CalendarDateModFields.DAY));

    // Add the date components.
    //
    // Example "2025-03-09 00:00:00" + "1 year, 1 month, 22 days"
    // becomes "2025-10-31 00:00:00"
    TimePeriod added = TimePeriod.build()
        .year(result.year)
        .month(result.month)
        .week(result.week)
        .day(result.day);
    YearMonthDay start = CalendarMath.dateAdd(date, added);

    // Compute the start Julian day, then add the local time-of-day milliseconds
    // and get the UNIX epoch timestamp.
    //
    // Example "2025-10-31 00:00:00" becomes "2025-10-31 01:59:00"
    long jd = GregorianMath.ymdToJD(start.year, start.month, start.day, state.monthCount);
    long startEpoch = CalendarMath.unixEpochFromJD(jd, state.ms1);

    // Get wall clock UNIX epoch timestamp.
    Pair<Long, ?> wall = TimeZoneData.fromWall(state.date1.timeZoneId(), startEpoch);
    if (wall != null) {
      startEpoch = wall._1.longValue();
    }

    // Repeat the above to get the end date that is +/- 1 day after the start date.
    // The +1 or -1 increment depends on the direction of the comparison.
    //
    // Example "2025-10-31 00:00:00" with +1 day becomes "2025-11-01 00:00:00"
    YearMonthDay end = adjustDays(start.year, start.month, start.day + state.incr);
    long endJd = GregorianMath.ymdToJD(end.year, end.month, end.day, state.monthCount);
    long endEpoch = CalendarMath.unixEpochFromJD(endJd, state.ms1);
    wall = TimeZoneData.fromWall(state.date1.timeZoneId(), endEpoch);
    if (wall != null) {
      endEpoch = wall._1.longValue();
    }

    // Get the difference between start and end.
    long delta = endEpoch - startEpoch;

    // If the milliseconds of our time period exceeds the length of the last full day,
    // roll milliseconds into 1 day.
    if (state.has(FLAG_DAY) && Math.abs(result.millis) >= Math.abs(delta)) {
      int days = toInt32(result.millis / (state.incr * delta));
      double remainder = result.millis - delta;
      result.day += days;
      result.millis = remainder;
    }
  }

  /**
   * WARNING: This is only designed to be used for small day increments, mainly
   * to support 1- or 2-day adjustments. We use Julian day in other calculations
   * which lets us directly add or subtract any number of days.
   *
   * Adjust the day parameter to correct for underflow and overflow of the
   * given year and month.
   *
   * For example: adding 3 days to "2024-01-30" returns "2024-02-02" and
   * subtracting 3 days from "2024-02-02" returns "2024-01-30".
   *
   * Port of difference.ts:400-414.
   */
  private static YearMonthDay adjustDays(long year, long month, long day) {
    YearMonth ym = CalendarMath.adjustYM(year, month);
    year = ym.year;
    month = ym.month;
    if (day < 1) {
      ym = CalendarMath.adjustYM(year, month - 1);
      year = ym.year;
      month = ym.month;
      day += GregorianMath.daysInMonth(year, month - 1);
    }
    if (day > GregorianMath.daysInMonth(year, month - 1)) {
      day -= GregorianMath.daysInMonth(year, month - 1);
      ym = CalendarMath.adjustYM(year, month + 1);
      year = ym.year;
      month = ym.month;
    }
    return new YearMonthDay(year, month, day);
  }

  /**
   * Return true if (y1, m1, d1) is greater than date2.
   *
   * Port of difference.ts:416-433.
   */
  private static boolean greaterThanYMD(int sign, long y1, long m1, long d1, YearMonthDay date2) {
    if (y1 != date2.year) {
      if (sign * (y1 - date2.year) > 0) {
        return true;
      }
    } else if (m1 != date2.month) {
      if (sign * (m1 - date2.month) > 0) {
        return true;
      }
    } else if (d1 != date2.day) {
      if (sign * (d1 - date2.day) > 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * TS `x | 0` — ECMA-262 ToInt32: truncate toward zero, wrap modulo 2^32. A
   * direct Java `(int)` cast *saturates* at Integer.MAX_VALUE/MIN_VALUE for
   * out-of-range magnitudes, so we truncate via long first (in range for every
   * value this algorithm produces) then narrow mod 2^32.
   */
  private static int toInt32(double x) {
    return (int) (long) x;
  }

  /**
   * Roll milliseconds into all time fields.
   *
   * Port of difference.ts:436-452.
   */
  private static void rollTime(DifferenceState state, TimePeriod result) {
    if (state.has(FLAG_HOUR)) {
      result.hour = toInt32(result.millis / CalendarConstants.ONE_HOUR_MS);
      result.millis -= result.hour * CalendarConstants.ONE_HOUR_MS;
    }
    if (state.has(FLAG_MINUTE)) {
      result.minute = toInt32(result.millis / CalendarConstants.ONE_MINUTE_MS);
      result.millis -= result.minute * CalendarConstants.ONE_MINUTE_MS;
    }
    if (state.has(FLAG_SECOND)) {
      result.second = toInt32(result.millis / CalendarConstants.ONE_SECOND_MS);
      result.millis -= result.second * CalendarConstants.ONE_SECOND_MS;
    }
    if (!state.has(FLAG_MILLIS)) {
      result.millis = 0;
    }
  }

  /**
   * One of the date fields is the smallest field requested, so
   * roll up smaller fields into fractions of a larger field.
   *
   * Port of difference.ts:458-479.
   */
  private static void rollDateFractions(DifferenceState state, TimePeriod result) {
    // Roll milliseconds into day.
    double days = result.day + result.millis / CalendarConstants.ONE_DAY_MS;
    result.millis = 0;

    if (state.smallest(FLAG_YEAR)) {
      result.year += result.month / state.monthCount + (result.week * 7 + days) / 365.25;
      result.month = result.week = result.day = 0;
    } else if (state.smallest(FLAG_MONTH)) {
      result.month += (result.week * 7 + days) / 30.44;
      result.week = result.day = 0;
    } else if (state.smallest(FLAG_WEEK)) {
      result.week += days / 7;
      result.day = 0;
    } else if (state.smallest(FLAG_DAY)) {
      result.day = days;
    }
  }

  /**
   * One of the time fields is the smallest field requested, so
   * roll up smaller fields into fractions of a larger field.
   *
   * Port of difference.ts:481-502.
   */
  private static void rollTimeFractions(DifferenceState state, TimePeriod result) {
    if (state.smallest(FLAG_HOUR)) {
      result.hour +=
          (result.minute * CalendarConstants.ONE_MINUTE_MS +
              result.second * CalendarConstants.ONE_SECOND_MS +
              result.millis) /
              CalendarConstants.ONE_HOUR_MS;
      result.minute = result.second = 0;
    }
    if (state.smallest(FLAG_MINUTE)) {
      result.minute = (result.second * CalendarConstants.ONE_MINUTE_MS + result.millis) / CalendarConstants.ONE_MINUTE_MS;
      result.second = 0;
    }
    if (state.smallest(FLAG_SECOND)) {
      result.second = result.millis / CalendarConstants.ONE_SECOND_MS;
    }

    // Finally if MILLIS not requested, zero it.
    if (!state.smallest(FLAG_MILLIS)) {
      result.millis = 0;
    }
  }

}
