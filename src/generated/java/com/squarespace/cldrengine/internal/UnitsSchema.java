package com.squarespace.cldrengine.internal;


public class UnitsSchema {

  public final UnitInfo long_;
  public final UnitInfo narrow;
  public final UnitInfo short_;

  public UnitsSchema(
      UnitInfo long_,
      UnitInfo narrow,
      UnitInfo short_) {
    this.long_ = long_;
    this.narrow = narrow;
    this.short_ = short_;
  }

}
