package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class Quantity {

  public final Option<Decimal> value = Option.option();
  public final Option<UnitType> unit = Option.option();
  public final Option<UnitType> per = Option.option();
  public final Option<UnitType> times = Option.option();

  public Quantity() {
  }

  public Quantity(Quantity arg) {
    this.value.set(arg.value);
    this.unit.set(arg.unit);
    this.per.set(arg.per);
    this.times.set(arg.times);
  }

  public Quantity value(Decimal arg) {
    this.value.set(arg);
    return this;
  }

  public Quantity value(Option<Decimal> arg) {
    this.value.set(arg);
    return this;
  }

  public Quantity unit(UnitType arg) {
    this.unit.set(arg);
    return this;
  }

  public Quantity unit(Option<UnitType> arg) {
    this.unit.set(arg);
    return this;
  }

  public Quantity per(UnitType arg) {
    this.per.set(arg);
    return this;
  }

  public Quantity per(Option<UnitType> arg) {
    this.per.set(arg);
    return this;
  }

  public Quantity times(UnitType arg) {
    this.times.set(arg);
    return this;
  }

  public Quantity times(Option<UnitType> arg) {
    this.times.set(arg);
    return this;
  }

  public static Quantity build() {
    return new Quantity();
  }

  public Quantity copy() {
    return new Quantity(this);
  }

  public Quantity mergeIf(Quantity ...args) {
    Quantity o = new Quantity(this);
    for (Quantity arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(Quantity o) {
    this.value.setIf(o.value);
    this.unit.setIf(o.unit);
    this.per.setIf(o.per);
    this.times.setIf(o.times);
  }

  public Quantity merge(Quantity ...args) {
    Quantity o = new Quantity(this);
    for (Quantity arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(Quantity o) {
    this.value.set(o.value);
    this.unit.set(o.unit);
    this.per.set(o.per);
    this.times.set(o.times);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("Quantity( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (value.ok()) {
      buf.append("value=").append(value).append(' ');
    }
    if (unit.ok()) {
      buf.append("unit=").append(unit).append(' ');
    }
    if (per.ok()) {
      buf.append("per=").append(per).append(' ');
    }
    if (times.ok()) {
      buf.append("times=").append(times).append(' ');
    }
  }

}
