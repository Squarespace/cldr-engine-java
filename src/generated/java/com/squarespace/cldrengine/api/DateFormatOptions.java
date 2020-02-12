package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.ContextType;
import lombok.Generated;

@Generated
public class DateFormatOptions {

  public final Option<ContextType> context = Option.option();
  public final Option<FormatWidthType> datetime = Option.option();
  public final Option<FormatWidthType> date = Option.option();
  public final Option<FormatWidthType> time = Option.option();
  public final Option<FormatWidthType> wrap = Option.option();
  public final Option<String> skeleton = Option.option();
  public final Option<CalendarType> calendar = Option.option();
  public final Option<String> numberSystem = Option.option();

  public DateFormatOptions() {
  }

  public DateFormatOptions(DateFormatOptions arg) {
    this.context.set(arg.context);
    this.datetime.set(arg.datetime);
    this.date.set(arg.date);
    this.time.set(arg.time);
    this.wrap.set(arg.wrap);
    this.skeleton.set(arg.skeleton);
    this.calendar.set(arg.calendar);
    this.numberSystem.set(arg.numberSystem);
  }

  public DateFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public DateFormatOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public DateFormatOptions datetime(FormatWidthType arg) {
    this.datetime.set(arg);
    return this;
  }

  public DateFormatOptions datetime(Option<FormatWidthType> arg) {
    this.datetime.set(arg);
    return this;
  }

  public DateFormatOptions date(FormatWidthType arg) {
    this.date.set(arg);
    return this;
  }

  public DateFormatOptions date(Option<FormatWidthType> arg) {
    this.date.set(arg);
    return this;
  }

  public DateFormatOptions time(FormatWidthType arg) {
    this.time.set(arg);
    return this;
  }

  public DateFormatOptions time(Option<FormatWidthType> arg) {
    this.time.set(arg);
    return this;
  }

  public DateFormatOptions wrap(FormatWidthType arg) {
    this.wrap.set(arg);
    return this;
  }

  public DateFormatOptions wrap(Option<FormatWidthType> arg) {
    this.wrap.set(arg);
    return this;
  }

  public DateFormatOptions skeleton(String arg) {
    this.skeleton.set(arg);
    return this;
  }

  public DateFormatOptions skeleton(Option<String> arg) {
    this.skeleton.set(arg);
    return this;
  }

  public DateFormatOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateFormatOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public DateFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public DateFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public static DateFormatOptions build() {
    return new DateFormatOptions();
  }

  public DateFormatOptions copy() {
    return new DateFormatOptions(this);
  }

  public DateFormatOptions mergeIf(DateFormatOptions ...args) {
    DateFormatOptions o = new DateFormatOptions(this);
    for (DateFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DateFormatOptions o) {
    this.context.setIf(o.context);
    this.datetime.setIf(o.datetime);
    this.date.setIf(o.date);
    this.time.setIf(o.time);
    this.wrap.setIf(o.wrap);
    this.skeleton.setIf(o.skeleton);
    this.calendar.setIf(o.calendar);
    this.numberSystem.setIf(o.numberSystem);
  }

  public DateFormatOptions merge(DateFormatOptions ...args) {
    DateFormatOptions o = new DateFormatOptions(this);
    for (DateFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateFormatOptions o) {
    this.context.set(o.context);
    this.datetime.set(o.datetime);
    this.date.set(o.date);
    this.time.set(o.time);
    this.wrap.set(o.wrap);
    this.skeleton.set(o.skeleton);
    this.calendar.set(o.calendar);
    this.numberSystem.set(o.numberSystem);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
    if (datetime.ok()) {
      buf.append("datetime=").append(datetime).append(' ');
    }
    if (date.ok()) {
      buf.append("date=").append(date).append(' ');
    }
    if (time.ok()) {
      buf.append("time=").append(time).append(' ');
    }
    if (wrap.ok()) {
      buf.append("wrap=").append(wrap).append(' ');
    }
    if (skeleton.ok()) {
      buf.append("skeleton=").append(skeleton).append(' ');
    }
    if (calendar.ok()) {
      buf.append("calendar=").append(calendar).append(' ');
    }
    if (numberSystem.ok()) {
      buf.append("numberSystem=").append(numberSystem).append(' ');
    }
  }

}
