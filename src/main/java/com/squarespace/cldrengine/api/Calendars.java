package com.squarespace.cldrengine.api;

public interface Calendars {

  /**
   * Formats a date-time value to string.
   */
  String formatDate(CalendarDate date, DateFormatOptions options);

}
