package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.RoundingModeType;
import lombok.Generated;

@Generated
public class RelativeTimeFieldFormatOptions extends NumberFormatOptions {

  public final Option<DateFieldWidthType> width = Option.option();
  public final Option<ContextType> context = Option.option();
  public final Option<Boolean> numericOnly = Option.option();
  public final Option<Boolean> alwaysNow = Option.option();

  public RelativeTimeFieldFormatOptions() {
  }

  public RelativeTimeFieldFormatOptions(RelativeTimeFieldFormatOptions arg) {
    super(arg);
    this.width.set(arg.width);
    this.context.set(arg.context);
    this.numericOnly.set(arg.numericOnly);
    this.alwaysNow.set(arg.alwaysNow);
  }

  public RelativeTimeFieldFormatOptions width(DateFieldWidthType arg) {
    this.width.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions width(Option<DateFieldWidthType> arg) {
    this.width.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions numericOnly(Boolean arg) {
    this.numericOnly.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions numericOnly(Option<Boolean> arg) {
    this.numericOnly.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions alwaysNow(Boolean arg) {
    this.alwaysNow.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions alwaysNow(Option<Boolean> arg) {
    this.alwaysNow.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions group(Boolean arg) {
    this.group.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions group(Option<Boolean> arg) {
    this.group.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions numberSystem(String arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions numberSystem(Option<String> arg) {
    this.numberSystem.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions minimumIntegerDigits(Integer arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions minimumIntegerDigits(Option<Integer> arg) {
    this.minimumIntegerDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions maximumFractionDigits(Integer arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions maximumFractionDigits(Option<Integer> arg) {
    this.maximumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions minimumFractionDigits(Integer arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions minimumFractionDigits(Option<Integer> arg) {
    this.minimumFractionDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions maximumSignificantDigits(Integer arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions maximumSignificantDigits(Option<Integer> arg) {
    this.maximumSignificantDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions minimumSignificantDigits(Integer arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public RelativeTimeFieldFormatOptions minimumSignificantDigits(Option<Integer> arg) {
    this.minimumSignificantDigits.set(arg);
    return this;
  }

  public static RelativeTimeFieldFormatOptions build() {
    return new RelativeTimeFieldFormatOptions();
  }

  public RelativeTimeFieldFormatOptions copy() {
    return new RelativeTimeFieldFormatOptions(this);
  }

  public RelativeTimeFieldFormatOptions mergeIf(RelativeTimeFieldFormatOptions ...args) {
    RelativeTimeFieldFormatOptions o = new RelativeTimeFieldFormatOptions(this);
    for (RelativeTimeFieldFormatOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(RelativeTimeFieldFormatOptions o) {
    super._mergeIf(o);
    this.width.setIf(o.width);
    this.context.setIf(o.context);
    this.numericOnly.setIf(o.numericOnly);
    this.alwaysNow.setIf(o.alwaysNow);
  }

  public RelativeTimeFieldFormatOptions merge(RelativeTimeFieldFormatOptions ...args) {
    RelativeTimeFieldFormatOptions o = new RelativeTimeFieldFormatOptions(this);
    for (RelativeTimeFieldFormatOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(RelativeTimeFieldFormatOptions o) {
    super._merge(o);
    this.width.set(o.width);
    this.context.set(o.context);
    this.numericOnly.set(o.numericOnly);
    this.alwaysNow.set(o.alwaysNow);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("RelativeTimeFieldFormatOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    super._tostring(buf);
    if (width.ok()) {
      buf.append("width=").append(width).append(' ');
    }
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
    if (numericOnly.ok()) {
      buf.append("numericOnly=").append(numericOnly).append(' ');
    }
    if (alwaysNow.ok()) {
      buf.append("alwaysNow=").append(alwaysNow).append(' ');
    }
  }

}
