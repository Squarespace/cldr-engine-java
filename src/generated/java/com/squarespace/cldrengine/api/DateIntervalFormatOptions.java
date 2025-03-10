package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class DateIntervalFormatOptions {

  public final Option<Boolean> strict = Option.option();
  public final Option<String> skeleton = Option.option();
  public final Option<String> date = Option.option();
  public final Option<String> time = Option.option();
  public final Option<ContextType> context = Option.option();
  public final Option<CalendarType> calendar = Option.option();
  public final Option<String> numberSystem = Option.option();
  public final Option<DateFormatAltOptions> alt = Option.option();
  public final Option<Boolean> atTime = Option.option();
  public final Option<FormatWidthType> wrap = Option.option();

  public DateIntervalFormatOptions() {
  }

  public DateIntervalFormatOptions(DateIntervalFormatOptions arg) {
    this.strict.set(arg.strict);
    this.skeleton.set(arg.skeleton);
    this.date.set(arg.date);
    this.time.set(arg.time);
    this.context.set(arg.context);
    this.calendar.set(arg.calendar);
    this.numberSystem.set(arg.numberSystem);
    this.alt.set(arg.alt);
    this.atTime.set(arg.atTime);
    this.wrap.set(arg.wrap);
  }

  public DateIntervalFormatOptions strict(Boolean arg) {
    this.strict.set(arg);
    return this;
  }

  public DateIntervalFormatOptions strict(Option<Boolean> arg) {
    this.strict.set(arg);
    return this;
  }

  public DateIntervalFormatOptions skeleton(String arg) {
    this.skeleton.set(arg);
    return this;
  }

  public DateIntervalFormatOptions skeleton(Option<String> arg) {
    this.skeleton.set(arg);
    return this;
  }

  public DateIntervalFormatOptions date(String arg) {
    this.date.set(arg);
    return this;
  }

  public DateIntervalFormatOptions date(Option<String> arg) {
    this.date.set(arg);
    return this;
  }

  public DateIntervalFormatOptions time(String arg) {
    this.time.set(arg);
    return this;
  }

  public DateIntervalFormatOptions time(Option<String> arg) {
    this.time.set(arg);
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

  public DateIntervalFormatOptions alt(DateFormatAltOptions arg) {
    this.alt.set(arg);
    return this;
  }

  public DateIntervalFormatOptions alt(Option<DateFormatAltOptions> arg) {
    this.alt.set(arg);
    return this;
  }

  public DateIntervalFormatOptions atTime(Boolean arg) {
    this.atTime.set(arg);
    return this;
  }

  public DateIntervalFormatOptions atTime(Option<Boolean> arg) {
    this.atTime.set(arg);
    return this;
  }

  public DateIntervalFormatOptions wrap(FormatWidthType arg) {
    this.wrap.set(arg);
    return this;
  }

  public DateIntervalFormatOptions wrap(Option<FormatWidthType> arg) {
    this.wrap.set(arg);
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
    this.strict.setIf(o.strict);
    this.skeleton.setIf(o.skeleton);
    this.date.setIf(o.date);
    this.time.setIf(o.time);
    this.context.setIf(o.context);
    this.calendar.setIf(o.calendar);
    this.numberSystem.setIf(o.numberSystem);
    this.alt.setIf(o.alt);
    this.atTime.setIf(o.atTime);
    this.wrap.setIf(o.wrap);
  }

  public DateIntervalFormatOptions merge(DateIntervalFormatOptions ...args) {
    DateIntervalFormatOptions o = new DateIntervalFormatOptions(this);
    for (DateIntervalFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateIntervalFormatOptions o) {
    this.strict.set(o.strict);
    this.skeleton.set(o.skeleton);
    this.date.set(o.date);
    this.time.set(o.time);
    this.context.set(o.context);
    this.calendar.set(o.calendar);
    this.numberSystem.set(o.numberSystem);
    this.alt.set(o.alt);
    this.atTime.set(o.atTime);
    this.wrap.set(o.wrap);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateIntervalFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (strict.ok()) {
      buf.append("strict=").append(strict).append(' ');
    }
    if (skeleton.ok()) {
      buf.append("skeleton=").append(skeleton).append(' ');
    }
    if (date.ok()) {
      buf.append("date=").append(date).append(' ');
    }
    if (time.ok()) {
      buf.append("time=").append(time).append(' ');
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
    if (alt.ok()) {
      buf.append("alt=").append(alt).append(' ');
    }
    if (atTime.ok()) {
      buf.append("atTime=").append(atTime).append(' ');
    }
    if (wrap.ok()) {
      buf.append("wrap=").append(wrap).append(' ');
    }
  }

}
