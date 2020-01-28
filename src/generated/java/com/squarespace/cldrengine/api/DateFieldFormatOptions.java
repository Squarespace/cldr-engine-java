package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.CalendarType;

public class DateFieldFormatOptions {

  public final Option<CalendarType> calendar = Option.option();
  public final Option<DateFieldWidthType> width = Option.option();
  public final Option<ContextType> context = Option.option();

  public DateFieldFormatOptions() {
  }

  public DateFieldFormatOptions(DateFieldFormatOptions arg) {
    this.calendar.set(arg.calendar);
    this.width.set(arg.width);
    this.context.set(arg.context);
  }

  public DateFieldFormatOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateFieldFormatOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateFieldFormatOptions width(DateFieldWidthType arg) {
    this.width.set(arg);
    return this;
  }

  public DateFieldFormatOptions width(Option<DateFieldWidthType> arg) {
    this.width.set(arg);
    return this;
  }

  public DateFieldFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public DateFieldFormatOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public static DateFieldFormatOptions build() {
    return new DateFieldFormatOptions();
  }

  public DateFieldFormatOptions copy() {
    return new DateFieldFormatOptions(this);
  }

  public DateFieldFormatOptions mergeIf(DateFieldFormatOptions ...args) {
    DateFieldFormatOptions o = new DateFieldFormatOptions(this);
    for (DateFieldFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DateFieldFormatOptions o) {
    this.calendar.setIf(o.calendar);
    this.width.setIf(o.width);
    this.context.setIf(o.context);
  }

  public DateFieldFormatOptions merge(DateFieldFormatOptions ...args) {
    DateFieldFormatOptions o = new DateFieldFormatOptions(this);
    for (DateFieldFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateFieldFormatOptions o) {
    this.calendar.set(o.calendar);
    this.width.set(o.width);
    this.context.set(o.context);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateFieldFormatOptions( ");
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
