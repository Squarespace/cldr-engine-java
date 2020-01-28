package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.UnitType;
public class UnitInfo {

  public final Vector2Arrow<PluralType, UnitType> unitPattern;
  public final Vector1Arrow<UnitType> displayName;
  public final Vector1Arrow<UnitType> perUnitPattern;
  public final FieldArrow perPattern;
  public final FieldArrow timesPattern;

  public UnitInfo(
      Vector2Arrow<PluralType, UnitType> unitPattern,
      Vector1Arrow<UnitType> displayName,
      Vector1Arrow<UnitType> perUnitPattern,
      FieldArrow perPattern,
      FieldArrow timesPattern) {
    this.unitPattern = unitPattern;
    this.displayName = displayName;
    this.perUnitPattern = perUnitPattern;
    this.perPattern = perPattern;
    this.timesPattern = timesPattern;
  }

}
