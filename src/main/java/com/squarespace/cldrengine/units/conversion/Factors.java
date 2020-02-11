package com.squarespace.cldrengine.units.conversion;

import static com.squarespace.cldrengine.utils.ListUtils.concat;

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
      def(UnitType.SQUARE_FOOT, "144", UnitType.SQUARE_INCH));

  public static final List<FactorDef> CONSUMPTION = Arrays.asList(
      def(UnitType.LITER_PER_100KILOMETERS, "1/100", UnitType.LITER_PER_KILOMETER));

  public static final List<FactorDef> DIGITAL_BASE = kfactors("1000",
      UnitType.TERABIT,
      UnitType.GIGABIT,
      UnitType.MEGABIT,
      UnitType.KILOBIT,
      UnitType.BIT);

  public static final List<FactorDef> DIGITAL = concat(
      DIGITAL_BASE, kfactors("1024",
          UnitType.TERABYTE,
          UnitType.GIGABYTE,
          UnitType.MEGABYTE,
          UnitType.KILOBYTE,
          UnitType.BYTE));

  public static final List<FactorDef> DIGITAL_DECIMAL = concat(
      DIGITAL_BASE, kfactors("1000",
          UnitType.TERABYTE,
          UnitType.GIGABYTE,
          UnitType.MEGABYTE,
          UnitType.KILOBYTE,
          UnitType.BYTE));

  /**
   * Duration factors. Values for month, year, century are approximate. If you want accurate duration conversions from a
   * given date, use calendar math.
   */
  public static final List<FactorDef> DURATION = Arrays.asList(
      def(UnitType.CENTURY, "315569520", UnitType.SECOND),
      def(UnitType.YEAR, "12", UnitType.MONTH),
      def(UnitType.YEAR, "31556952", UnitType.SECOND),
      def(UnitType.MONTH, "30.436875", UnitType.DAY),
      def(UnitType.WEEK, "7", UnitType.DAY),
      def(UnitType.DAY, "24", UnitType.HOUR),
      def(UnitType.HOUR, "60", UnitType.MINUTE),
      def(UnitType.MINUTE, "60", UnitType.SECOND),
      def(UnitType.SECOND, "1000", UnitType.MILLISECOND),
      def(UnitType.MILLISECOND, "1000", UnitType.MICROSECOND),
      def(UnitType.MICROSECOND, "1000", UnitType.NANOSECOND));

  public static final List<FactorDef> ELECTRIC = Arrays.asList(
      def(UnitType.AMPERE, "1000", UnitType.MILLIAMPERE));

  public static final List<FactorDef> ENERGY = Arrays.asList(
      def(UnitType.KILOJOULE, "1000", UnitType.JOULE),
      def(UnitType.KILOWATT_HOUR, "3600000", UnitType.JOULE),
      def(UnitType.CALORIE, "4.1868", UnitType.JOULE),
      def(UnitType.FOODCALORIE, "523 / 125", UnitType.JOULE),
      def(UnitType.KILOJOULE, "1000", UnitType.CALORIE));

  public static final List<FactorDef> FORCE = Arrays.asList(
      def(UnitType.POUND_FORCE, "4.448222", UnitType.NEWTON));

  public static final List<FactorDef> FREQUENCY = kfactors("1000",
      UnitType.GIGAHERTZ, UnitType.MEGAHERTZ, UnitType.KILOHERTZ, UnitType.HERTZ);

  public static final List<FactorDef> GRAPHICS_PER = Arrays.asList(
      def(UnitType.DOT_PER_INCH, "1", UnitType.PIXEL_PER_INCH),
      def(UnitType.DOT_PER_CENTIMETER, "2.54", UnitType.DOT_PER_INCH));

  public static final List<FactorDef> GRAPHICS_PIXEL = Arrays.asList(
      def(UnitType.MEGAPIXEL, "1000000", UnitType.PIXEL));

  public static final List<FactorDef> LENGTH = Arrays.asList(
      def(UnitType.KILOMETER, "100000", UnitType.CENTIMETER),
      def(UnitType.METER, "100", UnitType.CENTIMETER),
      def(UnitType.DECIMETER, "10", UnitType.CENTIMETER),
      def(UnitType.MILLIMETER, "1 / 10", UnitType.CENTIMETER),
      def(UnitType.MICROMETER, "1 / 10000", UnitType.CENTIMETER),
      def(UnitType.NANOMETER, "1 / 10000000", UnitType.CENTIMETER),
      def(UnitType.PICOMETER, "1 / 10000000000", UnitType.CENTIMETER),

      def(UnitType.MILE, "5280", UnitType.FOOT),
      def(UnitType.YARD, "36", UnitType.INCH),
      def(UnitType.FOOT, "12", UnitType.INCH),
      def(UnitType.INCH, "2.54", UnitType.CENTIMETER),

      def(UnitType.LIGHT_YEAR, "9460730472580800", UnitType.METER),
      def(UnitType.ASTRONOMICAL_UNIT, "149597870700", UnitType.METER),
      def(UnitType.PARSEC, "648000 / " + PI, UnitType.ASTRONOMICAL_UNIT),

      def(UnitType.FURLONG, "220", UnitType.YARD),
      def(UnitType.FATHOM, "6", UnitType.FOOT),
      def(UnitType.NAUTICAL_MILE, "1852", UnitType.METER),
      def(UnitType.MILE_SCANDINAVIAN, "10000", UnitType.METER),

      def(UnitType.POINT, "1 / 72", UnitType.INCH));

  public static final List<FactorDef> MASS = Arrays.asList(
      def(UnitType.METRIC_TON, "1000", UnitType.KILOGRAM),
      def(UnitType.GRAM, "1 / 1000", UnitType.KILOGRAM),
      def(UnitType.MILLIGRAM, "1 / 1000", UnitType.GRAM),
      def(UnitType.MICROGRAM, "1 / 1000", UnitType.MILLIGRAM),

      def(UnitType.CARAT, "200", UnitType.MILLIGRAM),

      def(UnitType.POUND, "45359237 / 100000000", UnitType.KILOGRAM),
      def(UnitType.TON, "2000", UnitType.POUND),
      def(UnitType.STONE, "14", UnitType.POUND),
      def(UnitType.OUNCE, "1 / 16", UnitType.POUND),
      def(UnitType.OUNCE_TROY, "12 / 175", UnitType.POUND));

  public static final List<FactorDef> POWER = concat(
      Arrays.asList(def(UnitType.HORSEPOWER, "745.69987158227", UnitType.WATT)),
      kfactors("1000",
          UnitType.GIGAWATT, UnitType.MEGAWATT, UnitType.KILOWATT, UnitType.WATT));

  public static final List<FactorDef> PRESSURE = Arrays.asList(
      def(UnitType.HECTOPASCAL, "1", UnitType.MILLIBAR),
      def(UnitType.HECTOPASCAL, "129032000000 / 8896443230521", UnitType.POUND_PER_SQUARE_INCH),
      def(UnitType.INCH_HG, "33.86389", UnitType.HECTOPASCAL),
      def(UnitType.MILLIMETER_OF_MERCURY, "1013.25 / 760", UnitType.HECTOPASCAL));

  public static final List<FactorDef> SPEED = Arrays.asList(
      def(UnitType.KILOMETER_PER_HOUR, "5 / 18", UnitType.METER_PER_SECOND),
      def(UnitType.MILE_PER_HOUR, "1397 / 3125", UnitType.METER_PER_SECOND),
      def(UnitType.KNOT, "463 / 900", UnitType.METER_PER_SECOND));

  public static final List<FactorDef> TORQUE = Arrays.asList(
      def(UnitType.POUND_FOOT, "1.35582", UnitType.NEWTON_METER));

  public static final List<FactorDef> VOLUME_BASE = Arrays.asList(
      def(UnitType.CUBIC_KILOMETER, "1000000000", UnitType.CUBIC_METER),
      def(UnitType.CUBIC_METER, "1000000000", UnitType.CUBIC_CENTIMETER),
      def(UnitType.CUBIC_CENTIMETER, "0.06102374409473", UnitType.CUBIC_INCH),

      def(UnitType.LITER, "1000", UnitType.CUBIC_CENTIMETER),
      def(UnitType.MEGALITER, "1000000", UnitType.LITER),
      def(UnitType.HECTOLITER, "100", UnitType.LITER),
      def(UnitType.DECILITER, "1 / 10", UnitType.LITER),
      def(UnitType.CENTILITER, "1 / 100", UnitType.LITER),
      def(UnitType.MILLILITER, "1 / 1000", UnitType.LITER),

      def(UnitType.CUP_METRIC, "1 / 4", UnitType.LITER),

      def(UnitType.CUBIC_MILE, "5451776000", UnitType.CUBIC_YARD),
      def(UnitType.CUBIC_YARD, "27", UnitType.CUBIC_FOOT),
      def(UnitType.CUBIC_FOOT, "1 / 35.31466672148859", UnitType.CUBIC_METER),
      def(UnitType.CUBIC_INCH, "1 / 1728", UnitType.CUBIC_FOOT),

      def(UnitType.ACRE_FOOT, "43560", UnitType.CUBIC_FOOT),

      // To be correct, metric pint conversions would need to be localized.
      // Pinning this at 500mL for now.
      def(UnitType.PINT_METRIC, "500", UnitType.MILLILITER),

      def(UnitType.TABLESPOON, "1 / 2", UnitType.FLUID_OUNCE),
      def(UnitType.TEASPOON, "1 / 6", UnitType.FLUID_OUNCE),
      def(UnitType.FLUID_OUNCE_IMPERIAL, "0.960759940", UnitType.FLUID_OUNCE));

  // These are US units. Grouped to be overridden below for UK.
  public static final List<FactorDef> VOLUME = concat(
      VOLUME_BASE, Arrays.asList(
          def(UnitType.GALLON, "3.785411784", UnitType.LITER),
          def(UnitType.GALLON_IMPERIAL, "4.54609", UnitType.LITER),
          def(UnitType.BUSHEL, "2150.42", UnitType.CUBIC_INCH),
          def(UnitType.GALLON, "231", UnitType.CUBIC_INCH),
          def(UnitType.FLUID_OUNCE, "1 / 128", UnitType.GALLON),
          def(UnitType.QUART, "1 / 4", UnitType.GALLON),
          def(UnitType.PINT, "1 / 8", UnitType.GALLON),
          def(UnitType.CUP, "8", UnitType.FLUID_OUNCE)));

  public static final List<FactorDef> VOLUME_UK = concat(
      VOLUME_BASE, Arrays.asList(
          def(UnitType.GALLON, "4.54609", UnitType.LITER),
          def(UnitType.GALLON_IMPERIAL, "4.54609", UnitType.LITER),
          def(UnitType.BUSHEL, "8", UnitType.GALLON_IMPERIAL),
          def(UnitType.GALLON, "4.54609", UnitType.LITER),
          def(UnitType.FLUID_OUNCE, "1 / 160", UnitType.GALLON_IMPERIAL),
          def(UnitType.QUART, "1 / 4", UnitType.GALLON_IMPERIAL),
          def(UnitType.PINT, "1 / 8", UnitType.GALLON_IMPERIAL),
          def(UnitType.CUP, "284.1", UnitType.MILLILITER)));

  private static FactorDef def(UnitType src, String factor, UnitType dst) {
    return new FactorDef(src, factor, dst);
  }

  /**
   * Create a series of definitions that are multiples of the same base factor.
   */
  private static List<FactorDef> kfactors(String factor, UnitType... units) {
    List<FactorDef> factors = new ArrayList<>();
    for (int i = 1; i < units.length; i++) {
      factors.add(def(units[i - 1], factor, units[i]));
    }
    return factors;
  }
}
