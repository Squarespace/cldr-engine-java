package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class TimeStringOptions {

  public final Option<Boolean> includeZoneId = Option.option();
  public final Option<Boolean> includeZoneOffset = Option.option();
  public final Option<Boolean> optionalMilliseconds = Option.option();

  public TimeStringOptions() {
  }

  public TimeStringOptions(TimeStringOptions arg) {
    this.includeZoneId.set(arg.includeZoneId);
    this.includeZoneOffset.set(arg.includeZoneOffset);
    this.optionalMilliseconds.set(arg.optionalMilliseconds);
  }

  public TimeStringOptions includeZoneId(Boolean arg) {
    this.includeZoneId.set(arg);
    return this;
  }

  public TimeStringOptions includeZoneId(Option<Boolean> arg) {
    this.includeZoneId.set(arg);
    return this;
  }

  public TimeStringOptions includeZoneOffset(Boolean arg) {
    this.includeZoneOffset.set(arg);
    return this;
  }

  public TimeStringOptions includeZoneOffset(Option<Boolean> arg) {
    this.includeZoneOffset.set(arg);
    return this;
  }

  public TimeStringOptions optionalMilliseconds(Boolean arg) {
    this.optionalMilliseconds.set(arg);
    return this;
  }

  public TimeStringOptions optionalMilliseconds(Option<Boolean> arg) {
    this.optionalMilliseconds.set(arg);
    return this;
  }

  public static TimeStringOptions build() {
    return new TimeStringOptions();
  }

  public TimeStringOptions copy() {
    return new TimeStringOptions(this);
  }

  public TimeStringOptions mergeIf(TimeStringOptions ...args) {
    TimeStringOptions o = new TimeStringOptions(this);
    for (TimeStringOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(TimeStringOptions o) {
    this.includeZoneId.setIf(o.includeZoneId);
    this.includeZoneOffset.setIf(o.includeZoneOffset);
    this.optionalMilliseconds.setIf(o.optionalMilliseconds);
  }

  public TimeStringOptions merge(TimeStringOptions ...args) {
    TimeStringOptions o = new TimeStringOptions(this);
    for (TimeStringOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(TimeStringOptions o) {
    this.includeZoneId.set(o.includeZoneId);
    this.includeZoneOffset.set(o.includeZoneOffset);
    this.optionalMilliseconds.set(o.optionalMilliseconds);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("TimeStringOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (includeZoneId.ok()) {
      buf.append("includeZoneId=").append(includeZoneId).append(' ');
    }
    if (includeZoneOffset.ok()) {
      buf.append("includeZoneOffset=").append(includeZoneOffset).append(' ');
    }
    if (optionalMilliseconds.ok()) {
      buf.append("optionalMilliseconds=").append(optionalMilliseconds).append(' ');
    }
  }

}
