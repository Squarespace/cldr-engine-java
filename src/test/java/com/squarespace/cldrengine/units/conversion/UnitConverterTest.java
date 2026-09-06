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

    // Path and factors for revolution -> arc-minute.
    UnitConversion f = c.get("angle", "revolution", "arc-minute");
    assertEquals(values(f.path), Arrays.asList("revolution", "degree", "arc-minute"));
    assertEquals(f.factors.stream().map(r -> r.toString()).collect(Collectors.toList()),
        Arrays.asList("360 / 1", "60 / 1"));

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

  private static List<String> values(List<com.squarespace.cldrengine.api.UnitType> path) {
    return path.stream().map(u -> u.value()).collect(Collectors.toList());
  }
}
