package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.ContextType;

public class CurrencyDisplayNameOptions {

  public final Option<ContextType> context = Option.option();

  public CurrencyDisplayNameOptions() {
  }

  public CurrencyDisplayNameOptions(CurrencyDisplayNameOptions arg) {
    this.context.set(arg.context);
  }

  public CurrencyDisplayNameOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public CurrencyDisplayNameOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public static CurrencyDisplayNameOptions build() {
    return new CurrencyDisplayNameOptions();
  }

  public CurrencyDisplayNameOptions copy() {
    return new CurrencyDisplayNameOptions(this);
  }

  public CurrencyDisplayNameOptions mergeIf(CurrencyDisplayNameOptions ...args) {
    CurrencyDisplayNameOptions o = new CurrencyDisplayNameOptions(this);
    for (CurrencyDisplayNameOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(CurrencyDisplayNameOptions o) {
    this.context.setIf(o.context);
  }

  public CurrencyDisplayNameOptions merge(CurrencyDisplayNameOptions ...args) {
    CurrencyDisplayNameOptions o = new CurrencyDisplayNameOptions(this);
    for (CurrencyDisplayNameOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(CurrencyDisplayNameOptions o) {
    this.context.set(o.context);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("CurrencyDisplayNameOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
  }

}
