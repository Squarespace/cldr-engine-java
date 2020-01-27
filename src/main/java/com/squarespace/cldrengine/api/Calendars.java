package com.squarespace.cldrengine.api;

import java.util.Date;
import java.util.List;

public interface Calendars {

  /**
   * Convert a date time into a date in the Buddhist calendar, with locale-specific
   * week data.
   */
  BuddhistDate toBuddhistDate(long epoch, String zoneId);

  /**
   * Convert a date time into a date in the Buddhist calendar, with locale-specific
   * week data.
   */
  BuddhistDate toBuddhistDate(Date date, String zoneId);

  /**
   * Convert a date time into a date in the Buddhist calendar, with locale-specific
   * week data.
   */
  BuddhistDate toBuddhistDate(CalendarDate date);

  /**
   * Convert a date time into a date in the Gregorian calendar, with locale-specific
   * week data.
   */
  GregorianDate toGregorianDate(long epoch, String zoneId);

  /**
   * Convert a date time into a date in the Gregorian calendar, with locale-specific
   * week data.
   */
  GregorianDate toGregorianDate(Date date, String zoneId);

  /**
   * Convert a date time into a date in the Gregorian calendar, with locale-specific
   * week data.
   */
  GregorianDate toGregorianDate(CalendarDate date);

  /**
   * Convert a date time into a date in the ISO-8601 calendar, with ISO week data.
   */
  ISO8601Date toISO8601Date(long epoch, String zoneId);

  /**
   * Convert a date time into a date in the ISO-8601 calendar, with ISO week data.
   */
  ISO8601Date toISO8601Date(Date date, String zoneId);

  /**
   * Convert a date time into a date in the ISO-8601 calendar, with ISO week data.
   */
  ISO8601Date toISO8601Date(CalendarDate date);

  /**
   * Convert a date time into a date in the Japanese calendar, with locale-specific
   * week data.
   */
  JapaneseDate toJapaneseDate(long epoch, String zoneId);

  /**
   * Convert a date time into a date in the Japanese calendar, with locale-specific
   * week data.
   */
  JapaneseDate toJapaneseDate(Date date, String zoneId);

  /**
   * Convert a date time into a date in the Japanese calendar, with locale-specific
   * week data.
   */
  JapaneseDate toJapaneseDate(CalendarDate date);

  /**
   * Convert a date time into a date in the Persian calendar, with locale-specific
   * week data.
   */
  PersianDate toPersianDate(long epoch, String zoneId);

  /**
   * Convert a date time into a date in the Persian calendar, with locale-specific
   * week data.
   */
  PersianDate toPersianDate(Date date, String zoneId);

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

  /**
   * Formats a date-time interval for the given skeleton to an array of parts.
   */
  List<Part> formatDateIntervalToParts(CalendarDate start, CalendarDate end, DateIntervalFormatOptions options);

  /**
   * Formats a relative time field to string.
   */
  String formatRelativeTimeField(Decimal value, RelativeTimeFieldType field, RelativeTimeFieldFormatOptions options);

  /**
   * Formats the relative time from a start to end date.
   */
  String formatRelativeTime(CalendarDate start, CalendarDate end, RelativeTimeFormatOptions options);

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
