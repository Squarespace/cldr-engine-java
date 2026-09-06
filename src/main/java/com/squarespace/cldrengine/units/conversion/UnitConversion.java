package com.squarespace.cldrengine.units.conversion;

import java.util.List;

import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.ToString;

/**
 * Represents a conversion path from source to destination unit, the
 * rational conversion factors, and the combined additive offset.
 *
 * A value `v` in the source unit converts to the destination unit as
 * `v × (factors[0] × factors[1] × ...) + offset`. The offset is only
 * present for affine conversions, e.g. temperature.
 */
@Generated
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UnitConversion {

  public final List<UnitType> path;
  public final List<Rational> factors;
  public final Rational offset;

  public UnitConversion(List<UnitType> path, List<Rational> factors) {
    this(path, factors, null);
  }

}
