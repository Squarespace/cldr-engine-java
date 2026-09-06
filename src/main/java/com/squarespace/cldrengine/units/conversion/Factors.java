package com.squarespace.cldrengine.units.conversion;

import static com.squarespace.cldrengine.utils.ListUtils.concat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.squarespace.cldrengine.api.UnitType;

import lombok.experimental.UtilityClass;

/**
 * Mirror of TS packages/unit-converter/src/factors.ts (commit fa960378).
 *
 * CLDR-generated star tables (FactorsGenerated) are merged with the
 * hand-maintained extensions below. The generated edges come from CLDR
 * 48.2.0 supplemental/units.json (convertUnits + unitConstants); the hand
 * entries cover units CLDR does not model in convertUnits (prefix units,
 * compound units, and bridge edges) and pin down localized variants.
 */
@UtilityClass
public class Factors {

  public static final String PI = "3.14159265358979323846";

  /**
   * Conversions between CLDR acceleration units.
   */
  public static final List<FactorDef> ACCELERATION = concat(
      // hand-maintained (same value as CLDR's `gravity` constant)
      Arrays.asList(def(UnitType.G_FORCE, "9.80665", UnitType.METER_PER_SQUARE_SECOND)),
      FactorsGenerated.CLDR_FACTORS.get("acceleration"));

  /**
   * Conversions between CLDR angle units.
   */
  public static final List<FactorDef> ANGLE = concat(
      // hand-maintained sub-degree chain (radian and the star edges come from CLDR)
      Arrays.asList(
          def(UnitType.REVOLUTION, "360", UnitType.DEGREE),
          def(UnitType.ARC_MINUTE, "1/60", UnitType.DEGREE),
          def(UnitType.ARC_SECOND, "1/60", UnitType.ARC_MINUTE)),
      FactorsGenerated.CLDR_FACTORS.get("angle"));

  /**
   * Conversions between CLDR area units.
   */
  public static final List<FactorDef> AREA = concat(
      // hand-maintained square-unit chain (CLDR only models square-meter)
      Arrays.asList(
          def(UnitType.SQUARE_KILOMETER, "1000000", UnitType.SQUARE_METER),
          def(UnitType.SQUARE_CENTIMETER, "1 / 10000", UnitType.SQUARE_METER),
          def(UnitType.SQUARE_CENTIMETER, "2500 / 16129", UnitType.SQUARE_INCH),
          def(UnitType.SQUARE_MILE, "40468564224 / 15625", UnitType.SQUARE_METER),
          def(UnitType.SQUARE_MILE, "3097600", UnitType.SQUARE_YARD),
          def(UnitType.SQUARE_MILE, "27878400", UnitType.SQUARE_FOOT),
          def(UnitType.ACRE, "43560", UnitType.SQUARE_FOOT),
          def(UnitType.SQUARE_YARD, "9", UnitType.SQUARE_FOOT),
          def(UnitType.SQUARE_FOOT, "144", UnitType.SQUARE_INCH)),
      FactorsGenerated.CLDR_FACTORS.get("area"));

  /**
   * Conversions between CLDR consumption units.
   */
  public static final List<FactorDef> CONSUMPTION = Arrays.asList(
      def(UnitType.LITER_PER_100_KILOMETER, "1 / 100", UnitType.LITER_PER_KILOMETER));

  private static final List<FactorDef> DIGITAL_BASE = concat(
      kfactors("1000",
          UnitType.TERABIT,
          UnitType.GIGABIT,
          UnitType.MEGABIT,
          UnitType.KILOBIT,
          UnitType.BIT),
      Arrays.asList(def(UnitType.BYTE, "8", UnitType.BIT)));

  /**
   * Conversions between CLDR digital units.
   */
  public static final List<FactorDef> DIGITAL = concat(
      concat(DIGITAL_BASE, kfactors("1024",
          UnitType.TERABYTE,
          UnitType.GIGABYTE,
          UnitType.MEGABYTE,
          UnitType.KILOBYTE,
          UnitType.BYTE)),
      FactorsGenerated.CLDR_FACTORS.get("digital"));

  /**
   * Conversions between CLDR digital units where 1 kilobyte is 1000 bytes.
   */
  public static final List<FactorDef> DIGITAL_DECIMAL = concat(
      DIGITAL_BASE,
      kfactors("1000",
          UnitType.TERABYTE,
          UnitType.GIGABYTE,
          UnitType.MEGABYTE,
          UnitType.KILOBYTE,
          UnitType.BYTE));

  /**
   * Duration factors. Values for month, year, century are approximate. If you want accurate duration
   * conversions from a given date, use calendar math.
   */
  public static final List<FactorDef> DURATION = concat(
      Arrays.asList(
          def(UnitType.CENTURY, "3155695200", UnitType.SECOND),
          def(UnitType.YEAR, "12", UnitType.MONTH),
          def(UnitType.YEAR, "31556952", UnitType.SECOND),
          def(UnitType.MONTH, "30.436875", UnitType.DAY),
          def(UnitType.WEEK, "7", UnitType.DAY),
          def(UnitType.DAY, "24", UnitType.HOUR),
          def(UnitType.HOUR, "60", UnitType.MINUTE),
          def(UnitType.MINUTE, "60", UnitType.SECOND),
          def(UnitType.SECOND, "1000", UnitType.MILLISECOND),
          def(UnitType.MILLISECOND, "1000", UnitType.MICROSECOND),
          def(UnitType.MICROSECOND, "1000", UnitType.NANOSECOND)),
      FactorsGenerated.CLDR_FACTORS.get("duration"));

  /**
   * Conversions between CLDR electric units.
   */
  public static final List<FactorDef> ELECTRIC = concat(
      // hand-maintained (CLDR only models the base units)
      Arrays.asList(def(UnitType.AMPERE, "1000", UnitType.MILLIAMPERE)),
      FactorsGenerated.CLDR_FACTORS.get("electric"));

  /**
   * Conversions between CLDR energy units.
   */
  public static final List<FactorDef> ENERGY = concat(
      // hand-maintained prefix/compound units (calorie and foodcalorie come
      // from CLDR: 1 calorie = 4.184 J, 1 foodcalorie = 4184 J)
      Arrays.asList(
          def(UnitType.KILOJOULE, "1000", UnitType.JOULE),
          def(UnitType.KILOWATT_HOUR, "3600000", UnitType.JOULE),
          def(UnitType.KILOCALORIE, "1000", UnitType.CALORIE)),
      FactorsGenerated.CLDR_FACTORS.get("energy"));

  /**
   * Conversions between CLDR force units.
   */
  public static final List<FactorDef> FORCE = FactorsGenerated.CLDR_FACTORS.get("force");

  /**
   * Conversions between CLDR frequency units.
   */
  public static final List<FactorDef> FREQUENCY = concat(
      kfactors("1000",
          UnitType.GIGAHERTZ,
          UnitType.MEGAHERTZ,
          UnitType.KILOHERTZ,
          UnitType.HERTZ),
      FactorsGenerated.CLDR_FACTORS.get("frequency"));

  /**
   * Conversions between CLDR graphics 'per' units.
   */
  public static final List<FactorDef> GRAPHICS_PER = Arrays.asList(
      def(UnitType.DOT_PER_INCH, "1", UnitType.PIXEL_PER_INCH),
      def(UnitType.DOT_PER_CENTIMETER, "2.54", UnitType.DOT_PER_INCH));

  /**
   * Conversions between CLDR graphics 'pixel' units.
   */
  public static final List<FactorDef> GRAPHICS_PIXEL = concat(
      // hand-maintained (CLDR only models pixel, dot, and em)
      Arrays.asList(def(UnitType.MEGAPIXEL, "1000000", UnitType.PIXEL)),
      FactorsGenerated.CLDR_FACTORS.get("graphics"));

  /**
   * Conversions between CLDR illuminance / luminous units (candela, lumen, lux; these are different
   * dimensions and are not mutually convertible).
   */
  public static final List<FactorDef> ILLUMINANCE = FactorsGenerated.CLDR_FACTORS.get("illuminance");

  /**
   * Conversions between CLDR length units.
   */
  public static final List<FactorDef> LENGTH = concat(
      // hand-maintained sub-meter chain (meter star edges come from CLDR)
      Arrays.asList(
          def(UnitType.KILOMETER, "100000", UnitType.CENTIMETER),
          def(UnitType.METER, "100", UnitType.CENTIMETER),
          def(UnitType.DECIMETER, "10", UnitType.CENTIMETER),
          def(UnitType.MILLIMETER, "1 / 10", UnitType.CENTIMETER),
          def(UnitType.MICROMETER, "1 / 10000", UnitType.CENTIMETER),
          def(UnitType.NANOMETER, "1 / 10000000", UnitType.CENTIMETER),
          def(UnitType.PICOMETER, "1 / 10000000000", UnitType.CENTIMETER),

          // hand-maintained US chain
          def(UnitType.MILE, "5280", UnitType.FOOT),
          def(UnitType.YARD, "36", UnitType.INCH),
          def(UnitType.FOOT, "12", UnitType.INCH),
          def(UnitType.INCH, "2.54", UnitType.CENTIMETER),
          def(UnitType.FURLONG, "220", UnitType.YARD),
          def(UnitType.FATHOM, "6", UnitType.FOOT),
          def(UnitType.POINT, "1 / 72", UnitType.INCH)),
      FactorsGenerated.CLDR_FACTORS.get("length"));

  /**
   * Conversions between CLDR mass units.
   */
  public static final List<FactorDef> MASS = concat(
      // hand-maintained prefix chain (kilogram star edges come from CLDR)
      Arrays.asList(
          def(UnitType.GRAM, "1 / 1000", UnitType.KILOGRAM),
          def(UnitType.MILLIGRAM, "1 / 1000", UnitType.GRAM),
          def(UnitType.MICROGRAM, "1 / 1000", UnitType.MILLIGRAM),

          def(UnitType.CARAT, "200", UnitType.MILLIGRAM),
          def(UnitType.TON, "2000", UnitType.POUND),
          def(UnitType.STONE, "14", UnitType.POUND),
          def(UnitType.OUNCE, "1 / 16", UnitType.POUND),
          def(UnitType.OUNCE_TROY, "12 / 175", UnitType.POUND)),
      FactorsGenerated.CLDR_FACTORS.get("mass"));

  /**
   * Conversions between CLDR power units.
   */
  public static final List<FactorDef> POWER = concat(
      kfactors("1000",
          UnitType.GIGAWATT,
          UnitType.MEGAWATT,
          UnitType.KILOWATT,
          UnitType.WATT),
      FactorsGenerated.CLDR_FACTORS.get("power"));

  /**
   * Conversions between CLDR pressure units.
   */
  public static final List<FactorDef> PRESSURE = concat(
      // hand-maintained: CLDR does not model these units, and the pascal and
      // mercury-column bridges connect the two CLDR pressure dimensions
      Arrays.asList(
          def(UnitType.HECTOPASCAL, "100", UnitType.PASCAL),
          def(UnitType.HECTOPASCAL, "1", UnitType.MILLIBAR),
          def(UnitType.HECTOPASCAL, "129032000000 / 8896443230521", UnitType.POUND_FORCE_PER_SQUARE_INCH),
          def(UnitType.INCH_OFHG, "33.86389", UnitType.HECTOPASCAL),
          def(UnitType.MILLIMETER_OFHG, "1013.25 / 760", UnitType.HECTOPASCAL),
          def(UnitType.OFHG, "133.322387415", UnitType.PASCAL)),
      FactorsGenerated.CLDR_FACTORS.get("pressure"));

  /**
   * Conversions between CLDR radiation dose units (gray, sievert).
   */
  public static final List<FactorDef> RADIATION = FactorsGenerated.CLDR_FACTORS.get("radiation");

  /**
   * Conversions between CLDR radioactivity units (becquerel).
   */
  public static final List<FactorDef> RADIOACTIVITY = FactorsGenerated.CLDR_FACTORS.get("radioactivity");

  /**
   * Conversions between CLDR ratio units (part, percent, permille, permyriad, karat).
   */
  public static final List<FactorDef> RATIO = FactorsGenerated.CLDR_FACTORS.get("ratio");

  /**
   * Conversions between CLDR speed units.
   */
  public static final List<FactorDef> SPEED = concat(
      Arrays.asList(
          def(UnitType.KILOMETER_PER_HOUR, "5 / 18", UnitType.METER_PER_SECOND),
          def(UnitType.MILE_PER_HOUR, "1397 / 3125", UnitType.METER_PER_SECOND),
          def(UnitType.KNOT, "463 / 900", UnitType.METER_PER_SECOND)),
      FactorsGenerated.CLDR_FACTORS.get("speed"));

  /**
   * Conversions between CLDR substance units (item, mole; katal and ofglucose are different
   * dimensions and are not mutually convertible).
   */
  public static final List<FactorDef> SUBSTANCE = FactorsGenerated.CLDR_FACTORS.get("substance");

  /**
   * Conversions between CLDR temperature units, anchored to the kelvin. These are affine: the
   * fourth element is the additive offset in kelvin, so `kelvin = unit * factor + offset` (CLDR
   * 48.2.0 `convertUnits`).
   */
  public static final List<FactorDef> TEMPERATURE = new ArrayList<>(
      FactorsGenerated.CLDR_FACTORS.get("temperature"));

  /**
   * Conversions between CLDR torque units.
   */
  public static final List<FactorDef> TORQUE = Arrays.asList(
      def(UnitType.POUND_FORCE_FOOT, "1.35582", UnitType.NEWTON_METER));

  private static final List<FactorDef> VOLUME_BASE = Arrays.asList(
      def(UnitType.CUBIC_KILOMETER, "1000000000", UnitType.CUBIC_METER),
      def(UnitType.CUBIC_METER, "1000000000", UnitType.CUBIC_CENTIMETER),
      def(UnitType.CUBIC_CENTIMETER, "0.06102374409473", UnitType.CUBIC_INCH),

      // hand-maintained prefix chain (CLDR volume units connect to the
      // cubic-meter hub from the generated table)
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
      def(UnitType.CUBIC_FOOT, "28.316846592", UnitType.LITER),
      def(UnitType.CUBIC_INCH, "1 / 1728", UnitType.CUBIC_FOOT),

      def(UnitType.ACRE_FOOT, "43560", UnitType.CUBIC_FOOT),

      // To be correct, metric pint conversions would need to be localized.
      // Pinning this at 500mL for now.
      def(UnitType.PINT_METRIC, "500", UnitType.MILLILITER),

      def(UnitType.TABLESPOON, "1 / 2", UnitType.FLUID_OUNCE),
      def(UnitType.TEASPOON, "1 / 6", UnitType.FLUID_OUNCE));

  /**
   * Units whose generated (US) values must not leak into the UK variant.
   */
  private static final Set<String> US_VOLUME_UNITS = new HashSet<>(Arrays.asList(
      "gallon", "bushel", "cup", "dram", "dessert-spoon", "drop", "fluid-ounce",
      "jigger", "pinch", "pint", "quart", "barrel", "tablespoon", "teaspoon"));

  private static final List<FactorDef> VOLUME_UK_GENERATED = filterVolumeUk(
      FactorsGenerated.CLDR_FACTORS.get("volume"));

  /**
   * Conversions between CLDR volume units. These are US units. Grouped to be overridden below
   * for UK.
   */
  public static final List<FactorDef> VOLUME = concat(
      concat(VOLUME_BASE, FactorsGenerated.CLDR_FACTORS.get("volume")),
      Arrays.asList(
          def(UnitType.GALLON, "3.785411784", UnitType.LITER),
          def(UnitType.GALLON_IMPERIAL, "4.54609", UnitType.LITER),
          def(UnitType.BUSHEL, "2150.42", UnitType.CUBIC_INCH),
          def(UnitType.GALLON, "231", UnitType.CUBIC_INCH),
          def(UnitType.FLUID_OUNCE, "1 / 128", UnitType.GALLON),
          def(UnitType.QUART, "1 / 4", UnitType.GALLON),
          def(UnitType.PINT, "1 / 8", UnitType.GALLON),
          def(UnitType.CUP, "8", UnitType.FLUID_OUNCE)));

  /**
   * Conversions between CLDR volume units in the UK.
   */
  public static final List<FactorDef> VOLUME_UK = concat(
      concat(VOLUME_BASE, VOLUME_UK_GENERATED),
      Arrays.asList(
          def(UnitType.GALLON, "4.54609", UnitType.LITER),
          def(UnitType.GALLON_IMPERIAL, "4.54609", UnitType.LITER),
          def(UnitType.BUSHEL, "8", UnitType.GALLON_IMPERIAL),
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

  private static List<FactorDef> filterVolumeUk(List<FactorDef> edges) {
    List<FactorDef> r = new ArrayList<>();
    for (FactorDef e : edges) {
      if (!US_VOLUME_UNITS.contains(e.src.value())) {
        r.add(e);
      }
    }
    return r;
  }
}
