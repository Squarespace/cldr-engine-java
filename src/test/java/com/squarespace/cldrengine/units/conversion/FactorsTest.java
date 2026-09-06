package com.squarespace.cldrengine.units.conversion;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;

/**
 * Mirror of the post-tag TS unit-converter tests (temperature.test.ts,
 * factors.test.ts, generated-factor spot checks) against the CLDR 48.2.0
 * convertUnits factor tables (commit fa960378).
 */
public class FactorsTest {

  private static UnitConverter temperature() {
    UnitConverter c = new UnitConverter();
    c.add("temperature", new UnitFactors(Factors.TEMPERATURE));
    return c;
  }

  private static List<String> rationals(UnitConversion c) {
    return c.factors.stream().map(r -> r.toString()).collect(Collectors.toList());
  }

  private static List<String> values(List<com.squarespace.cldrengine.api.UnitType> path) {
    return path.stream().map(u -> u.value()).collect(Collectors.toList());
  }

  /**
   * Celsius and fahrenheit connect to kelvin through direct affine edges;
   * fahrenheit -> kelvin is direct (star), celsius -> fahrenheit walks
   * through the kelvin hub. The composed offset is the exact rational 32.
   */
  @Test
  public void testTemperatureFactors() {
    UnitConverter c = temperature();

    // celsius -> kelvin is a direct edge with an offset
    UnitConversion ck = c.get("temperature", "celsius", "kelvin");
    assertEquals(values(ck.path), Arrays.asList("celsius", "kelvin"));
    assertEquals(rationals(ck), Arrays.asList("1 / 1"));
    assertEquals(ck.offset.toDecimal(null).toString(), "273.15");

    // celsius -> fahrenheit = celsius -> kelvin -> fahrenheit: x 9/5 + 32
    UnitConversion cf = c.get("temperature", "celsius", "fahrenheit");
    assertEquals(values(cf.path), Arrays.asList("celsius", "kelvin", "fahrenheit"));
    assertEquals(rationals(cf), Arrays.asList("1 / 1", "9 / 5"));
    assertEquals(cf.offset.compare(new Rational("32 / 1"), null), 0);

    // kelvin -> fahrenheit is the inverse: F = K x 9/5 - 459.67
    UnitConversion kf = c.get("temperature", "kelvin", "fahrenheit");
    assertEquals(rationals(kf), Arrays.asList("9 / 5"));
    assertEquals(kf.offset.toDecimal(null).toString(), "-459.67");
  }

  @Test
  public void testTemperatureConversions() {
    UnitConverter c = temperature();

    // Decimal eq: exact arithmetic (1x factors or terminating decimals).
    assertEquals(c.convert("temperature", new Decimal(0), "celsius", "kelvin", null).compare(new Decimal("273.15")), 0);
    assertEquals(c.convert("temperature", new Decimal(100), "celsius", "kelvin", null).compare(new Decimal("373.15")), 0);
    assertEquals(c.convert("temperature", new Decimal(0), "kelvin", "celsius", null).compare(new Decimal("-273.15")), 0);
    assertEquals(c.convert("temperature", new Decimal(0), "celsius", "fahrenheit", null).compare(new Decimal("32")), 0);
    assertEquals(c.convert("temperature", new Decimal(100), "celsius", "fahrenheit", null).compare(new Decimal("212")), 0);
    assertEquals(c.convert("temperature", new Decimal(-40), "celsius", "fahrenheit", null).compare(new Decimal("-40")), 0);
    assertEquals(c.convert("temperature", new Decimal(300), "kelvin", "fahrenheit", null).compare(new Decimal("80.33")), 0);

    // Degenerate identity conversion with no offset.
    assertNull(c.get("temperature", "celsius", "celsius").offset);

    // Repeating 5/9 factors render only at finite decimal precision;
    // pin these with a tight tolerance (mirrors the TS temperature tests).
    Decimal r = c.convert("temperature", new Decimal(32), "fahrenheit", "kelvin", null);
    assertTrue(r.subtract(new Decimal("273.15")).abs().compare(new Decimal("1e-20")) < 0);
    r = c.convert("temperature", new Decimal(212), "fahrenheit", "kelvin", null);
    assertTrue(r.subtract(new Decimal("373.15")).abs().compare(new Decimal("1e-20")) < 0);
    r = c.convert("temperature", new Decimal(0), "rankine", "kelvin", null);
    assertEquals(r.compare(new Decimal(0)), 0);
    r = c.convert("temperature", new Decimal(0), "kelvin", "rankine", null);
    assertEquals(r.compare(new Decimal(0)), 0);
    r = c.convert("temperature", new Decimal(491.67), "rankine", "kelvin", null);
    assertTrue(r.subtract(new Decimal("273.15")).abs().compare(new Decimal("1e-20")) < 0);
    r = c.convert("temperature", new Decimal(212), "fahrenheit", "celsius", null);
    assertTrue(r.subtract(new Decimal(100)).abs().compare(new Decimal("1e-20")) < 0);
    r = c.convert("temperature", new Decimal(32), "fahrenheit", "celsius", null);
    assertTrue(r.subtract(new Decimal(0)).abs().compare(new Decimal("1e-20")) < 0);
  }

  @Test
  public void testTemperatureRoundTrips() {
    for (String[] pair : new String[][] {
        { "celsius", "fahrenheit" },
        { "celsius", "kelvin" },
        { "fahrenheit", "rankine" } }) {
      for (int n : new int[] { -100, -40, 0, 1, 25, 100 }) {
        UnitConverter c = temperature();
        Decimal v = c.convert("temperature", new Decimal(n), pair[0], pair[1], null);
        Decimal back = c.convert("temperature", v, pair[1], pair[0], null);
        assertTrue(back.subtract(new Decimal(n)).abs().compare(new Decimal("1e-18")) < 0,
            pair[0] + " -> " + pair[1] + " round trip of " + n + " = " + back);
        if (pair[0].equals("celsius") && pair[1].equals("kelvin")) {
          assertEquals(back.compare(new Decimal(n)), 0);
        }
      }
    }
  }

  /**
   * Century is 100 years (CLDR 48.2.0 convertUnits); a century is
   * 3155695200 seconds, not 315569520 (pre-todo-035 10x bug).
   */
  @Test
  public void testDurationCentury() {
    UnitConverter c = new UnitConverter();
    c.add("duration", new UnitFactors(Factors.DURATION));

    assertEquals(c.convert("duration", new Decimal(1), "century", "second", null).toString(), "3155695200");
    assertEquals(c.convert("duration", new Decimal(1), "century", "year", null).toString(), "100");
    assertEquals(c.convert("duration", new Decimal(1), "year", "second", null).toString(), "31556952");

    UnitFactors map = new UnitFactors(Factors.DURATION);
    UnitConversion fac = map.get(UnitType.CENTURY, UnitType.YEAR);
    Rational total = new Rational("1");
    for (Rational r : fac.factors) {
      total = total.multiply(r, null);
    }
    assertEquals(total.compare(new Rational("100 / 1"), null), 0);
  }

  /**
   * Spot-check generated star edges: unit -> hub factors must be exact
   * rationals matching the CLDR convertUnits table.
   */
  @Test
  public void testGeneratedEdges() {
    check(Factors.ANGLE, UnitType.ARC_MINUTE, UnitType.REVOLUTION, "1 / 21600");
    check(Factors.ANGLE, UnitType.ARC_SECOND, UnitType.REVOLUTION, "1 / 1296000");
    check(Factors.ANGLE, UnitType.RADIAN, UnitType.REVOLUTION, "65501488 / 411557987");
    check(Factors.AREA, UnitType.ACRE, UnitType.SQUARE_METER, "4046.8564224 / 1");
    check(Factors.AREA, UnitType.BU_JP, UnitType.SQUARE_METER, "400 / 121");
    check(Factors.ENERGY, UnitType.BRITISH_THERMAL_UNIT_IT, UnitType.JOULE, "1055.05585262 / 1");
    check(Factors.LENGTH, UnitType.FOOT, UnitType.METER, "0.3048 / 1");
    check(Factors.LENGTH, UnitType.PARSEC, UnitType.METER, "12699352539696155673600000 / 411557987");
    check(Factors.MASS, UnitType.POUND, UnitType.KILOGRAM, "0.45359237 / 1");
    check(Factors.SUBSTANCE, UnitType.MOLE, UnitType.ITEM, "602214076000000000000000 / 1");
    check(Factors.PRESSURE, UnitType.ATMOSPHERE, UnitType.PASCAL, "101325 / 1");
    check(Factors.VOLUME, UnitType.BARREL, UnitType.CUBIC_METER, "0.158987294928 / 1");
    check(Factors.VOLUME, UnitType.GALLON, UnitType.CUBIC_METER, "0.003785411784 / 1");
    check(Factors.DURATION, UnitType.MONTH_PERSON, UnitType.YEAR, "1 / 12");
    check(Factors.DURATION, UnitType.WEEK_PERSON, UnitType.SECOND, "604800 / 1");
    check(Factors.DURATION, UnitType.YEAR_PERSON, UnitType.YEAR, "1 / 1");
  }

  private static void check(List<FactorDef> factors, UnitType src, UnitType dst, String expected) {
    UnitConversion c = new UnitFactors(factors).get(src, dst);
    assertNotNull(c, src + " -> " + dst + " must resolve");
    assertEquals(c.factors.get(0).toString(), expected, src + " -> " + dst);
  }

  /**
   * Light port of the TS cldr-coverage idea: every generated edge must
   * reference known units and a parseable rational factor, and only the
   * temperature category carries additive offsets.
   */
  @Test
  public void testGeneratedTableCoverage() {
    for (String category : FactorsGenerated.CLDR_FACTORS.keySet()) {
      for (FactorDef edge : FactorsGenerated.CLDR_FACTORS.get(category)) {
        assertNotNull(UnitType.fromString(edge.src.value()), category + " edge src " + edge.src.value());
        assertNotNull(UnitType.fromString(edge.dst.value()), category + " edge dst " + edge.dst.value());
        new Rational(edge.factor); // must parse
        if (!category.equals("temperature")) {
          assertNull(edge.offset, category + " " + edge.src + " must have no offset");
        }
      }
    }
  }

  /**
   * The VOLUME_UK variant must not contain the US-only generated star
   * edges (gallon, bushel, cup, dram, dessert-spoon, drop, fluid-ounce,
   * jigger, pinch, pint, quart, barrel, tablespoon, teaspoon). US-only
   * units with no hand-maintained edges disappear from the UK graph.
   */
  @Test
  public void testVolumeUkFiltersGeneratedUsUnits() {
    UnitConverter us = new UnitConverter();
    us.add("volume", new UnitFactors(Factors.VOLUME));
    UnitConverter uk = new UnitConverter();
    uk.add("volume-uk", new UnitFactors(Factors.VOLUME_UK));

    // US: the generated star edge jigger -> cubic-meter exists.
    UnitFactors usf = new UnitFactors(Factors.VOLUME);
    UnitConversion jigger = usf.get(UnitType.CUBIC_METER, UnitType.JIGGER);
    assertNotNull(jigger);
    assertEquals(jigger.factors.get(0).toString(), "1 / 0.00004436029434375");

    // UK: jigger and barrel have no edges at all and are unreachable.
    UnitFactors ukf = new UnitFactors(Factors.VOLUME_UK);
    assertNull(ukf.get(UnitType.CUBIC_METER, UnitType.JIGGER));
    assertNull(ukf.get(UnitType.CUBIC_METER, UnitType.DROP));
    assertNull(uk.get("volume-uk", "gallon", "barrel"));

    // Non-US generated units keep their star edges in the UK variant.
    UnitConversion koku = ukf.get(UnitType.KOKU, UnitType.CUBIC_METER);
    assertNotNull(koku);
    assertEquals(koku.factors.get(0).toString(), "2401 / 13310");

    // The US gallon star edge (0.003785411784) is gone from UK, but the
    // path through the liter hand chain resolves if needed.
    assertEquals(uk.convert("volume-uk", new Decimal(1), "gallon", "cubic-meter", null)
        .compare(new Decimal("0.00454609")), 0);
  }
}
