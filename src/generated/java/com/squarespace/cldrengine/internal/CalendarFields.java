package com.squarespace.cldrengine.internal;


public class CalendarFields {

  public final Vector2Arrow<String, String> weekdays;
  public final Vector2Arrow<String, String> months;
  public final Vector2Arrow<String, String> quarters;
  public final Vector2Arrow<String, String> dayPeriods;

  public CalendarFields(
      Vector2Arrow<String, String> weekdays,
      Vector2Arrow<String, String> months,
      Vector2Arrow<String, String> quarters,
      Vector2Arrow<String, String> dayPeriods) {
    this.weekdays = weekdays;
    this.months = months;
    this.quarters = quarters;
    this.dayPeriods = dayPeriods;
  }

}
