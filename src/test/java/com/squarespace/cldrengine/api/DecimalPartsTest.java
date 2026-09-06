package com.squarespace.cldrengine.api;

import static com.squarespace.cldrengine.api.Part.part;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

/**
 * Tests for Decimal.toInteger / toScientificString / toParts / toScientificParts.
 *
 * Mirrors the corresponding test cases in the TS oracle:
 *   - packages/decimal/__tests__/decimal.test.ts  ('to integer', 'to parts')
 *   - packages/decimal/__tests__/scientific.test.ts
 *   - packages/decimal/__tests__/flags.test.ts    (formatting/scientific variants)
 */
public class DecimalPartsTest {

  private final static CLDR EN = CLDR.get("en");

  private static Decimal d(String s) {
    return new Decimal(s);
  }

  private static String sci(String s) {
    return d(s).toScientificString();
  }

  private static String sci(String s, int m) {
    return d(s).toScientificString(m);
  }

  private static List<Part> scip(String s) {
    return d(s).toScientificParts();
  }

  private static List<Part> scip(String s, int m) {
    return d(s).toScientificParts(m);
  }

  @Test
  public void testToInteger() {
    assertEquals(d("0.000").toInteger(), d("0"));
    assertEquals(d("0.999").toInteger(), d("0"));
    assertEquals(d("-0.999").toInteger(), d("-0"));
    assertEquals(d("2.79").toInteger(), d("2"));
    assertEquals(d("12345.6789").toInteger(), d("12345"));
    assertEquals(d("-2.79").toInteger(), d("-2"));
    assertEquals(d("-12345.6789").toInteger(), d("-12345"));
  }

  @Test
  public void testToIntegerFlags() {
    assertEquals(d("nan").toInteger(), d("nan"));
    assertEquals(d("infinity").toInteger(), d("infinity"));
    assertEquals(d("-infinity").toInteger(), d("-infinity"));
  }

  @Test
  public void testToScientificString() {
    assertEquals(sci("0"), "0");
    assertEquals(sci("0.0"), "0");
    assertEquals(sci("1.0"), "1.0");
    assertEquals(sci("1.01"), "1.01");
    assertEquals(sci("10.01"), "1.001E+1");
    assertEquals(sci("0.000123"), "1.23E-4");
    assertEquals(sci("12345678"), "1.2345678E+7");
    assertEquals(sci("1234.5678"), "1.2345678E+3");
    assertEquals(sci("1.2e6"), "1.2E+6");
    assertEquals(sci("1e21"), "1E+21");
    assertEquals(sci("1.23e-7"), "1.23E-7");
    assertEquals(sci("-0"), "-0");
  }

  @Test
  public void testToScientificStringMinIntDigits() {
    assertEquals(sci("0", 2), "00");
    assertEquals(sci("1", 2), "10E-1");
    assertEquals(sci("123", 2), "12.3E+1");
    assertEquals(sci("12345.678", 2), "12.345678E+3");
    assertEquals(sci("0.5", 3), "500E-3");
    assertEquals(sci("-0.999", 5), "-99900E-5");
  }

  @Test
  public void testToScientificStringFlags() {
    assertEquals(d("nan").toScientificString(), "NaN");
    assertEquals(d("infinity").toScientificString(), "Infinity");
    assertEquals(d("-infinity").toScientificString(), "-Infinity");
  }

  @Test
  public void testToParts() {
    assertEquals(d("0").toParts(), Arrays.asList(part("integer", "0")));
    assertEquals(d("-0").toParts(), Arrays.asList(part("minus", "-"), part("integer", "0")));
    assertEquals(d("1234.5").toParts(), Arrays.asList(
        part("integer", "1234"), part("decimal", "."), part("fraction", "5")));
    assertEquals(d("1e21").toParts(), Arrays.asList(part("integer", "1000000000000000000000")));
    assertEquals(d("1.23e-7").toParts(), Arrays.asList(
        part("integer", "0"), part("decimal", "."), part("fraction", "000000123")));
    assertEquals(d("-15.1234e2").toParts(), Arrays.asList(
        part("minus", "-"), part("integer", "1512"), part("decimal", "."), part("fraction", "34")));
    assertEquals(d("1.3e-5").toParts(), Arrays.asList(
        part("integer", "0"), part("decimal", "."), part("fraction", "000013")));
  }

  @Test
  public void testToPartsFlags() {
    assertEquals(d("nan").toParts(), Arrays.asList(part("nan", "NaN")));
    assertEquals(d("infinity").toParts(), Arrays.asList(part("infinity", "Infinity")));
    assertEquals(d("-infinity").toParts(), Arrays.asList(part("infinity", "-Infinity")));
  }

  @Test
  public void testToScientificParts() {
    assertEquals(scip("0"), Arrays.asList(part("integer", "0")));
    assertEquals(scip("-0"), Arrays.asList(part("minus", "-"), part("integer", "0")));
    assertEquals(scip("0.0012"), Arrays.asList(
        part("integer", "1"), part("decimal", "."), part("fraction", "2"),
        part("exp", "E"), part("minus", "-"), part("integer", "3")));
    assertEquals(scip("1.23e-7"), Arrays.asList(
        part("integer", "1"), part("decimal", "."), part("fraction", "23"),
        part("exp", "E"), part("minus", "-"), part("integer", "7")));
    assertEquals(scip("1e21"), Arrays.asList(
        part("integer", "1"), part("exp", "E"), part("plus", "+"), part("integer", "21")));
    assertEquals(scip("5", 3), Arrays.asList(
        part("integer", "500"), part("exp", "E"), part("minus", "-"), part("integer", "2")));
    assertEquals(scip("123", 2), Arrays.asList(
        part("integer", "12"), part("decimal", "."), part("fraction", "3"),
        part("exp", "E"), part("plus", "+"), part("integer", "1")));
    assertEquals(scip("-123.57", 2), Arrays.asList(
        part("minus", "-"), part("integer", "12"), part("decimal", "."), part("fraction", "357"),
        part("exp", "E"), part("plus", "+"), part("integer", "1")));
  }

  @Test
  public void testToScientificPartsFlags() {
    assertEquals(d("nan").toScientificParts(), Arrays.asList(part("nan", "NaN")));
    assertEquals(d("infinity").toScientificParts(), Arrays.asList(part("infinity", "Infinity")));
    assertEquals(d("-infinity").toScientificParts(), Arrays.asList(part("infinity", "-Infinity")));
  }

  @Test
  public void testToPartsMatchesFormatDecimalToParts() {
    // Cross-check consistency with the numbers formatter path: Decimal.toParts
    // (fixed point, no grouping) must agree with Numbers.formatDecimalToParts for
    // the same value when grouping is disabled. The default number-formatter
    // options group by locale ("1,234.5"), while toParts never groups.
    Decimal n = d("1234.5");
    List<Part> expected = n.toParts();
    List<Part> actual = EN.Numbers.formatDecimalToParts(n,
        DecimalFormatOptions.build().group(false));
    assertEquals(actual, expected);
  }
}
