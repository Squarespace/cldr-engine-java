package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class DecimalAdjustOptions {

  public final Option<RoundingModeType> round = Option.option();
  public final Option<Integer> minimumIntegerDigits = Option.option();
  public final Option<Integer> maximumFractionDigits = Option.option();
  public final Option<Integer> minimumFractionDigits = Option.option();
  public final Option<Integer> maximumSignificantDigits = Option.option();
  public final Option<Integer> minimumSignificantDigits = Option.option();

  public DecimalAdjustOptions() {
  }

  public DecimalAdjustOptions(DecimalAdjustOptions arg) {
    this.round.set(arg.round);
    this.minimumIntegerDigits.set(arg.minimumIntegerDigits);
    this.maximumFractionDigits.set(arg.maximumFractionDigits);
    this.minimumFractionDigits.set(arg.minimumFractionDigits);
    this.maximumSignificantDigits.set(arg.maximumSignificantDigits);
    this.minimumSignificantDigits.set(arg.minimumSignificantDigits);
  }

  public DecimalAdjustOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public DecimalAdjustOptions round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public DecimalAdjustOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions minimumIntegerDigits(Option<Integer> arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions maximumFractionDigits(Option<Integer> arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions minimumFractionDigits(Option<Integer> arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions maximumSignificantDigits(Option<Integer> arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public DecimalAdjustOptions minimumSignificantDigits(Option<Integer> arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public static DecimalAdjustOptions build() {
    return new DecimalAdjustOptions();
  }

  public DecimalAdjustOptions copy() {
    return new DecimalAdjustOptions(this);
  }

  public DecimalAdjustOptions mergeIf(DecimalAdjustOptions ...args) {
    DecimalAdjustOptions o = new DecimalAdjustOptions(this);
    for (DecimalAdjustOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DecimalAdjustOptions o) {
    this.round.setIf(o.round);
    this.minimumIntegerDigits.setIf(o.minimumIntegerDigits);
    this.maximumFractionDigits.setIf(o.maximumFractionDigits);
    this.minimumFractionDigits.setIf(o.minimumFractionDigits);
    this.maximumSignificantDigits.setIf(o.maximumSignificantDigits);
    this.minimumSignificantDigits.setIf(o.minimumSignificantDigits);
  }

  public DecimalAdjustOptions merge(DecimalAdjustOptions ...args) {
    DecimalAdjustOptions o = new DecimalAdjustOptions(this);
    for (DecimalAdjustOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DecimalAdjustOptions o) {
    this.round.set(o.round);
    this.minimumIntegerDigits.set(o.minimumIntegerDigits);
    this.maximumFractionDigits.set(o.maximumFractionDigits);
    this.minimumFractionDigits.set(o.minimumFractionDigits);
    this.maximumSignificantDigits.set(o.maximumSignificantDigits);
    this.minimumSignificantDigits.set(o.minimumSignificantDigits);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DecimalAdjustOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (round.ok()) {
      buf.append("round=").append(round).append(' ');
    }
    if (minimumIntegerDigits.ok()) {
      buf.append("minimumIntegerDigits=").append(minimumIntegerDigits).append(' ');
    }
    if (maximumFractionDigits.ok()) {
      buf.append("maximumFractionDigits=").append(maximumFractionDigits).append(' ');
    }
    if (minimumFractionDigits.ok()) {
      buf.append("minimumFractionDigits=").append(minimumFractionDigits).append(' ');
    }
    if (maximumSignificantDigits.ok()) {
      buf.append("maximumSignificantDigits=").append(maximumSignificantDigits).append(' ');
    }
    if (minimumSignificantDigits.ok()) {
      buf.append("minimumSignificantDigits=").append(minimumSignificantDigits).append(' ');
    }
  }

}
