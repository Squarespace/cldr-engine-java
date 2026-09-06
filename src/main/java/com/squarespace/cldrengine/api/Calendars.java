package com.squarespace.cldrengine.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
   * Construct a new date in the Buddhist calendar with the given fields.
   */
  BuddhistDate newBuddhistDate(CalendarDateFields fields);

  /**
   * Construct a new date in the Gregorian calendar with the given fields.
   */
  GregorianDate newGregorianDate(CalendarDateFields fields);

  /**
   * Construct a new date in the ISO-8601 calendar with the given fields.
   */
  ISO8601Date newISO8601Date(CalendarDateFields fields);

  /**
   * Construct a new date in the Japanese calendar with the given fields.
   */
  JapaneseDate newJapaneseDate(CalendarDateFields fields);

  /**
   * Construct a new date in the Persian calendar with the given fields.
   */
  PersianDate newPersianDate(CalendarDateFields fields);

  /**
   * Alias for {@code nowGregorian(zoneId)}.
   */
  GregorianDate now(String zoneId);

  /**
   * Construct a new date in the Buddhist calendar representing the current date and time.
   */
  BuddhistDate nowBuddhist(String zoneId);

  /**
   * Construct a new date in the Gregorian calendar representing the current date and time.
   */
  GregorianDate nowGregorian(String zoneId);

  /**
   * Construct a new date in the ISO-8601 calendar representing the current date and time.
   */
  ISO8601Date nowISO8601(String zoneId);

  /**
   * Construct a new date in the Japanese calendar representing the current date and time.
   */
  JapaneseDate nowJapanese(String zoneId);

  /**
   * Construct a new date in the Persian calendar representing the current date and time.
   */
  PersianDate nowPersian(String zoneId);

  /**
   * Formats a date-time value to string using a raw date-time pattern.
   *
   * Warning: You should not use this for general formatting.
   */
  String formatDateRaw(CalendarDate date, DateRawFormatOptions options);

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

  /**
   * Return the first day of the week. 1 denotes Sunday, 7 denotes Saturday.
   */
  int firstDayOfWeek();

  /**
   * Return the minimum number of days required in the first week of the year.
   */
  int minDaysInFirstWeek();

  /**
   * Return a localized display name for the given date field type, e.g. "day", "month".
   */
  String dateField(DateFieldType type, DateFieldFormatOptions options);

  /**
   * Return the localized day periods for the calendar.
   *
   * The TypeScript API returns an `any` value with a nested object structure.
   * In Java this is mapped to a nested {@code Map<String, Object>} whose leaf
   * values are always {@code String}s. Intermediate map keys correspond to
   * the JSON object keys in TypeScript: the day-period key (e.g. "am",
   * "noon") maps to a sub-map keyed by alternate (“none”, “casing”).
   */
  Map<String, Object> dayPeriods(CalendarFieldsOptions options);

  /**
   * Return the localized era names for the calendar.
   *
   * The TypeScript API returns an `any` value with a nested object structure.
   * In Java this is mapped to a nested {@code Map<String, Object>} whose leaf
   * values are always {@code String}s. The era key (e.g. "0", "1") maps to a
   * sub-map keyed by alternate (“none”, “sensitive”).
   */
  Map<String, Object> eras(EraFieldsOptions options);

  /**
   * Return the localized month names for the calendar.
   *
   * The TypeScript API returns an `any` value with a nested object structure.
   * In Java this is mapped to a nested {@code Map<String, Object>} whose leaf
   * values are always {@code String}s; here the map is flat, keyed by month
   * number ("1".."12").
   */
  Map<String, Object> months(CalendarFieldsOptions options);

  /**
   * Return the localized quarter names for the calendar.
   *
   * The TypeScript API returns an `any` value with a nested object structure.
   * In Java this is mapped to a nested {@code Map<String, Object>} whose leaf
   * values are always {@code String}s; here the map is flat, keyed by quarter
   * number ("1".."4").
   */
  Map<String, Object> quarters(CalendarFieldsOptions options);

  /**
   * Return the localized weekday names for the calendar.
   *
   * The TypeScript API returns an `any` value with a nested object structure.
   * In Java this is mapped to a nested {@code Map<String, Object>} whose leaf
   * values are always {@code String}s; here the map is flat, keyed by weekday
   * number ("1"=Sunday .. "7"=Saturday).
   */
  Map<String, Object> weekdays(CalendarFieldsOptions options);

  // formatDateRaw

  // formatDateRawToParts

  /**
   * Return information about the locale's preferred and allowed time cycles.
   */
  TimeData timeData();

  /**
   * Return an array containing the official TZDB timezone identifiers.
   */
  List<String> timeZoneIds();

  /**
   * Resolve a timezone id / alias to the official TZDB identifier.
   */
  String resolveTimeZoneId(String zoneId);

  /**
   * Return additional information for a timezone id, including the
   * localized exemplar city.
   */
  TimeZoneInfo timeZoneInfo(String zoneId);

  // timePeriodToQuantity

}
