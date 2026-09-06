package com.squarespace.cldrengine.units.conversion;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;

/**
 * Oracle-derived mirror of packages/unit-converter/__tests__/converter.test.ts
 * (v1.15.0), plus conversion cases required by the Java unit-convert suite.
 */
public class UnitConverterTest {

  private static UnitConverter converter() {
    UnitConverter c = new UnitConverter();
    c.add("angle", new UnitFactors(Factors.ANGLE));
    c.add("acceleration", new UnitFactors(Factors.ACCELERATION));
    c.add("length", new UnitFactors(Factors.LENGTH));
    c.add("duration", new UnitFactors(Factors.DURATION));
    c.add("digital", new UnitFactors(Factors.DIGITAL));
    c.add("digital-decimal", new UnitFactors(Factors.DIGITAL_DECIMAL));
    return c;
  }

  @Test
  public void testConverter() {
    UnitConverter c = new UnitConverter();

    UnitFactors angle = new UnitFactors(Factors.ANGLE);
    c.add("angle", angle);

    assertTrue(c.has("angle"));
    assertTrue(!c.has("acceleration"));

    // Duplicate registration throws.
    try {
      c.add("angle", angle);
      fail("expected duplicate registration to throw");
    } catch (RuntimeException e) {
      // expected
    }

    c.add("acceleration", new UnitFactors(Factors.ACCELERATION));
    assertTrue(c.has("acceleration"));

    assertEquals(c.factors("angle"), angle);

    c.add("length", new UnitFactors(Factors.LENGTH));

    // Categories are sorted.
    assertEquals(c.categories(), Arrays.asList("acceleration", "angle", "length"));

    // Path and factors for revolution -> arc-minute. Mirror of the post-tag
    // TS converter.test.ts: the CLDR star table adds a direct inverse edge
    // (1 revolution = 21600 arc-minutes).
    UnitConversion f = c.get("angle", "revolution", "arc-minute");
    assertEquals(values(f.path), Arrays.asList("revolution", "arc-minute"));
    assertEquals(f.factors.stream().map(r -> r.toString()).collect(Collectors.toList()),
        Arrays.asList("21600 / 1"));

    // Unknown unit within a known category.
    assertNull(c.get("angle", "unknown", "arc-minute"));

    // Unknown category.
    assertNull(c.get("unknown", "arc-second", "arc-minute"));

    // Convert via the factor path.
    assertEquals(c.convert("angle", new Decimal(3), "revolution", "arc-minute", null).toString(), "64800");

    // Unknown unit -> null.
    assertNull(c.convert("angle", new Decimal(3), "revolution", "unknown", null));
  }

  @Test
  public void testLength() {
    UnitConverter c = new UnitConverter();
    c.add("length", new UnitFactors(Factors.LENGTH));

    assertEquals(c.convert("length", new Decimal(1), "mile", "kilometer", null).toString(), "1.609344");
    assertEquals(c.convert("length", new Decimal(1), "kilometer", "mile", null).toString(),
        "0.6213711922373339696174341844");

    // Round-trip.
    Decimal back = c.convert("length",
        c.convert("length", new Decimal(1), "mile", "kilometer", null), "kilometer", "mile", null);
    assertEquals(back.toString(), "1.000000000000000000000000000");
  }

  @Test
  public void testDuration() {
    UnitConverter c = new UnitConverter();
    c.add("duration", new UnitFactors(Factors.DURATION));
    assertEquals(c.convert("duration", new Decimal(90), "minute", "hour", null).toString(),
        "1.500000000000000000000000000");
  }

  @Test
  public void testDigital() {
    UnitConverter c = new UnitConverter();
    c.add("digital", new UnitFactors(Factors.DIGITAL));
    c.add("digital-decimal", new UnitFactors(Factors.DIGITAL_DECIMAL));

    // Binary: 1 kilobyte = 1024 bytes.
    assertEquals(c.convert("digital", new Decimal(1), "kilobyte", "byte", null).toString(), "1024");

    // Decimal: 1 kilobyte = 1000 bytes.
    assertEquals(c.convert("digital-decimal", new Decimal(1), "kilobyte", "byte", null).toString(), "1000");
  }

  @Test
  public void testUnknown() {
    UnitConverter c = new UnitConverter();
    c.add("length", new UnitFactors(Factors.LENGTH));

    assertNull(c.convert("length", new Decimal(1), "unknown", "mile", null));
    assertNull(c.convert("unknown", new Decimal(1), "mile", "kilometer", null));
    assertNull(c.get("length", "unknown", "mile"));
    assertNull(c.get("unknown", "mile", "kilometer"));
  }

  /**
   * Hand-rolled affine (temperature-style) factors. The celsius/kelvin
   * relationship is real; the fahrenheit->celsius edge below is NOT
   * physically valid (it is only a mechanism test, see
   * {@link #testAffinePathComposition}).
   */
  private static UnitConverter temperature() {
    UnitConverter c = new UnitConverter();
    c.add("temperature", new UnitFactors(Arrays.asList(
        new FactorDef(UnitType.CELSIUS, "1", UnitType.KELVIN, "273.15"),
        new FactorDef(UnitType.FAHRENHEIT, "5/9", UnitType.CELSIUS, "-1600/9"))));
    return c;
  }

  /**
   * A direct affine edge is `dst = src * factor + offset`; the implicit
   * inverse edge `src = dst * (1/factor) - offset/factor` is generated by
   * {@link UnitFactors#init()}.
   */
  @Test
  public void testAffineDirectEdges() {
    UnitConverter c = temperature();

    // celsius -> kelvin: K = C * 1 + 273.15
    UnitConversion k = c.get("temperature", "celsius", "kelvin");
    assertEquals(values(k.path), Arrays.asList("celsius", "kelvin"));
    assertEquals(rationals(k), Arrays.asList("1 / 1"));
    assertEquals(k.offset.toString(), "273.15 / 1");

    assertEquals(c.convert("temperature", new Decimal(100), "celsius", "kelvin", null).toString(), "373.15");
    assertEquals(c.convert("temperature", new Decimal(0), "celsius", "kelvin", null).toString(), "273.15");

    // kelvin -> celsius inverse edge: C = K * 1 - 273.15
    UnitConversion ks = c.get("temperature", "kelvin", "celsius");
    assertEquals(ks.factors.get(0).toString(), "1 / 1");
    assertEquals(ks.offset.toString(), "-273.15 / 1");
    assertEquals(c.convert("temperature", new Decimal(0), "kelvin", "celsius", null).toString(), "-273.15");

    // fahrenheit -> celsius direct edge
    UnitConversion f = c.get("temperature", "fahrenheit", "celsius");
    assertEquals(rationals(f), Arrays.asList("5 / 9"));
    assertEquals(f.offset.toString(), "-1600 / 9");
  }

  /**
   * Composition of an affine path. With no direct fahrenheit -> kelvin
   * edge, fahrenheit -> kelvin walks fahrenheit -> celsius -> kelvin and
   * composes the offset after each step as `offset = offset * factor + a`
   * (starting from ZERO). Rationals are never reduced, so denominators
   * accumulate exactly as in the TS engine:
   *
   * <pre>
   *   F -> C: r1 = 5/9,   a1 = -1600/9
   *   C -> K: r2 = 1,     a2 = 273.15 = 5463/20
   *
   *   step 1: offset = ZERO * (5/9) + (-1600/9)
   *                   = 0/9 + (-1600/9)
   *                   = (0*9 + -1600*9) / 81 = -14400/81
   *   step 2: offset = (-14400/81) * (1/1) + 273.15
   *                   = (-14400*1 + 273.15*81) / 81 = 7725.15/81
   *
   *   final: K = F * (5/9 * 1) + 7725.15/81        (= 17167/180 reduced)
   *   F = 32: K = 32 * (5/9) + 95.3722... = 160/9 + 95.3722... = 113.15
   * </pre>
   *
   * All arithmetic mirrors the post-tag TS `converter.ts`/`factormap.ts`.
   */
  @Test
  public void testAffinePathComposition() {
    UnitConverter c = temperature();

    UnitConversion conv = c.get("temperature", "fahrenheit", "kelvin");
    assertEquals(values(conv.path), Arrays.asList("fahrenheit", "celsius", "kelvin"));
    assertEquals(rationals(conv), Arrays.asList("5 / 9", "1 / 1"));
    assertEquals(conv.offset.toString(), "7725.15 / 81");

    assertEquals(c.convert("temperature", new Decimal(32), "fahrenheit", "kelvin", null)
        .compare(new Decimal("113.15")), 0);
    // Repeating decimal; compare within a tight tolerance.
    assertEquals(c.convert("temperature", new Decimal(0), "fahrenheit", "kelvin", null)
        .subtract(new Decimal("95.37222222222222222222222222")).abs()
        .compare(new Decimal("1e-20")), -1);
  }

  /**
   * Pure-ratio conversions (no offsets anywhere) must expose a null
   * offset, both for a multi-step path and for a direct edge whose
   * offset entry is explicitly zero.
   */
  @Test
  public void testAffineOffsetAbsent() {
    UnitFactors factors = new UnitFactors(Arrays.asList(
        new FactorDef(UnitType.BIT, "2", UnitType.BYTE),
        new FactorDef(UnitType.BYTE, "4", UnitType.KILOBIT),
        new FactorDef(UnitType.KILOBIT, "10", UnitType.KILOBYTE, "0")));

    UnitConversion conv = factors.get(UnitType.BIT, UnitType.KILOBYTE);
    assertEquals(values(conv.path), Arrays.asList("bit", "byte", "kilobit", "kilobyte"));
    assertEquals(rationals(conv), Arrays.asList("2 / 1", "4 / 1", "10 / 1"));
    assertNull(conv.offset);

    // Direct edge with an explicit zero offset renders as null.
    assertNull(factors.get(UnitType.KILOBIT, UnitType.KILOBYTE).offset);
    assertEquals(factors.get(UnitType.KILOBIT, UnitType.KILOBYTE).factors.get(0).toString(), "10 / 1");

    UnitConverter c = new UnitConverter();
    c.add("synthetic", factors);
    assertEquals(c.convert("synthetic", new Decimal(1), "bit", "kilobyte", null).toString(), "80");
  }

  /**
   * Dijkstra path costs must be non-negative (TS commit f3f8f271). A
   * factor of `0.001` contributes precision 1 + alignexp -3 = -2 in its
   * numerator, so without abs() that edge costs -1 and Dijkstra settles
   * on the cheap-but-wrong path through arc-second (factors 0.001 * 1000
   * = 1) instead of the correct one through degree (10 * 10 = 100):
   *
   * <pre>
   *   arc-minute -> arc-second (0.001)  cost -1   (would be 3 with abs)
   *   arc-second  -> radian (1000)      cost  8
   *   arc-minute -> degree (10)         cost  4
   *   degree      -> radian (10)        cost  4
   * </pre>
   *
   * Without abs(): arc-minute -> arc-second -> radian costs -1 + 8 = 7 and
   * beats arc-minute -> degree -> radian (4 + 4 = 8). With abs() the 0.001
   * edge costs 3, so the arc-second path (11) loses to the degree path (8),
   * giving factors 10 * 10 = 100.
   */
  @Test
  public void testNonNegativePathCost() {
    UnitFactors factors = new UnitFactors(Arrays.asList(
        new FactorDef(UnitType.ARC_MINUTE, "0.001", UnitType.ARC_SECOND),
        new FactorDef(UnitType.ARC_SECOND, "1000", UnitType.RADIAN),
        new FactorDef(UnitType.ARC_MINUTE, "10", UnitType.DEGREE),
        new FactorDef(UnitType.DEGREE, "10", UnitType.RADIAN)));

    UnitConversion conv = factors.get(UnitType.ARC_MINUTE, UnitType.RADIAN);
    assertEquals(values(conv.path), Arrays.asList("arc-minute", "degree", "radian"));
    assertEquals(rationals(conv), Arrays.asList("10 / 1", "10 / 1"));
    assertNull(conv.offset);

    UnitConverter c = new UnitConverter();
    c.add("synthetic-angle", factors);
    assertEquals(c.convert("synthetic-angle", new Decimal(2), "arc-minute", "radian", null).toString(), "200");
  }

  private static List<String> rationals(UnitConversion c) {
    return c.factors.stream().map(r -> r.toString()).collect(Collectors.toList());
  }

  private static List<String> values(List<com.squarespace.cldrengine.api.UnitType> path) {
    return path.stream().map(u -> u.value()).collect(Collectors.toList());
  }
}
