package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.decimal.DecimalConstants;
import com.squarespace.cldrengine.decimal.RationalConstants;

/**
 * Port of the post-1.15.0 upstream TS fixes, mirroring these test sources:
 *
 *   - packages/decimal/__tests__/compare.test.ts   (ea2587c6 Fixed
 *     Decimal.compare for zero vs same-sign values)
 *   - packages/decimal/__tests__/decimal.test.ts    (94ac984b Fixed isInteger
 *     returning false for scaled zero)
 *   - packages/decimal/__tests__/parse.test.ts      (39005f13 Fixed Decimal
 *     parser rejecting valid 7-digit exponents)
 *   - packages/decimal/__tests__/flags.test.ts      (cf8f321c Fixed Decimal
 *     division by infinity to preserve sign)
 *   - packages/decimal/__tests__/rational.test.ts   (c73b52b8 Add add,
 *     subtract, negate to Rational)
 */
public class DecimalPost1150Test {

  private static final Decimal INF_POS = new Decimal("infinity");
  private static final Decimal INF_NEG = new Decimal("-infinity");
  private static final Decimal POS = new Decimal("10");
  private static final Decimal NEG = new Decimal("-10");
  private static final Decimal ZERO = new Decimal("0");
  private static final Decimal NEG_ZERO = new Decimal("-0");

  private static Decimal d(String s) {
    return new Decimal(s);
  }

  private static Rational r(String s) {
    return new Rational(s);
  }

  private static int cmp(String a, String b) {
    return d(a).compare(d(b));
  }

  private static int cmp(String a, String b, boolean abs) {
    return d(a).compare(d(b), abs);
  }

  private static void assertProps(String s, long data, int exp, int sign, int flag) {
    Decimal.Properties p = d(s).properties();
    assertEquals(p.data.length, 1);
    assertEquals(p.data[0], data);
    assertEquals(p.exp, exp);
    assertEquals(p.sign, sign);
    assertEquals(p.flag, flag);
  }

  private static void assertThrowsIllegalArgument(String s, String message) {
    try {
      d(s);
      throw new AssertionError("Expected IllegalArgumentException for: " + s);
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), message);
    }
  }

  @Test
  public void testCompareZeroVsSameSign() {
    // Ported from compare.test.ts 'compare': zero vs a same-sign non-zero
    // value with a different exponent.
    assertEquals(cmp("0", "1e-18"), -1);
    assertEquals(cmp("1e-18", "0"), 1);
    assertEquals(cmp("-1e-18", "0"), -1);
    assertEquals(cmp("0", "-1e-18"), 1);
    assertEquals(cmp("1e18", "0"), 1);
    assertEquals(cmp("0", "1e18"), -1);
    assertEquals(cmp("0", "1e-18", true), -1);
    assertEquals(cmp("1e-18", "0", true), 1);
    assertEquals(cmp("-0", "1e-18"), -1);
    assertEquals(cmp("1e-18", "-0"), 1);
    // Acceptance repros.
    assertEquals(cmp("0", "0.5"), -1);
    assertEquals(cmp("0", "1e-18"), -1);
    assertEquals(cmp("1e-18", "0"), 1);
  }

  @Test
  public void testIsIntegerScaledZero() {
    // Ported from decimal.test.ts 'is integer'.
    assertEquals(d("0").isInteger(), true);
    assertEquals(d("0.00").isInteger(), true);
    assertEquals(d("0.000").isInteger(), true);
    assertEquals(d("-0.00").isInteger(), true);
    assertEquals(d("0e5").isInteger(), true);
    assertEquals(d("0e-10").isInteger(), true);
  }

  @Test
  public void testParseSevenDigitExponents() {
    // Ported from parse.test.ts 'parse': exponents up to
    // Constants.RADIX - 1 (7 digits) are valid.
    assertProps("1e999999", 1, 999999, 1, 0);
    assertProps("1e1000000", 1, 1000000, 1, 0);
    assertProps("1e1234567", 1, 1234567, 1, 0);
    assertProps("1e9999999", 1, 9999999, 1, 0);
    assertProps("-1e-9999999", 1, -9999999, -1, 0);
    assertProps("1.23e1000000", 123, 999998, 1, 0);
    assertProps("1.23e-9999999", 123, -10000001, 1, 0);
    assertEquals(d("1e9999999").toScientificString(), "1E+9999999");
    assertEquals(d("1.23e1000000").toScientificString(), "1.23E+1000000");
  }

  @Test
  public void testParseRejectOverlongExponent() {
    // Ported from parse.test.ts 'parse invalid': exponents of
    // Constants.RADIX (1e7) or greater are rejected.
    assertThrowsIllegalArgument("1e10000000", "Exponent too large");
    assertThrowsIllegalArgument("1e-10000000", "Exponent too large");
    assertThrowsIllegalArgument("1.23e99999999", "Exponent too large");
  }

  @Test
  public void testDivideByInfinitySignedZero() {
    // Ported from flags.test.ts 'division'.
    assertEquals(ZERO.divide(INF_POS, null), ZERO);
    assertEquals(ZERO.divide(INF_NEG, null), NEG_ZERO);
    assertEquals(POS.divide(INF_POS, null), ZERO);
    assertEquals(POS.divide(INF_NEG, null), NEG_ZERO);
    assertEquals(NEG.divide(INF_POS, null), NEG_ZERO);
    assertEquals(NEG.divide(INF_NEG, null), ZERO);
  }

  @Test
  public void testDivideByInfinityFreshSignedZero() {
    // Ported from flags.test.ts 'division by infinity yields signed zero and
    // does not alias the shared ZERO'.
    assertEquals(POS.divide(INF_POS, null).isNegative(), false);
    assertEquals(POS.divide(INF_NEG, null).isNegative(), true);
    assertEquals(NEG.divide(INF_POS, null).isNegative(), true);
    assertEquals(NEG.divide(INF_NEG, null).isNegative(), false);
    assertEquals(ZERO.divide(INF_POS, null).isNegative(), false);
    assertEquals(ZERO.divide(INF_NEG, null).isNegative(), true);

    Decimal r = NEG.divide(INF_POS, null);
    assertEquals(r.isZero(), true);
    assertEquals(r.signum(), 0);
    assertEquals(r.compare(ZERO), 0);

    // A fresh instance is returned, not the shared module-level ZERO constant.
    assertNotSame(POS.divide(INF_POS, null), DecimalConstants.ZERO);
    assertNotSame(NEG.divide(INF_POS, null), DecimalConstants.ZERO);
    assertNotSame(ZERO.divide(INF_NEG, null), DecimalConstants.ZERO);
  }

  @Test
  public void testRationalAdd() {
    // Ported from rational.test.ts 'add'.
    assertEquals(r("1 / 5").add(r("1 / 4"), null).toString(), "9 / 20");
    assertEquals(r("1 / 5").add(r("1"), null).toString(), "6 / 5");
    assertEquals(r("273.15").multiply(r("9 / 5"), null).add(r("-459.67"), null)
        .compare(r("32 / 1"), null), 0);
    assertEquals(r("1 / 3").add(r("1 / 3"), null).compare(r("2 / 3"), null), 0);
  }

  @Test
  public void testRationalSubtract() {
    // Ported from rational.test.ts 'subtract'.
    assertEquals(r("1 / 5").subtract(r("1 / 4"), null).toString(), "-1 / 20");
    assertEquals(r("3 / 5").subtract(r("1"), null).toString(), "-2 / 5");
  }

  @Test
  public void testRationalNegate() {
    // Ported from rational.test.ts 'negate'.
    assertEquals(r("3 / 5").negate().toString(), "-3 / 5");
    assertEquals(r("-3 / 5").negate().toString(), "3 / 5");
  }

  @Test
  public void testRationalZeroConstant() {
    // RationalConstants.ZERO from c73b52b8.
    assertEquals(RationalConstants.ZERO.toString(), "0 / 1");
    assertEquals(RationalConstants.ZERO.numerator(), new Decimal("0"));
    assertEquals(RationalConstants.ZERO.denominator(), new Decimal("1"));
  }
}
