package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode(callSuper = true)
public class DecimalFormatOptions extends NumberFormatOptions {

  public final Option<DecimalFormatStyleType> style = Option.option();
  public final Option<Boolean> negativeZero = Option.option();
  public final Option<Integer> divisor = Option.option();
  public final Option<ContextType> context = Option.option();
  public final Option<String> errors = Option.option();

  public DecimalFormatOptions() {
  }

  public DecimalFormatOptions(DecimalFormatOptions arg) {
    super(arg);
    this.style.set(arg.style);
    this.negativeZero.set(arg.negativeZero);
    this.divisor.set(arg.divisor);
    this.context.set(arg.context);
    this.errors.set(arg.errors);
  }

  public DecimalFormatOptions style(DecimalFormatStyleType arg) {
    this.style.set(arg);
    return this;
  }

  public DecimalFormatOptions style(Option<DecimalFormatStyleType> arg) {
    this.style.set(arg);
    return this;
  }

  public DecimalFormatOptions negativeZero(Boolean arg) {
    this.negativeZero.set(arg);
    return this;
  }

  public DecimalFormatOptions negativeZero(Option<Boolean> arg) {
    this.negativeZero.set(arg);
    return this;
  }

  public DecimalFormatOptions divisor(Integer arg) {
    this.divisor.set(arg);
    return this;
  }

  public DecimalFormatOptions divisor(Option<Integer> arg) {
    this.divisor.set(arg);
    return this;
  }

  public DecimalFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public DecimalFormatOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public DecimalFormatOptions errors(String arg) {
    this.errors.set(arg);
    return this;
  }

  public DecimalFormatOptions errors(Option<String> arg) {
    this.errors.set(arg);
    return this;
  }

  public DecimalFormatOptions group(Boolean arg) {
    this.group.set(arg);
    return this;
  }

  public DecimalFormatOptions group(Option<Boolean> arg) {
    this.group.set(arg);
    return this;
  }

  public DecimalFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public DecimalFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public DecimalFormatOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public DecimalFormatOptions round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public DecimalFormatOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions minimumIntegerDigits(Option<Integer> arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions maximumFractionDigits(Option<Integer> arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions minimumFractionDigits(Option<Integer> arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions maximumSignificantDigits(Option<Integer> arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public DecimalFormatOptions minimumSignificantDigits(Option<Integer> arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public static DecimalFormatOptions build() {
    return new DecimalFormatOptions();
  }

  public DecimalFormatOptions copy() {
    return new DecimalFormatOptions(this);
  }

  public DecimalFormatOptions mergeIf(DecimalFormatOptions ...args) {
    DecimalFormatOptions o = new DecimalFormatOptions(this);
    for (DecimalFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DecimalFormatOptions o) {
    super._mergeIf(o);
    this.style.setIf(o.style);
    this.negativeZero.setIf(o.negativeZero);
    this.divisor.setIf(o.divisor);
    this.context.setIf(o.context);
    this.errors.setIf(o.errors);
  }

  public DecimalFormatOptions merge(DecimalFormatOptions ...args) {
    DecimalFormatOptions o = new DecimalFormatOptions(this);
    for (DecimalFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DecimalFormatOptions o) {
    super._merge(o);
    this.style.set(o.style);
    this.negativeZero.set(o.negativeZero);
    this.divisor.set(o.divisor);
    this.context.set(o.context);
    this.errors.set(o.errors);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DecimalFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    super._tostring(buf);
    if (style.ok()) {
      buf.append("style=").append(style).append(' ');
    }
    if (negativeZero.ok()) {
      buf.append("negativeZero=").append(negativeZero).append(' ');
    }
    if (divisor.ok()) {
      buf.append("divisor=").append(divisor).append(' ');
    }
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
    if (errors.ok()) {
      buf.append("errors=").append(errors).append(' ');
    }
  }

}
