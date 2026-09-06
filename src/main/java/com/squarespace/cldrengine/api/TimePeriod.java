package com.squarespace.cldrengine.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A period of time expressed as a set of calendar and clock fields.
 *
 * Mirrors the TS TimePeriod record (systems/calendars/interval.ts) introduced
 * in cldr-engine 1.13.0: all eight numeric fields are plain doubles, always
 * present and zero-initialized (unlike the older Option-wrapped partial
 * fields model).
 */
@EqualsAndHashCode
@ToString
public class TimePeriod {

  public double year = 0;
  public double month = 0;
  public double week = 0;
  public double day = 0;
  public double hour = 0;
  public double minute = 0;
  public double second = 0;
  public double millis = 0;

  public TimePeriod() {
  }

  public TimePeriod(TimePeriod arg) {
    this.year = arg.year;
    this.month = arg.month;
    this.week = arg.week;
    this.day = arg.day;
    this.hour = arg.hour;
    this.minute = arg.minute;
    this.second = arg.second;
    this.millis = arg.millis;
  }

  public static TimePeriod build() {
    return new TimePeriod();
  }

  public TimePeriod year(double arg) {
    this.year = arg;
    return this;
  }

  public TimePeriod month(double arg) {
    this.month = arg;
    return this;
  }

  public TimePeriod week(double arg) {
    this.week = arg;
    return this;
  }

  public TimePeriod day(double arg) {
    this.day = arg;
    return this;
  }

  public TimePeriod hour(double arg) {
    this.hour = arg;
    return this;
  }

  public TimePeriod minute(double arg) {
    this.minute = arg;
    return this;
  }

  public TimePeriod second(double arg) {
    this.second = arg;
    return this;
  }

  public TimePeriod millis(double arg) {
    this.millis = arg;
    return this;
  }

}
