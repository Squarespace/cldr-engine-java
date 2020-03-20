package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class CalendarFieldsOptions {

  public final Option<CalendarType> calendar = Option.option();
  public final Option<FieldWidthType> width = Option.option();
  public final Option<ContextType> context = Option.option();

  public CalendarFieldsOptions() {
  }

  public CalendarFieldsOptions(CalendarFieldsOptions arg) {
    this.calendar.set(arg.calendar);
    this.width.set(arg.width);
    this.context.set(arg.context);
  }

  public CalendarFieldsOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public CalendarFieldsOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public CalendarFieldsOptions width(FieldWidthType arg) {
    this.width.set(arg);
    return this;
  }

  public CalendarFieldsOptions width(Option<FieldWidthType> arg) {
    this.width.set(arg);
    return this;
  }

  public CalendarFieldsOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public CalendarFieldsOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public static CalendarFieldsOptions build() {
    return new CalendarFieldsOptions();
  }

  public CalendarFieldsOptions copy() {
    return new CalendarFieldsOptions(this);
  }

  public CalendarFieldsOptions mergeIf(CalendarFieldsOptions ...args) {
    CalendarFieldsOptions o = new CalendarFieldsOptions(this);
    for (CalendarFieldsOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(CalendarFieldsOptions o) {
    this.calendar.setIf(o.calendar);
    this.width.setIf(o.width);
    this.context.setIf(o.context);
  }

  public CalendarFieldsOptions merge(CalendarFieldsOptions ...args) {
    CalendarFieldsOptions o = new CalendarFieldsOptions(this);
    for (CalendarFieldsOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(CalendarFieldsOptions o) {
    this.calendar.set(o.calendar);
    this.width.set(o.width);
    this.context.set(o.context);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("CalendarFieldsOptions( ");
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
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
  }

}
