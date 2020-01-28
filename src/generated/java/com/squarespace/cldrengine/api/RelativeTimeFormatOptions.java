package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.CalendarType;
import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.RoundingModeType;

public class RelativeTimeFormatOptions extends RelativeTimeFieldFormatOptions {

  public final Option<CalendarType> calendar = Option.option();
  public final Option<Boolean> dayOfWeek = Option.option();
  public final Option<TimePeriodField> field = Option.option();

  public RelativeTimeFormatOptions() {
  }

  public RelativeTimeFormatOptions(RelativeTimeFormatOptions arg) {
    super(arg);
    this.calendar.set(arg.calendar);
    this.dayOfWeek.set(arg.dayOfWeek);
    this.field.set(arg.field);
  }

  public RelativeTimeFormatOptions calendar(CalendarType arg) {
    this.calendar.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions calendar(Option<CalendarType> arg) {
    this.calendar.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions dayOfWeek(Boolean arg) {
    this.dayOfWeek.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions dayOfWeek(Option<Boolean> arg) {
    this.dayOfWeek.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions field(TimePeriodField arg) {
    this.field.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions field(Option<TimePeriodField> arg) {
    this.field.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions width(DateFieldWidthType arg) {
    this.width.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions width(Option<DateFieldWidthType> arg) {
    this.width.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions numericOnly(Boolean arg) {
    this.numericOnly.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions numericOnly(Option<Boolean> arg) {
    this.numericOnly.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions alwaysNow(Boolean arg) {
    this.alwaysNow.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions alwaysNow(Option<Boolean> arg) {
    this.alwaysNow.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions group(Boolean arg) {
    this.group.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions group(Option<Boolean> arg) {
    this.group.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions minimumIntegerDigits(Option<Integer> arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions maximumFractionDigits(Option<Integer> arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions minimumFractionDigits(Option<Integer> arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions maximumSignificantDigits(Option<Integer> arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public RelativeTimeFormatOptions minimumSignificantDigits(Option<Integer> arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public static RelativeTimeFormatOptions build() {
    return new RelativeTimeFormatOptions();
  }

  public RelativeTimeFormatOptions copy() {
    return new RelativeTimeFormatOptions(this);
  }

  public RelativeTimeFormatOptions mergeIf(RelativeTimeFormatOptions ...args) {
    RelativeTimeFormatOptions o = new RelativeTimeFormatOptions(this);
    for (RelativeTimeFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(RelativeTimeFormatOptions o) {
    super._mergeIf(o);
    this.calendar.setIf(o.calendar);
    this.dayOfWeek.setIf(o.dayOfWeek);
    this.field.setIf(o.field);
  }

  public RelativeTimeFormatOptions merge(RelativeTimeFormatOptions ...args) {
    RelativeTimeFormatOptions o = new RelativeTimeFormatOptions(this);
    for (RelativeTimeFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(RelativeTimeFormatOptions o) {
    super._merge(o);
    this.calendar.set(o.calendar);
    this.dayOfWeek.set(o.dayOfWeek);
    this.field.set(o.field);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("RelativeTimeFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    super._tostring(buf);
    if (calendar.ok()) {
      buf.append("calendar=").append(calendar).append(' ');
    }
    if (dayOfWeek.ok()) {
      buf.append("dayOfWeek=").append(dayOfWeek).append(' ');
    }
    if (field.ok()) {
      buf.append("field=").append(field).append(' ');
    }
  }

}
