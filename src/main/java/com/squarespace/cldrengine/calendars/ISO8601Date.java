package com.squarespace.cldrengine.calendars;

public class ISO8601Date extends GregorianDate {

  protected ISO8601Date() {
    super(CalendarType.ISO8601, DayOfWeek.MONDAY, CalendarConstants.ISO8601_MIN_DAYS);
  }

}
