package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.NumberSystemCategory;
import java.util.Map;
public class NumbersSchema {

  public final FieldArrow minimumGroupingDigits;
  public final Vector1Arrow<NumberSystemCategory> numberSystems;
  public final Map<String, NumberSystemInfo> numberSystem;

  public NumbersSchema(
      FieldArrow minimumGroupingDigits,
      Vector1Arrow<NumberSystemCategory> numberSystems,
      Map<String, NumberSystemInfo> numberSystem) {
    this.minimumGroupingDigits = minimumGroupingDigits;
    this.numberSystems = numberSystems;
    this.numberSystem = numberSystem;
  }

}
