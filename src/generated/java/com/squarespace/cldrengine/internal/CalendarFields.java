package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.DayPeriodAltType;

public class CalendarFields {

  public final Vector2Arrow<String, String> weekdays;
  public final Vector2Arrow<String, String> months;
  public final Vector2Arrow<String, String> quarters;
  public final Vector3Arrow<String, String, DayPeriodAltType> dayPeriods;

  public CalendarFields(
      Vector2Arrow<String, String> weekdays,
      Vector2Arrow<String, String> months,
      Vector2Arrow<String, String> quarters,
      Vector3Arrow<String, String, DayPeriodAltType> dayPeriods) {
    this.weekdays = weekdays;
    this.months = months;
    this.quarters = quarters;
    this.dayPeriods = dayPeriods;
  }

}
