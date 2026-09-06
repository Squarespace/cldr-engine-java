package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;

/**
 * Shared test helpers mirroring the TS test utilities
 * (__tests__/systems/calendars/_utils.ts and _referencedates.ts, tag v1.15.0):
 * the CLDRFormatter date/period string formats and the reference timezones /
 * timestamps used by the calendar date math test files.
 */
class DiffTestUtils {

  // REFERENCE TIMEZONES (_referencedates.ts)
  static final String UTC = "UTC";
  static final String LONDON = "Europe/London";
  static final String LOS_ANGELES = "America/Los_Angeles";
  static final String NEW_YORK = "America/New_York";
  static final String PARIS = "Europe/Paris";
  static final String TOKYO = "Asia/Tokyo";
  static final String VANCOUVER = "America/Vancouver";

  // REFERENCE TIMESTAMPS (_referencedates.ts)
  static final long JAN_01_2000 = 946747800000L;
  static final long MAR_11_2000 = 952761625000L;
  static final long APR_02_2000 = 954658740000L;
  static final long SEP_01_2000 = 967809600000L;
  static final long SEP_15_2000 = 969019200000L;
  static final long JAN_30_2024 = 1706621400000L;
  static final long FEB_28_2024 = 1709127000000L;
  static final long FEB_29_2024 = 1709213400000L;
  static final long MAR_03_2024 = 1709472600000L;
  static final long MAR_10_2024 = 1710053940000L;
  static final long MAR_30_2024 = 1711801800000L;
  static final long MAR_09_2025 = 1741503540000L;
  static final long NOV_02_2025 = 1762063140000L;

  private interface PeriodField {
    double get(TimePeriod p);
  }

  private static final PeriodField[] GETTERS = new PeriodField[] {
    p -> p.year, p -> p.month, p -> p.week, p -> p.day,
    p -> p.hour, p -> p.minute, p -> p.second, p -> p.millis,
  };

  // TIME_PERIOD_FIELD_SUFFIX (_utils.ts)
  private static final String[] SUFFIXES = { "y", "m", "w", "d", "H", "M", "S", "ms" };

  private DiffTestUtils() {
  }

  static GregorianDate gregorian(long epoch, String zone) {
    return GregorianDate.fromUnixEpoch(epoch, zone, 1, 1);
  }

  /**
   * CLDRFormatter.periodString(_utils.ts): only non-zero fields are emitted,
   * decimals rounded to 3 places for display.
   */
  static String periodString(TimePeriod p) {
    StringBuilder r = new StringBuilder();
    for (int i = 0; i < 8; i++) {
      double n = GETTERS[i].get(p);
      if (n != 0.0) {
        if (r.length() > 0) {
          r.append(' ');
        }
        // Round all decimals to 3 places for convenience; (n|0) !== n
        if ((int) n != n) {
          n = Math.round(n * 1000.0) / 1000.0;
        }
        // Match JS String(n): integral magnitudes render without a
        // fractional part ("9" not "9.0").
        r.append(numToString(n));
        r.append(SUFFIXES[i]);
      }
    }
    return r.toString();
  }

  /**
   * JS String(number) for the magnitudes used by these tests.
   */
  private static String numToString(double n) {
    if (n == (long) n) {
      return Long.toString((long) n);
    }
    return Double.toString(n);
  }

  /**
   * CalendarDate.toDateString(c) (calendar.ts:614).
   */
  static String dateString(CalendarDate c) {
    long y = c.extendedYear();
    boolean neg = y < 0;
    return String.format("%s%04d-%02d-%02d", neg ? "-" : "", Math.abs(y), c.month(), c.dayOfMonth());
  }

  /**
   * CalendarDate.toTimeString(c, options) (calendar.ts:625) restricted to the
   * includeZoneOffset / optionalMilliseconds options used by the tests.
   */
  static String timeString(CalendarDate c, boolean includeZoneOffset, boolean optionalMilliseconds) {
    long ms = c.milliseconds();
    // Check if user disabled display of milliseconds when zero
    boolean showMilliseconds = !(optionalMilliseconds && ms == 0);
    StringBuilder s = new StringBuilder(String.format("%02d:%02d:%02d", c.hourOfDay(), c.minute(), c.second()));
    if (showMilliseconds) {
      s.append(String.format(".%03d", ms));
    }
    if (includeZoneOffset) {
      s.append(formatZoneOffset(c));
    }
    return s.toString();
  }

  /**
   * CalendarDate.toDateTimeString(c, options) (calendar.ts:649).
   */
  static String dateTimeString(CalendarDate c, boolean includeZoneOffset, boolean optionalMilliseconds) {
    return dateString(c) + " " + timeString(c, includeZoneOffset, optionalMilliseconds);
  }

  /**
   * formatZoneOffset (calendar.ts:921) via getTZC (internals/calendars/zoneutil.ts).
   */
  static String formatZoneOffset(CalendarDate c) {
    int offset = c.timeZoneOffset();
    boolean negative = offset < 0;
    if (negative) {
      offset *= -1;
    }
    double mins = offset / 60000.0;
    int hours = toInt32(mins / 60);
    int minutes = toInt32(mins % 60);
    boolean zero = hours == 0 && minutes == 0;
    return String.format("%s%02d:%02d", zero ? "+" : (negative ? "-" : "+"), hours, minutes);
  }

  /**
   * TS | 0 (ToInt32) truncation for the sub-minute-offset zone rendering.
   */
  private static int toInt32(double x) {
    return (int) (long) x;
  }

}
