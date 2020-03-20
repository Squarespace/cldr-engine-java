package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode(callSuper = true)
public class CurrencyFormatOptions extends NumberFormatOptions {

  public final Option<Integer> divisor = Option.option();
  public final Option<Boolean> cash = Option.option();
  public final Option<CurrencyFormatStyleType> style = Option.option();
  public final Option<CurrencySymbolWidthType> symbolWidth = Option.option();

  public CurrencyFormatOptions() {
  }

  public CurrencyFormatOptions(CurrencyFormatOptions arg) {
    super(arg);
    this.divisor.set(arg.divisor);
    this.cash.set(arg.cash);
    this.style.set(arg.style);
    this.symbolWidth.set(arg.symbolWidth);
  }

  public CurrencyFormatOptions divisor(Integer arg) {
    this.divisor.set(arg);
    return this;
  }

  public CurrencyFormatOptions divisor(Option<Integer> arg) {
    this.divisor.set(arg);
    return this;
  }

  public CurrencyFormatOptions cash(Boolean arg) {
    this.cash.set(arg);
    return this;
  }

  public CurrencyFormatOptions cash(Option<Boolean> arg) {
    this.cash.set(arg);
    return this;
  }

  public CurrencyFormatOptions style(CurrencyFormatStyleType arg) {
    this.style.set(arg);
    return this;
  }

  public CurrencyFormatOptions style(Option<CurrencyFormatStyleType> arg) {
    this.style.set(arg);
    return this;
  }

  public CurrencyFormatOptions symbolWidth(CurrencySymbolWidthType arg) {
    this.symbolWidth.set(arg);
    return this;
  }

  public CurrencyFormatOptions symbolWidth(Option<CurrencySymbolWidthType> arg) {
    this.symbolWidth.set(arg);
    return this;
  }

  public CurrencyFormatOptions group(Boolean arg) {
    this.group.set(arg);
    return this;
  }

  public CurrencyFormatOptions group(Option<Boolean> arg) {
    this.group.set(arg);
    return this;
  }

  public CurrencyFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public CurrencyFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public CurrencyFormatOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public CurrencyFormatOptions round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public CurrencyFormatOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions minimumIntegerDigits(Option<Integer> arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions maximumFractionDigits(Option<Integer> arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions minimumFractionDigits(Option<Integer> arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions maximumSignificantDigits(Option<Integer> arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public CurrencyFormatOptions minimumSignificantDigits(Option<Integer> arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public static CurrencyFormatOptions build() {
    return new CurrencyFormatOptions();
  }

  public CurrencyFormatOptions copy() {
    return new CurrencyFormatOptions(this);
  }

  public CurrencyFormatOptions mergeIf(CurrencyFormatOptions ...args) {
    CurrencyFormatOptions o = new CurrencyFormatOptions(this);
    for (CurrencyFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(CurrencyFormatOptions o) {
    super._mergeIf(o);
    this.divisor.setIf(o.divisor);
    this.cash.setIf(o.cash);
    this.style.setIf(o.style);
    this.symbolWidth.setIf(o.symbolWidth);
  }

  public CurrencyFormatOptions merge(CurrencyFormatOptions ...args) {
    CurrencyFormatOptions o = new CurrencyFormatOptions(this);
    for (CurrencyFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(CurrencyFormatOptions o) {
    super._merge(o);
    this.divisor.set(o.divisor);
    this.cash.set(o.cash);
    this.style.set(o.style);
    this.symbolWidth.set(o.symbolWidth);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("CurrencyFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    super._tostring(buf);
    if (divisor.ok()) {
      buf.append("divisor=").append(divisor).append(' ');
    }
    if (cash.ok()) {
      buf.append("cash=").append(cash).append(' ');
    }
    if (style.ok()) {
      buf.append("style=").append(style).append(' ');
    }
    if (symbolWidth.ok()) {
      buf.append("symbolWidth=").append(symbolWidth).append(' ');
    }
  }

}
