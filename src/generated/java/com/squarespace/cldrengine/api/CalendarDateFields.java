package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class CalendarDateFields {

  public final Option<Double> year = Option.option();
  public final Option<Double> month = Option.option();
  public final Option<Double> day = Option.option();
  public final Option<Double> hour = Option.option();
  public final Option<Double> minute = Option.option();
  public final Option<Double> second = Option.option();
  public final Option<Double> millis = Option.option();
  public final Option<String> zoneId = Option.option();

  public CalendarDateFields() {
  }

  public CalendarDateFields(CalendarDateFields arg) {
    this.year.set(arg.year);
    this.month.set(arg.month);
    this.day.set(arg.day);
    this.hour.set(arg.hour);
    this.minute.set(arg.minute);
    this.second.set(arg.second);
    this.millis.set(arg.millis);
    this.zoneId.set(arg.zoneId);
  }

  public CalendarDateFields year(Double arg) {
    this.year.set(arg);
    return this;
  }

  public CalendarDateFields year(Long arg) {
    this.year.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields year(Integer arg) {
    this.year.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields year(Option<Double> arg) {
    this.year.set(arg);
    return this;
  }

  public CalendarDateFields month(Double arg) {
    this.month.set(arg);
    return this;
  }

  public CalendarDateFields month(Long arg) {
    this.month.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields month(Integer arg) {
    this.month.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields month(Option<Double> arg) {
    this.month.set(arg);
    return this;
  }

  public CalendarDateFields day(Double arg) {
    this.day.set(arg);
    return this;
  }

  public CalendarDateFields day(Long arg) {
    this.day.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields day(Integer arg) {
    this.day.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields day(Option<Double> arg) {
    this.day.set(arg);
    return this;
  }

  public CalendarDateFields hour(Double arg) {
    this.hour.set(arg);
    return this;
  }

  public CalendarDateFields hour(Long arg) {
    this.hour.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields hour(Integer arg) {
    this.hour.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields hour(Option<Double> arg) {
    this.hour.set(arg);
    return this;
  }

  public CalendarDateFields minute(Double arg) {
    this.minute.set(arg);
    return this;
  }

  public CalendarDateFields minute(Long arg) {
    this.minute.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields minute(Integer arg) {
    this.minute.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields minute(Option<Double> arg) {
    this.minute.set(arg);
    return this;
  }

  public CalendarDateFields second(Double arg) {
    this.second.set(arg);
    return this;
  }

  public CalendarDateFields second(Long arg) {
    this.second.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields second(Integer arg) {
    this.second.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields second(Option<Double> arg) {
    this.second.set(arg);
    return this;
  }

  public CalendarDateFields millis(Double arg) {
    this.millis.set(arg);
    return this;
  }

  public CalendarDateFields millis(Long arg) {
    this.millis.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields millis(Integer arg) {
    this.millis.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public CalendarDateFields millis(Option<Double> arg) {
    this.millis.set(arg);
    return this;
  }

  public CalendarDateFields zoneId(String arg) {
    this.zoneId.set(arg);
    return this;
  }

  public CalendarDateFields zoneId(Option<String> arg) {
    this.zoneId.set(arg);
    return this;
  }

  public static CalendarDateFields build() {
    return new CalendarDateFields();
  }

  public CalendarDateFields copy() {
    return new CalendarDateFields(this);
  }

  public CalendarDateFields mergeIf(CalendarDateFields ...args) {
    CalendarDateFields o = new CalendarDateFields(this);
    for (CalendarDateFields arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(CalendarDateFields o) {
    this.year.setIf(o.year);
    this.month.setIf(o.month);
    this.day.setIf(o.day);
    this.hour.setIf(o.hour);
    this.minute.setIf(o.minute);
    this.second.setIf(o.second);
    this.millis.setIf(o.millis);
    this.zoneId.setIf(o.zoneId);
  }

  public CalendarDateFields merge(CalendarDateFields ...args) {
    CalendarDateFields o = new CalendarDateFields(this);
    for (CalendarDateFields arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(CalendarDateFields o) {
    this.year.set(o.year);
    this.month.set(o.month);
    this.day.set(o.day);
    this.hour.set(o.hour);
    this.minute.set(o.minute);
    this.second.set(o.second);
    this.millis.set(o.millis);
    this.zoneId.set(o.zoneId);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("CalendarDateFields( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (year.ok()) {
      buf.append("year=").append(year).append(' ');
    }
    if (month.ok()) {
      buf.append("month=").append(month).append(' ');
    }
    if (day.ok()) {
      buf.append("day=").append(day).append(' ');
    }
    if (hour.ok()) {
      buf.append("hour=").append(hour).append(' ');
    }
    if (minute.ok()) {
      buf.append("minute=").append(minute).append(' ');
    }
    if (second.ok()) {
      buf.append("second=").append(second).append(' ');
    }
    if (millis.ok()) {
      buf.append("millis=").append(millis).append(' ');
    }
    if (zoneId.ok()) {
      buf.append("zoneId=").append(zoneId).append(' ');
    }
  }

}
