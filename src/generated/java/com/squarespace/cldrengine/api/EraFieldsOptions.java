package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.CalendarType;

public class EraFieldsOptions {

  public final Option<CalendarType> calendar = Option.option();
  public final Option<EraWidthType> width = Option.option();
  public final Option<ContextType> context = Option.option();

  public EraFieldsOptions() {
  }

  public EraFieldsOptions(EraFieldsOptions arg) {
    this.calendar.set(arg.calendar);
    this.width.set(arg.width);
    this.context.set(arg.context);
  }

  public EraFieldsOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public EraFieldsOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public EraFieldsOptions width(EraWidthType arg) {
    this.width.set(arg);
    return this;
  }

  public EraFieldsOptions width(Option<EraWidthType> arg) {
    this.width.set(arg);
    return this;
  }

  public EraFieldsOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public EraFieldsOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public static EraFieldsOptions build() {
    return new EraFieldsOptions();
  }

  public EraFieldsOptions copy() {
    return new EraFieldsOptions(this);
  }

  public EraFieldsOptions mergeIf(EraFieldsOptions ...args) {
    EraFieldsOptions o = new EraFieldsOptions(this);
    for (EraFieldsOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(EraFieldsOptions o) {
    this.calendar.setIf(o.calendar);
    this.width.setIf(o.width);
    this.context.setIf(o.context);
  }

  public EraFieldsOptions merge(EraFieldsOptions ...args) {
    EraFieldsOptions o = new EraFieldsOptions(this);
    for (EraFieldsOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(EraFieldsOptions o) {
    this.calendar.set(o.calendar);
    this.width.set(o.width);
    this.context.set(o.context);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("EraFieldsOptions( ");
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
