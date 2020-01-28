package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.UnitFormatStyleType;
import com.squarespace.cldrengine.api.RoundingModeType;

public class UnitFormatOptions extends NumberFormatOptions {

  public final Option<Integer> divisor = Option.option();
  public final Option<UnitFormatStyleType> style = Option.option();
  public final Option<UnitLength> length = Option.option();

  public UnitFormatOptions() {
  }

  public UnitFormatOptions(UnitFormatOptions arg) {
    super(arg);
    this.divisor.set(arg.divisor);
    this.style.set(arg.style);
    this.length.set(arg.length);
  }

  public UnitFormatOptions divisor(Integer arg) {
    this.divisor.set(arg);
    return this;
  }

  public UnitFormatOptions divisor(Option<Integer> arg) {
    this.divisor.set(arg);
    return this;
  }

  public UnitFormatOptions style(UnitFormatStyleType arg) {
    this.style.set(arg);
    return this;
  }

  public UnitFormatOptions style(Option<UnitFormatStyleType> arg) {
    this.style.set(arg);
    return this;
  }

  public UnitFormatOptions length(UnitLength arg) {
    this.length.set(arg);
    return this;
  }

  public UnitFormatOptions length(Option<UnitLength> arg) {
    this.length.set(arg);
    return this;
  }

  public UnitFormatOptions group(Boolean arg) {
    this.group.set(arg);
    return this;
  }

  public UnitFormatOptions group(Option<Boolean> arg) {
    this.group.set(arg);
    return this;
  }

  public UnitFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public UnitFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public UnitFormatOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public UnitFormatOptions round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public UnitFormatOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public UnitFormatOptions minimumIntegerDigits(Option<Integer> arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public UnitFormatOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public UnitFormatOptions maximumFractionDigits(Option<Integer> arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public UnitFormatOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public UnitFormatOptions minimumFractionDigits(Option<Integer> arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public UnitFormatOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public UnitFormatOptions maximumSignificantDigits(Option<Integer> arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public UnitFormatOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public UnitFormatOptions minimumSignificantDigits(Option<Integer> arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public static UnitFormatOptions build() {
    return new UnitFormatOptions();
  }

  public UnitFormatOptions copy() {
    return new UnitFormatOptions(this);
  }

  public UnitFormatOptions mergeIf(UnitFormatOptions ...args) {
    UnitFormatOptions o = new UnitFormatOptions(this);
    for (UnitFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(UnitFormatOptions o) {
    super._mergeIf(o);
    this.divisor.setIf(o.divisor);
    this.style.setIf(o.style);
    this.length.setIf(o.length);
  }

  public UnitFormatOptions merge(UnitFormatOptions ...args) {
    UnitFormatOptions o = new UnitFormatOptions(this);
    for (UnitFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(UnitFormatOptions o) {
    super._merge(o);
    this.divisor.set(o.divisor);
    this.style.set(o.style);
    this.length.set(o.length);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("UnitFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    super._tostring(buf);
    if (divisor.ok()) {
      buf.append("divisor=").append(divisor).append(' ');
    }
    if (style.ok()) {
      buf.append("style=").append(style).append(' ');
    }
    if (length.ok()) {
      buf.append("length=").append(length).append(' ');
    }
  }

}
