package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode(callSuper = true)
public class NumberFormatOptions extends DecimalAdjustOptions {

  public final Option<Boolean> group = Option.option();
  public final Option<String> numberSystem = Option.option();

  public NumberFormatOptions() {
  }

  public NumberFormatOptions(NumberFormatOptions arg) {
    super(arg);
    this.group.set(arg.group);
    this.numberSystem.set(arg.numberSystem);
  }

  public NumberFormatOptions group(Boolean arg) {
    this.group.set(arg);
    return this;
  }

  public NumberFormatOptions group(Option<Boolean> arg) {
    this.group.set(arg);
    return this;
  }

  public NumberFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public NumberFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public NumberFormatOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public NumberFormatOptions round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public NumberFormatOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public NumberFormatOptions minimumIntegerDigits(Option<Integer> arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public NumberFormatOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public NumberFormatOptions maximumFractionDigits(Option<Integer> arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public NumberFormatOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public NumberFormatOptions minimumFractionDigits(Option<Integer> arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public NumberFormatOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public NumberFormatOptions maximumSignificantDigits(Option<Integer> arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public NumberFormatOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public NumberFormatOptions minimumSignificantDigits(Option<Integer> arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public static NumberFormatOptions build() {
    return new NumberFormatOptions();
  }

  public NumberFormatOptions copy() {
    return new NumberFormatOptions(this);
  }

  public NumberFormatOptions mergeIf(NumberFormatOptions ...args) {
    NumberFormatOptions o = new NumberFormatOptions(this);
    for (NumberFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(NumberFormatOptions o) {
    super._mergeIf(o);
    this.group.setIf(o.group);
    this.numberSystem.setIf(o.numberSystem);
  }

  public NumberFormatOptions merge(NumberFormatOptions ...args) {
    NumberFormatOptions o = new NumberFormatOptions(this);
    for (NumberFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(NumberFormatOptions o) {
    super._merge(o);
    this.group.set(o.group);
    this.numberSystem.set(o.numberSystem);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("NumberFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    super._tostring(buf);
    if (group.ok()) {
      buf.append("group=").append(group).append(' ');
    }
    if (numberSystem.ok()) {
      buf.append("numberSystem=").append(numberSystem).append(' ');
    }
  }

}
