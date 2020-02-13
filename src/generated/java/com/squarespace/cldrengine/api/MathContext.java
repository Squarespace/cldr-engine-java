package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.RoundingModeType;
import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class MathContext {

  public final Option<Integer> scale = Option.option();
  public final Option<Integer> precision = Option.option();
  public final Option<RoundingModeType> round = Option.option();

  public MathContext() {
  }

  public MathContext(MathContext arg) {
    this.scale.set(arg.scale);
    this.precision.set(arg.precision);
    this.round.set(arg.round);
  }

  public MathContext scale(Integer arg) {
    this.scale.set(arg);
    return this;
  }

  public MathContext scale(Option<Integer> arg) {
    this.scale.set(arg);
    return this;
  }

  public MathContext precision(Integer arg) {
    this.precision.set(arg);
    return this;
  }

  public MathContext precision(Option<Integer> arg) {
    this.precision.set(arg);
    return this;
  }

  public MathContext round(RoundingModeType arg) {
    this.round.set(arg);
    return this;
  }

  public MathContext round(Option<RoundingModeType> arg) {
    this.round.set(arg);
    return this;
  }

  public static MathContext build() {
    return new MathContext();
  }

  public MathContext copy() {
    return new MathContext(this);
  }

  public MathContext mergeIf(MathContext ...args) {
    MathContext o = new MathContext(this);
    for (MathContext arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(MathContext o) {
    this.scale.setIf(o.scale);
    this.precision.setIf(o.precision);
    this.round.setIf(o.round);
  }

  public MathContext merge(MathContext ...args) {
    MathContext o = new MathContext(this);
    for (MathContext arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(MathContext o) {
    this.scale.set(o.scale);
    this.precision.set(o.precision);
    this.round.set(o.round);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("MathContext( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (scale.ok()) {
      buf.append("scale=").append(scale).append(' ');
    }
    if (precision.ok()) {
      buf.append("precision=").append(precision).append(' ');
    }
    if (round.ok()) {
      buf.append("round=").append(round).append(' ');
    }
  }

}
