package com.squarespace.cldrengine.api;

import java.util.List;

public interface Calendars {

  /**
   * Convert a date time into a date in the Buddhist calendar, with locale-specific
   * week data.
   */
  BuddhistDate toBuddhistDate(CalendarDate date);

  /**
   * Convert a date time into a date in the Gregorian calendar, with locale-specific
   * week data.
   */
  GregorianDate toGregorianDate(CalendarDate date);

  /**
   * Convert a date time into a date in the ISO-8601 calendar, with ISO week data.
   */
  ISO8601Date toISO8601Date(CalendarDate date);

  /**
   * Convert a date time into a date in the Japanese calendar, with locale-specific
   * week data.
   */
  JapaneseDate toJapaneseDate(CalendarDate date);

  /**
   * Convert a date time into a date in the Persian calendar, with locale-specific
   * week data.
   */
  PersianDate toPersianDate(CalendarDate date);

  /**
   * Formats a date-time value to string.
   */
  String formatDate(CalendarDate date, DateFormatOptions options);

  /**
   * Formats a date-time value to an array of parts.
   */
  List<Part> formatDateToParts(CalendarDate date, DateFormatOptions options);

  /**
   * Formats a date interval with a start and end.
   */
  String formatDateInterval(CalendarDate start, CalendarDate end, DateIntervalFormatOptions options);

  // formatDateIntevalToParts

  // formatRelativeTimeField

  // formatRelativeTime

  // formatDateRaw

  // formatDateRawToParts

  /**
   * Return an array containing the official TZDB timezone identifiers.
   */
  List<String> timeZoneIds();

  /**
   * Resolve a timezone id / alias to the official TZDB identifier.
   */
  String resolveTimeZoneId(String zoneId);

  // timeZoneInfo

  // timePeriodToQuantity

}
