package com.squarespace.cldrengine.units.conversion;

import com.squarespace.cldrengine.api.UnitType;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class FactorDef {

  public final UnitType src;
  public final String factor;
  public final UnitType dst;

}
