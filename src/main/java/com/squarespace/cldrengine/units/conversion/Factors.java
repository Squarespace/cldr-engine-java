package com.squarespace.cldrengine.units.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.squarespace.cldrengine.api.UnitType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Factors {

  public static final String PI = "3.14159265358979323846";

  public static final List<FactorDef> ACCELERATION = Arrays.asList(
      def(UnitType.G_FORCE, "9.80665", UnitType.METER_PER_SECOND_SQUARED));

  public static final List<FactorDef> ANGLE = Arrays.asList(
      def(UnitType.REVOLUTION, "360", UnitType.DEGREE),
      def(UnitType.ARC_MINUTE, "1/60", UnitType.DEGREE),
      def(UnitType.ARC_SECOND, "1/60", UnitType.ARC_MINUTE),
      def(UnitType.RADIAN, "0.5 / " + PI, UnitType.REVOLUTION));

  public static final List<FactorDef> AREA = Arrays.asList(
      def(UnitType.SQUARE_KILOMETER, "1000000", UnitType.SQUARE_METER),
      def(UnitType.HECTARE, "10000", UnitType.SQUARE_METER),
      def(UnitType.SQUARE_CENTIMETER, "1 / 10000", UnitType.SQUARE_METER),
      def(UnitType.SQUARE_CENTIMETER, "2500 / 16129", UnitType.SQUARE_INCH),
      def(UnitType.SQUARE_MILE, "40468564224 / 15625", UnitType.SQUARE_METER),
      def(UnitType.SQUARE_MILE, "3097600", UnitType.SQUARE_YARD),
      def(UnitType.SQUARE_MILE, "27878400", UnitType.SQUARE_FOOT),
      def(UnitType.ACRE, "43560", UnitType.SQUARE_FOOT),
      def(UnitType.SQUARE_YARD, "9", UnitType.SQUARE_FOOT),
      def(UnitType.SQUARE_FOOT, "144", UnitType.SQUARE_INCH)
  );

  public static final List<FactorDef> CONSUMPTION = Arrays.asList(
      def(UnitType.LITER_PER_100KILOMETERS, "1/100", UnitType.LITER_PER_KILOMETER)
  );

  public static final List<FactorDef> DIGITAL_BASE = kfactors("1000",
      UnitType.TERABIT,
      UnitType.GIGABIT,
      UnitType.MEGABIT,
      UnitType.KILOBIT,
      UnitType.BIT);

  public static final List<FactorDef> DIGITAL = kfactors("1024",
      UnitType.TERABYTE,
      UnitType.GIGABYTE,
      UnitType.MEGABYTE,
      UnitType.KILOBYTE,
      UnitType.BYTE);

  public static final List<FactorDef> DIGITAL_DECIMAL = kfactors("1000",
      UnitType.TERABYTE,
      UnitType.GIGABYTE,
      UnitType.MEGABYTE,
      UnitType.KILOBYTE,
      UnitType.BYTE);

  private static FactorDef def(UnitType src, String factor, UnitType dst) {
    return new FactorDef(src, factor, dst);
  }

  /**
   * Create a series of definitions that are multiples of the same base factor.
   */
  private static List<FactorDef> kfactors (String factor, UnitType ...units) {
    List<FactorDef> factors = new ArrayList<>();
    for (int i = 1; i < units.length; i++) {
      factors.add(def(units[i - 1], factor, units[i]));
    }
    return factors;
  }
}
