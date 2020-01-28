package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum UnitType implements StringEnum<UnitType> {

  G_FORCE("g-force"),
  METER_PER_SECOND_SQUARED("meter-per-second-squared"),
  ARC_MINUTE("arc-minute"),
  ARC_SECOND("arc-second"),
  DEGREE("degree"),
  RADIAN("radian"),
  REVOLUTION("revolution"),
  ACRE("acre"),
  DUNAM("dunam"),
  HECTARE("hectare"),
  SQUARE_CENTIMETER("square-centimeter"),
  SQUARE_FOOT("square-foot"),
  SQUARE_INCH("square-inch"),
  SQUARE_KILOMETER("square-kilometer"),
  SQUARE_METER("square-meter"),
  SQUARE_MILE("square-mile"),
  SQUARE_YARD("square-yard"),
  KARAT("karat"),
  MILLIGRAM_PER_DECILITER("milligram-per-deciliter"),
  MILLIMOLE_PER_LITER("millimole-per-liter"),
  MOLE("mole"),
  PART_PER_MILLION("part-per-million"),
  PERCENT("percent"),
  PERMILLE("permille"),
  PERMYRIAD("permyriad"),
  LITER_PER_100KILOMETERS("liter-per-100kilometers"),
  LITER_PER_KILOMETER("liter-per-kilometer"),
  MILE_PER_GALLON("mile-per-gallon"),
  MILE_PER_GALLON_IMPERIAL("mile-per-gallon-imperial"),
  BIT("bit"),
  BYTE("byte"),
  GIGABIT("gigabit"),
  GIGABYTE("gigabyte"),
  KILOBIT("kilobit"),
  KILOBYTE("kilobyte"),
  MEGABIT("megabit"),
  MEGABYTE("megabyte"),
  PETABYTE("petabyte"),
  TERABIT("terabit"),
  TERABYTE("terabyte"),
  CENTURY("century"),
  DAY("day"),
  DECADE("decade"),
  HOUR("hour"),
  MICROSECOND("microsecond"),
  MILLISECOND("millisecond"),
  MINUTE("minute"),
  MONTH("month"),
  NANOSECOND("nanosecond"),
  SECOND("second"),
  WEEK("week"),
  YEAR("year"),
  AMPERE("ampere"),
  MILLIAMPERE("milliampere"),
  OHM("ohm"),
  VOLT("volt"),
  BRITISH_THERMAL_UNIT("british-thermal-unit"),
  CALORIE("calorie"),
  ELECTRONVOLT("electronvolt"),
  FOODCALORIE("foodcalorie"),
  JOULE("joule"),
  KILOCALORIE("kilocalorie"),
  KILOJOULE("kilojoule"),
  KILOWATT_HOUR("kilowatt-hour"),
  THERM_US("therm-us"),
  NEWTON("newton"),
  POUND_FORCE("pound-force"),
  GIGAHERTZ("gigahertz"),
  HERTZ("hertz"),
  KILOHERTZ("kilohertz"),
  MEGAHERTZ("megahertz"),
  DOT_PER_CENTIMETER("dot-per-centimeter"),
  DOT_PER_INCH("dot-per-inch"),
  EM("em"),
  MEGAPIXEL("megapixel"),
  PIXEL("pixel"),
  PIXEL_PER_CENTIMETER("pixel-per-centimeter"),
  PIXEL_PER_INCH("pixel-per-inch"),
  ASTRONOMICAL_UNIT("astronomical-unit"),
  CENTIMETER("centimeter"),
  DECIMETER("decimeter"),
  FATHOM("fathom"),
  FOOT("foot"),
  FURLONG("furlong"),
  INCH("inch"),
  KILOMETER("kilometer"),
  LIGHT_YEAR("light-year"),
  METER("meter"),
  MICROMETER("micrometer"),
  MILE("mile"),
  MILE_SCANDINAVIAN("mile-scandinavian"),
  MILLIMETER("millimeter"),
  NANOMETER("nanometer"),
  NAUTICAL_MILE("nautical-mile"),
  PARSEC("parsec"),
  PICOMETER("picometer"),
  POINT("point"),
  SOLAR_RADIUS("solar-radius"),
  YARD("yard"),
  LUX("lux"),
  SOLAR_LUMINOSITY("solar-luminosity"),
  CARAT("carat"),
  DALTON("dalton"),
  EARTH_MASS("earth-mass"),
  GRAM("gram"),
  KILOGRAM("kilogram"),
  METRIC_TON("metric-ton"),
  MICROGRAM("microgram"),
  MILLIGRAM("milligram"),
  OUNCE("ounce"),
  OUNCE_TROY("ounce-troy"),
  POUND("pound"),
  SOLAR_MASS("solar-mass"),
  STONE("stone"),
  TON("ton"),
  GIGAWATT("gigawatt"),
  HORSEPOWER("horsepower"),
  KILOWATT("kilowatt"),
  MEGAWATT("megawatt"),
  MILLIWATT("milliwatt"),
  WATT("watt"),
  ATMOSPHERE("atmosphere"),
  BAR("bar"),
  HECTOPASCAL("hectopascal"),
  INCH_HG("inch-hg"),
  KILOPASCAL("kilopascal"),
  MEGAPASCAL("megapascal"),
  MILLIBAR("millibar"),
  MILLIMETER_OF_MERCURY("millimeter-of-mercury"),
  PASCAL("pascal"),
  POUND_PER_SQUARE_INCH("pound-per-square-inch"),
  KILOMETER_PER_HOUR("kilometer-per-hour"),
  KNOT("knot"),
  METER_PER_SECOND("meter-per-second"),
  MILE_PER_HOUR("mile-per-hour"),
  CELSIUS("celsius"),
  FAHRENHEIT("fahrenheit"),
  GENERIC("generic"),
  KELVIN("kelvin"),
  TIMES("times"),
  NEWTON_METER("newton-meter"),
  POUND_FOOT("pound-foot"),
  ACRE_FOOT("acre-foot"),
  BARREL("barrel"),
  BUSHEL("bushel"),
  CENTILITER("centiliter"),
  CUBIC_CENTIMETER("cubic-centimeter"),
  CUBIC_FOOT("cubic-foot"),
  CUBIC_INCH("cubic-inch"),
  CUBIC_KILOMETER("cubic-kilometer"),
  CUBIC_METER("cubic-meter"),
  CUBIC_MILE("cubic-mile"),
  CUBIC_YARD("cubic-yard"),
  CUP("cup"),
  CUP_METRIC("cup-metric"),
  DECILITER("deciliter"),
  FLUID_OUNCE("fluid-ounce"),
  FLUID_OUNCE_IMPERIAL("fluid-ounce-imperial"),
  GALLON("gallon"),
  GALLON_IMPERIAL("gallon-imperial"),
  HECTOLITER("hectoliter"),
  LITER("liter"),
  MEGALITER("megaliter"),
  MILLILITER("milliliter"),
  PINT("pint"),
  PINT_METRIC("pint-metric"),
  QUART("quart"),
  TABLESPOON("tablespoon"),
  TEASPOON("teaspoon")
  ;

  private static final Map<String, UnitType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(UnitType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private UnitType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static UnitType fromString(String s) {
    return REVERSE.get(s);
  }
}
