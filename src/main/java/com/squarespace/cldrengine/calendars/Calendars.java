package com.squarespace.cldrengine.calendars;

public interface Calendars {

  /**
   * Formats a date-time value to string.
   */
  String formatDate(CalendarDate date);

  String formatDate(long epochUTC, String zoneId);

}
