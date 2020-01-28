package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.ContextType;

public class DateIntervalFormatOptions {

  public final Option<String> skeleton = Option.option();
  public final Option<ContextType> context = Option.option();
  public final Option<CalendarType> calendar = Option.option();
  public final Option<String> numberSystem = Option.option();

  public DateIntervalFormatOptions() {
  }

  public DateIntervalFormatOptions(DateIntervalFormatOptions arg) {
    this.skeleton.set(arg.skeleton);
    this.context.set(arg.context);
    this.calendar.set(arg.calendar);
    this.numberSystem.set(arg.numberSystem);
  }

  public DateIntervalFormatOptions skeleton(String arg) {
    this.skeleton.set(arg);
    return this;
  }

  public DateIntervalFormatOptions skeleton(Option<String> arg) {
    this.skeleton.set(arg);
    return this;
  }

  public DateIntervalFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public DateIntervalFormatOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public DateIntervalFormatOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateIntervalFormatOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateIntervalFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public DateIntervalFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public static DateIntervalFormatOptions build() {
    return new DateIntervalFormatOptions();
  }

  public DateIntervalFormatOptions copy() {
    return new DateIntervalFormatOptions(this);
  }

  public DateIntervalFormatOptions mergeIf(DateIntervalFormatOptions ...args) {
    DateIntervalFormatOptions o = new DateIntervalFormatOptions(this);
    for (DateIntervalFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DateIntervalFormatOptions o) {
    this.skeleton.setIf(o.skeleton);
    this.context.setIf(o.context);
    this.calendar.setIf(o.calendar);
    this.numberSystem.setIf(o.numberSystem);
  }

  public DateIntervalFormatOptions merge(DateIntervalFormatOptions ...args) {
    DateIntervalFormatOptions o = new DateIntervalFormatOptions(this);
    for (DateIntervalFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateIntervalFormatOptions o) {
    this.skeleton.set(o.skeleton);
    this.context.set(o.context);
    this.calendar.set(o.calendar);
    this.numberSystem.set(o.numberSystem);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateIntervalFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (skeleton.ok()) {
      buf.append("skeleton=").append(skeleton).append(' ');
    }
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
    if (calendar.ok()) {
      buf.append("calendar=").append(calendar).append(' ');
    }
    if (numberSystem.ok()) {
      buf.append("numberSystem=").append(numberSystem).append(' ');
    }
  }

}
