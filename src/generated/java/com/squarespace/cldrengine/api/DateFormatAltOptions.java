package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class DateFormatAltOptions {

  public final Option<EraAltType> era = Option.option();
  public final Option<DayPeriodAltType> dayPeriod = Option.option();

  public DateFormatAltOptions() {
  }

  public DateFormatAltOptions(DateFormatAltOptions arg) {
    this.era.set(arg.era);
    this.dayPeriod.set(arg.dayPeriod);
  }

  public DateFormatAltOptions era(EraAltType arg) {
    this.era.set(arg);
    return this;
  }

  public DateFormatAltOptions era(Option<EraAltType> arg) {
    this.era.set(arg);
    return this;
  }

  public DateFormatAltOptions dayPeriod(DayPeriodAltType arg) {
    this.dayPeriod.set(arg);
    return this;
  }

  public DateFormatAltOptions dayPeriod(Option<DayPeriodAltType> arg) {
    this.dayPeriod.set(arg);
    return this;
  }

  public static DateFormatAltOptions build() {
    return new DateFormatAltOptions();
  }

  public DateFormatAltOptions copy() {
    return new DateFormatAltOptions(this);
  }

  public DateFormatAltOptions mergeIf(DateFormatAltOptions ...args) {
    DateFormatAltOptions o = new DateFormatAltOptions(this);
    for (DateFormatAltOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DateFormatAltOptions o) {
    this.era.setIf(o.era);
    this.dayPeriod.setIf(o.dayPeriod);
  }

  public DateFormatAltOptions merge(DateFormatAltOptions ...args) {
    DateFormatAltOptions o = new DateFormatAltOptions(this);
    for (DateFormatAltOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateFormatAltOptions o) {
    this.era.set(o.era);
    this.dayPeriod.set(o.dayPeriod);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateFormatAltOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (era.ok()) {
      buf.append("era=").append(era).append(' ');
    }
    if (dayPeriod.ok()) {
      buf.append("dayPeriod=").append(dayPeriod).append(' ');
    }
  }

}
