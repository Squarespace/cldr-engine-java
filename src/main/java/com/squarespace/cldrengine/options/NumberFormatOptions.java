package com.squarespace.cldrengine.options;

import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.RoundingModeType;
import com.squarespace.cldrengine.numbers.DecimalAdjustOptions;

public class NumberFormatOptions extends DecimalAdjustOptions {

  public final Option<ContextType> context = Option.option();

  public NumberFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public NumberFormatOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public NumberFormatOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public NumberFormatOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public NumberFormatOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public NumberFormatOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public NumberFormatOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

}