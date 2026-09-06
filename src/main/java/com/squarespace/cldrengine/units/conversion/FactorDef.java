package com.squarespace.cldrengine.units.conversion;

import com.squarespace.cldrengine.api.UnitType;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class FactorDef {

  public final UnitType src;
  public final String factor;
  public final UnitType dst;
  public final String offset;

  public FactorDef(UnitType src, String factor, UnitType dst) {
    this(src, factor, dst, null);
  }

}
