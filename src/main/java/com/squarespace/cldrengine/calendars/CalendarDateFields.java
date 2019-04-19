package com.squarespace.cldrengine.calendars;

public class CalendarDateFields {

  public double year;
  public double month;
  public double week;
  public double day;
  public double hour;
  public double minute;
  public double second;
  public double millis;
  public String zoneId;

  public CalendarDateFields year(double y) {
    this.year = y;
    return this;
  }

  public CalendarDateFields year(int y) {
    return year((double)y);
  }

  public CalendarDateFields month(double m) {
    this.month = m;
    return this;
  }

  public CalendarDateFields month(int m) {
    return month((double)m);
  }

  public CalendarDateFields week(double w) {
    this.week = w;
    return this;
  }

  public CalendarDateFields week(int w) {
    return week((double)w);
  }

  public CalendarDateFields day(double d) {
    this.day = d;
    return this;
  }

  public CalendarDateFields day(int d) {
    return day((double)d);
  }

  public CalendarDateFields hour(double h) {
    this.hour = h;
    return this;
  }

  public CalendarDateFields hour(int h) {
    return hour((double)h);
  }

  public CalendarDateFields minute(double m) {
    this.minute = m;
    return this;
  }

  public CalendarDateFields minute(int m) {
    return minute((double)m);
  }

  public CalendarDateFields second(double s) {
    this.second = s;
    return this;
  }

  public CalendarDateFields second(int s) {
    return second((double)s);
  }

  public CalendarDateFields millis(double m) {
    this.millis = m;
    return this;
  }

  public CalendarDateFields millis(int s) {
    return millis((double)s);
  }

  public CalendarDateFields zoneId(String id) {
    this.zoneId = id;
    return this;
  }
}
