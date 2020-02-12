package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.api.AltType;
import lombok.Generated;

@Generated
public class DisplayNameOptions {

  public final Option<AltType> type = Option.option();
  public final Option<ContextType> context = Option.option();

  public DisplayNameOptions() {
  }

  public DisplayNameOptions(DisplayNameOptions arg) {
    this.type.set(arg.type);
    this.context.set(arg.context);
  }

  public DisplayNameOptions type(AltType arg) {
    this.type.set(arg);
    return this;
  }

  public DisplayNameOptions type(Option<AltType> arg) {
    this.type.set(arg);
    return this;
  }

  public DisplayNameOptions context(ContextType arg) {
    this.context.set(arg);
    return this;
  }

  public DisplayNameOptions context(Option<ContextType> arg) {
    this.context.set(arg);
    return this;
  }

  public static DisplayNameOptions build() {
    return new DisplayNameOptions();
  }

  public DisplayNameOptions copy() {
    return new DisplayNameOptions(this);
  }

  public DisplayNameOptions mergeIf(DisplayNameOptions ...args) {
    DisplayNameOptions o = new DisplayNameOptions(this);
    for (DisplayNameOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(DisplayNameOptions o) {
    this.type.setIf(o.type);
    this.context.setIf(o.context);
  }

  public DisplayNameOptions merge(DisplayNameOptions ...args) {
    DisplayNameOptions o = new DisplayNameOptions(this);
    for (DisplayNameOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(DisplayNameOptions o) {
    this.type.set(o.type);
    this.context.set(o.context);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("DisplayNameOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (type.ok()) {
      buf.append("type=").append(type).append(' ');
    }
    if (context.ok()) {
      buf.append("context=").append(context).append(' ');
    }
  }

}
