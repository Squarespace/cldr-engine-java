package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class DateRawFormatOptions {

  public final Option<String> pattern = Option.option();
  public final Option<CalendarType> calendar = Option.option();
  public final Option<String> numberSystem = Option.option();
  public final Option<ContextType> context = Option.option();

  public DateRawFormatOptions() {
  }

  public DateRawFormatOptions(DateRawFormatOptions arg) {
    this.pattern.set(arg.pattern);
    this.calendar.set(arg.calendar);
    this.numberSystem.set(arg.numberSystem);
    this.context.set(arg.context);
  }

  public DateRawFormatOptions pattern(String arg) {
    this.pattern.set(arg);
    return this;
  }

  public DateRawFormatOptions pattern(Option<String> arg) {
    this.pattern.set(arg);
    return this;
  }

  public DateRawFormatOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateRawFormatOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateRawFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public DateRawFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public DateRawFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public DateRawFormatOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public static DateRawFormatOptions build() {
    return new DateRawFormatOptions();
  }

  public DateRawFormatOptions copy() {
    return new DateRawFormatOptions(this);
  }

  public DateRawFormatOptions mergeIf(DateRawFormatOptions ...args) {
    DateRawFormatOptions o = new DateRawFormatOptions(this);
    for (DateRawFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DateRawFormatOptions o) {
    this.pattern.setIf(o.pattern);
    this.calendar.setIf(o.calendar);
    this.numberSystem.setIf(o.numberSystem);
    this.context.setIf(o.context);
  }

  public DateRawFormatOptions merge(DateRawFormatOptions ...args) {
    DateRawFormatOptions o = new DateRawFormatOptions(this);
    for (DateRawFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateRawFormatOptions o) {
    this.pattern.set(o.pattern);
    this.calendar.set(o.calendar);
    this.numberSystem.set(o.numberSystem);
    this.context.set(o.context);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateRawFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (pattern.ok()) {
      buf.append("pattern=").append(pattern).append(' ');
    }
    if (calendar.ok()) {
      buf.append("calendar=").append(calendar).append(' ');
    }
    if (numberSystem.ok()) {
      buf.append("numberSystem=").append(numberSystem).append(' ');
    }
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
  }

}
