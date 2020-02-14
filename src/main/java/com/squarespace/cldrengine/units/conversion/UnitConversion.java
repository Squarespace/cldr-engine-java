package com.squarespace.cldrengine.units.conversion;

import java.util.List;

import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.ToString;

@Generated
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UnitConversion {

  public final List<UnitType> path;
  public final List<Rational> factors;

}
