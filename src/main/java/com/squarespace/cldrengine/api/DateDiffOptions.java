package com.squarespace.cldrengine.api;

import java.util.List;

import lombok.EqualsAndHashCode;

/**
 * Options controlling difference / until / since / relativeTime calculations.
 *
 * Mirrors the TS DateDiffOptions interface (systems/calendars/calendar.ts):
 * includeWeeks, fields (list of fields to roll up into) and rollupFractional.
 *
 * Hand-written because the options generator has no list-typed field support.
 */
@EqualsAndHashCode
public class DateDiffOptions {

  public final Option<Boolean> includeWeeks = Option.option();
  public final Option<List<TimePeriodField>> fields = Option.option();
  public final Option<Boolean> rollupFractional = Option.option();

  public DateDiffOptions() {
  }

  public DateDiffOptions(DateDiffOptions arg) {
    this.includeWeeks.set(arg.includeWeeks);
    this.fields.set(arg.fields);
    this.rollupFractional.set(arg.rollupFractional);
  }

  public DateDiffOptions includeWeeks(Boolean arg) {
    this.includeWeeks.set(arg);
    return this;
  }

  public DateDiffOptions includeWeeks(Option<Boolean> arg) {
    this.includeWeeks.set(arg);
    return this;
  }

  public DateDiffOptions fields(List<TimePeriodField> arg) {
    this.fields.set(arg);
    return this;
  }

  public DateDiffOptions fields(Option<List<TimePeriodField>> arg) {
    this.fields.set(arg);
    return this;
  }

  public DateDiffOptions rollupFractional(Boolean arg) {
    this.rollupFractional.set(arg);
    return this;
  }

  public DateDiffOptions rollupFractional(Option<Boolean> arg) {
    this.rollupFractional.set(arg);
    return this;
  }

  public static DateDiffOptions build() {
    return new DateDiffOptions();
  }

  public DateDiffOptions copy() {
    return new DateDiffOptions(this);
  }

  public DateDiffOptions mergeIf(DateDiffOptions ...args) {
    DateDiffOptions o = new DateDiffOptions(this);
    for (DateDiffOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DateDiffOptions o) {
    this.includeWeeks.setIf(o.includeWeeks);
    this.fields.setIf(o.fields);
    this.rollupFractional.setIf(o.rollupFractional);
  }

  public DateDiffOptions merge(DateDiffOptions ...args) {
    DateDiffOptions o = new DateDiffOptions(this);
    for (DateDiffOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DateDiffOptions o) {
    this.includeWeeks.set(o.includeWeeks);
    this.fields.set(o.fields);
    this.rollupFractional.set(o.rollupFractional);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DateDiffOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (includeWeeks.ok()) {
      buf.append("includeWeeks=").append(includeWeeks).append(' ');
    }
    if (fields.ok()) {
      buf.append("fields=").append(fields).append(' ');
    }
    if (rollupFractional.ok()) {
      buf.append("rollupFractional=").append(rollupFractional).append(' ');
    }
  }

}
