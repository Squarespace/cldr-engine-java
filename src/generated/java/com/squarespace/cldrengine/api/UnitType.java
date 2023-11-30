package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum UnitType implements StringEnum<UnitType> {

  G_FORCE("g-force"),
  METER_PER_SQUARE_SECOND("meter-per-square-second"),
  ARC_MINUTE("arc-minute"),
  ARC_SECOND("arc-second"),
  DEGREE("degree"),
  RADIAN("radian"),
  REVOLUTION("revolution"),
  ACRE("acre"),
  BU_JP("bu-jp"),
  CHO("cho"),
  DUNAM("dunam"),
  HECTARE("hectare"),
  SE_JP("se-jp"),
  SQUARE_CENTIMETER("square-centimeter"),
  SQUARE_FOOT("square-foot"),
  SQUARE_INCH("square-inch"),
  SQUARE_KILOMETER("square-kilometer"),
  SQUARE_METER("square-meter"),
  SQUARE_MILE("square-mile"),
  SQUARE_YARD("square-yard"),
  ITEM("item"),
  KARAT("karat"),
  MILLIGRAM_OFGLUCOSE_PER_DECILITER("milligram-ofglucose-per-deciliter"),
  MILLIMOLE_PER_LITER("millimole-per-liter"),
  MOLE("mole"),
  PERCENT("percent"),
  PERMILLE("permille"),
  PERMILLION("permillion"),
  PERMYRIAD("permyriad"),
  LITER_PER_100_KILOMETER("liter-per-100-kilometer"),
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
  DAY_PERSON("day-person"),
  DECADE("decade"),
  HOUR("hour"),
  MICROSECOND("microsecond"),
  MILLISECOND("millisecond"),
  MINUTE("minute"),
  MONTH("month"),
  NANOSECOND("nanosecond"),
  QUARTER("quarter"),
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
  KILOWATT_HOUR_PER_100_KILOMETER("kilowatt-hour-per-100-kilometer"),
  NEWTON("newton"),
  POUND_FORCE("pound-force"),
  GIGAHERTZ("gigahertz"),
  HERTZ("hertz"),
  KILOHERTZ("kilohertz"),
  MEGAHERTZ("megahertz"),
  DOT("dot"),
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
  EARTH_RADIUS("earth-radius"),
  FATHOM("fathom"),
  FOOT("foot"),
  FURLONG("furlong"),
  INCH("inch"),
  JO_JP("jo-jp"),
  KEN("ken"),
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
  RI_JP("ri-jp"),
  RIN("rin"),
  SHAKU_CLOTH("shaku-cloth"),
  SHAKU_LENGTH("shaku-length"),
  SOLAR_RADIUS("solar-radius"),
  SUN("sun"),
  YARD("yard"),
  CANDELA("candela"),
  LUMEN("lumen"),
  LUX("lux"),
  SOLAR_LUMINOSITY("solar-luminosity"),
  CARAT("carat"),
  DALTON("dalton"),
  EARTH_MASS("earth-mass"),
  FUN("fun"),
  GRAIN("grain"),
  GRAM("gram"),
  KILOGRAM("kilogram"),
  MICROGRAM("microgram"),
  MILLIGRAM("milligram"),
  OUNCE("ounce"),
  OUNCE_TROY("ounce-troy"),
  POUND("pound"),
  SOLAR_MASS("solar-mass"),
  STONE("stone"),
  TON("ton"),
  TONNE("tonne"),
  GIGAWATT("gigawatt"),
  HORSEPOWER("horsepower"),
  KILOWATT("kilowatt"),
  MEGAWATT("megawatt"),
  MILLIWATT("milliwatt"),
  WATT("watt"),
  ATMOSPHERE("atmosphere"),
  BAR("bar"),
  GASOLINE_ENERGY_DENSITY("gasoline-energy-density"),
  HECTOPASCAL("hectopascal"),
  INCH_OFHG("inch-ofhg"),
  KILOPASCAL("kilopascal"),
  MEGAPASCAL("megapascal"),
  MILLIBAR("millibar"),
  MILLIMETER_OFHG("millimeter-ofhg"),
  PASCAL("pascal"),
  POUND_FORCE_PER_SQUARE_INCH("pound-force-per-square-inch"),
  BEAUFORT("beaufort"),
  KILOMETER_PER_HOUR("kilometer-per-hour"),
  KNOT("knot"),
  METER_PER_SECOND("meter-per-second"),
  MILE_PER_HOUR("mile-per-hour"),
  CELSIUS("celsius"),
  FAHRENHEIT("fahrenheit"),
  TEMPERATURE("temperature"),
  KELVIN("kelvin"),
  NEWTON_METER("newton-meter"),
  POUND_FORCE_FOOT("pound-force-foot"),
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
  CUP_JP("cup-jp"),
  CUP_METRIC("cup-metric"),
  DECILITER("deciliter"),
  DESSERT_SPOON("dessert-spoon"),
  DESSERT_SPOON_IMPERIAL("dessert-spoon-imperial"),
  DRAM("dram"),
  DROP("drop"),
  FLUID_OUNCE("fluid-ounce"),
  FLUID_OUNCE_IMPERIAL("fluid-ounce-imperial"),
  GALLON("gallon"),
  GALLON_IMPERIAL("gallon-imperial"),
  HECTOLITER("hectoliter"),
  JIGGER("jigger"),
  KOKU("koku"),
  KOSAJI("kosaji"),
  LITER("liter"),
  MEGALITER("megaliter"),
  MILLILITER("milliliter"),
  OSAJI("osaji"),
  PINCH("pinch"),
  PINT("pint"),
  PINT_METRIC("pint-metric"),
  QUART("quart"),
  QUART_IMPERIAL("quart-imperial"),
  SAI("sai"),
  SHAKU("shaku"),
  TABLESPOON("tablespoon"),
  TEASPOON("teaspoon"),
  TO_JP("to-jp")
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
