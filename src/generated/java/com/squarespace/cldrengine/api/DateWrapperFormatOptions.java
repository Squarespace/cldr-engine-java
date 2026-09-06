package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class DateWrapperFormatOptions {

  public final Option<CalendarType> calendar = Option.option();
  public final Option<FormatWidthType> width = Option.option();
  public final Option<Boolean> atTime = Option.option();

  public DateWrapperFormatOptions() {
  }

  public DateWrapperFormatOptions(DateWrapperFormatOptions arg) {
    this.calendar.set(arg.calendar);
    this.width.set(arg.width);
    this.atTime.set(arg.atTime);
  }

  public DateWrapperFormatOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateWrapperFormatOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateWrapperFormatOptions width(FormatWidthType arg) {
    this.width.set(arg);
    return this;
  }

  public DateWrapperFormatOptions width(Option<FormatWidthType> arg) {
    this.width.set(arg);
    return this;
  }

  public DateWrapperFormatOptions atTime(Boolean arg) {
    this.atTime.set(arg);
    return this;
  }

  public DateWrapperFormatOptions atTime(Option<Boolean> arg) {
    this.atTime.set(arg);
    return this;
  }

  public static DateWrapperFormatOptions build() {
    return new DateWrapperFormatOptions();
  }

  public DateWrapperFormatOptions copy() {
    return new DateWrapperFormatOptions(this);
  }

  public DateWrapperFormatOptions mergeIf(DateWrapperFormatOptions ...args) {
    DateWrapperFormatOptions o = new DateWrapperFormatOptions(this);
    for (DateWrapperFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DateWrapperFormatOptions o) {
    this.calendar.setIf(o.calendar);
    this.width.setIf(o.width);
    this.atTime.setIf(o.atTime);
  }

  public DateWrapperFormatOptions merge(DateWrapperFormatOptions ...args) {
    DateWrapperFormatOptions o = new DateWrapperFormatOptions(this);
    for (DateWrapperFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateWrapperFormatOptions o) {
    this.calendar.set(o.calendar);
    this.width.set(o.width);
    this.atTime.set(o.atTime);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateWrapperFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (calendar.ok()) {
      buf.append("calendar=").append(calendar).append(' ');
    }
    if (width.ok()) {
      buf.append("width=").append(width).append(' ');
    }
    if (atTime.ok()) {
      buf.append("atTime=").append(atTime).append(' ');
    }
  }

}
