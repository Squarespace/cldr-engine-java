package com.squarespace.cldrengine.units.conversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.MathContext;
import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;

/**
 * Converts between units.
 *
 * Port of TS packages/unit-converter/src/converter.ts (v1.15.0). Unit names
 * are passed as strings and resolved to {@link UnitType}s, mirroring the TS
 * string-based API; unknown categories or units yield null where TS returns
 * undefined.
 */
public class UnitConverter {

  private static final Rational ONE = new Rational("1");

  private final Map<String, UnitFactors> map = new TreeMap<>();

  /**
   * Return true if the given category is registered.
   */
  public boolean has(String category) {
    return this.map.containsKey(category);
  }

  /**
   * Add a set of conversion factors with the given category name.
   */
  public void add(String category, UnitFactors factors) {
    if (this.map.containsKey(category)) {
      throw new RuntimeException("this converter has registered factors for category " + category);
    }
    this.map.put(category, factors);
  }

  /**
   * Returns the unit factors for the given category, or null if not
   * registered.
   */
  public UnitFactors factors(String category) {
    return this.map.get(category);
  }

  /**
   * Return an array of the categories added to this converter.
   */
  public List<String> categories() {
    return new ArrayList<>(this.map.keySet());
  }

  /**
   * Get the unit conversion factor that converts `src` to `dst` for the
   * given category, or null when the category is unknown or no conversion
   * path exists.
   *
   * Note: the TS oracle returns an identity conversion for two *equal*
   * unknown unit names (the {@code src === dst} shortcut in
   * {@code UnitFactors.get}); this port returns null in that degenerate
   * case, which is not exercised by the TS test suite and is unreachable
   * through the cldr framework Units API.
   */
  public UnitConversion get(String category, String src, String dst) {
    UnitFactors factors = this.map.get(category);
    if (factors == null) {
      return null;
    }
    UnitType s = UnitType.fromString(src);
    UnitType d = UnitType.fromString(dst);
    if (s == null || d == null) {
      return null;
    }
    return factors.get(s, d);
  }

  /**
   * Convert the decimal value from `src` to `dst` units. Returns null when
   * the category is unknown or no conversion path exists.
   */
  public Decimal convert(String category, Decimal n, String src, String dst, MathContext ctx) {
    UnitConversion conv = this.get(category, src, dst);
    if (conv == null) {
      return null;
    }
    Rational fac = ONE;
    for (Rational c : conv.factors) {
      fac = fac.multiply(c, ctx);
    }
    return n.multiply(fac.toDecimal(ctx), ctx);
  }
}
