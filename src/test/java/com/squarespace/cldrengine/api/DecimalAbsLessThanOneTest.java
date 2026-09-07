package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.decimal.Constants;
import com.squarespace.cldrengine.decimal.DecimalFlag;

/**
 * Property test for the private, allocation-free {@code isAbsLessThanOne()}
 * predicate added to Decimal.format(). For every Decimal in the corpus we
 * assert that the private predicate (invoked via reflection) produces the
 * exact same result as the reference comparison {@code d.compare(ONE, true)
 * == -1} on an immutable Decimal (compare allocates only internal copies and
 * never mutates its receiver, so the same instance can serve both sides).
 *
 * The corpus intentionally includes the tricky cases where a naive
 * "exp &lt; 0 && precision + exp &lt;= 0" derivation diverges from the
 * reference: zero of any scale/sign, NaN, and negative infinity all compare
 * less than one, and must yield true.
 */
public class DecimalAbsLessThanOneTest {

  private static final Decimal ONE = new Decimal(1);

  private static final Method ABS_LESS_THAN_ONE;

  static {
    try {
      ABS_LESS_THAN_ONE = Decimal.class.getDeclaredMethod("isAbsLessThanOne");
      ABS_LESS_THAN_ONE.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Decimal.isAbsLessThanOne() not found", e);
    }
  }

  // Fixed seed for reproducibility.
  private static final long SEED = 20260907L;

  private long checkedEdge = 0;
  private long checkedArithmetic = 0;
  private long checkedRandom = 0;

  @Test
  public void testAbsLessThanOneMatchesCompare() {
    Random rnd = new Random(SEED);

    // --- Edge cases: zero variants -----------------------------------------
    String[] zeros = { "0", "0.0", "0.00", "-0", "0e0", "0e5", "0e-5", "-0.000", "-0.0" };
    for (String s : zeros) {
      check(new Decimal(s), "zero string '" + s + "'");
    }
    // Zeros produced by arithmetic.
    check(new Decimal("0.5").add(new Decimal("-0.5")), "0.5 + -0.5");
    check(new Decimal("2.75").subtract(new Decimal("2.75")), "2.75 - 2.75");
    check(new Decimal("-1.0").add(new Decimal("1.0")), "-1.0 + 1.0");
    check(new Decimal("0").multiply(new Decimal("-123.45"), null), "0 * -123.45");
    check(new Decimal("1e-30").subtract(new Decimal("1e-30")), "1e-30 - 1e-30");

    // --- Edge cases: explicit strings near the boundary ---------------------
    String[] edge = {
        "0.1", "0.9", "0.99", "0.01", "0.999", "1", "-1", "1.0", "1.00",
        "9.9", "-9.9", "-0.5", "10", "1e-1", "9.9e-1", "1.0e1",
        "0.99999999999999999999999999999",
        // additional boundary probes
        "1.000", "1.0000001", "0.9999999", "0.1e-30", "0.9e1", "1e0", "1e1",
        "-1e0", "-1.00", "100", "-100", "0.0000001", "9.99e-1", "1.01",
        ".5", "12.", "1000000000000000000000000000", "0.0000000000000000000000000000001"
    };
    for (String s : edge) {
      check(new Decimal(s), "edge string '" + s + "'");
    }

    // --- Non-finite via string and double constructors ----------------------
    String[] nonfinite = {
        "nan", "NaN", "infinity", "-infinity", "Infinity", "-Infinity",
        "+infinity", "+Infinity"
    };
    for (String s : nonfinite) {
      check(new Decimal(s), "non-finite string '" + s + "'");
    }
    check(new Decimal(Double.NaN), "Double.NaN");
    check(new Decimal(Double.POSITIVE_INFINITY), "Double.POSITIVE_INFINITY");
    check(new Decimal(Double.NEGATIVE_INFINITY), "Double.NEGATIVE_INFINITY");

    // Non-finite and zero results produced by arithmetic on finite operands.
    check(new Decimal("1.5").divide(new Decimal("0"), null), "1.5 / 0");
    check(new Decimal("-1.5").divide(new Decimal("0"), null), "-1.5 / 0");
    check(new Decimal("0").divide(new Decimal("0"), null), "0 / 0");

    // --- Random pool operands (checked as values in their own right) --------
    List<Decimal> pool = new ArrayList<>();
    for (int i = 0; i < 200; i++) {
      Decimal d = rnd.nextBoolean() ? new Decimal(randomDecimalString(rnd)) : randomRawDecimal(rnd);
      pool.add(d);
      check(d, "random pool #" + i);
    }

    // --- Arithmetic results: add / subtract / multiply / divide -------------
    MathContext ctx30 = new MathContext().precision(30);
    for (int i = 0; i < 400; i++) {
      Decimal a = pool.get(rnd.nextInt(pool.size()));
      Decimal b = pool.get(rnd.nextInt(pool.size()));
      check(a.add(b), "add #" + i);
      check(a.subtract(b), "subtract #" + i);
      check(a.multiply(b, null), "multiply #" + i);
      check(a.divide(b, null), "divide #" + i);
      check(b.divide(a, null), "reverse divide #" + i);
      check(a.multiply(b, ctx30), "multiply ctx #" + i);
      check(a.divide(b, ctx30), "divide ctx #" + i);
    }

    // --- Random finite Decimals ---------------------------------------------
    for (int i = 0; i < 1500; i++) {
      String s = randomDecimalString(rnd);
      check(new Decimal(s), "random string #" + i + " '" + s + "'");
    }
    for (int i = 0; i < 700; i++) {
      check(randomRawDecimal(rnd), "random raw limbs #" + i);
    }

    // ---- Report and floor on the random corpus -----------------------------
    long total = checkedEdge + checkedArithmetic + checkedRandom;
    assertTrue(checkedRandom >= 2000,
        "random corpus too small: " + checkedRandom + " < 2000");
    assertTrue(total == checkedEdge + checkedArithmetic + checkedRandom);
    System.out.println("DecimalAbsLessThanOne: checked " + total
        + " decimals (edge=" + checkedEdge + ", arithmetic=" + checkedArithmetic
        + ", random=" + checkedRandom + ") — 0 mismatches");
  }

  private void check(Decimal d, String origin) {
    boolean expected = d.compare(ONE, true) == -1;
    boolean actual = absLessThanOne(d);
    if (origin.startsWith("random ")) {
      checkedRandom++;
    } else if (origin.startsWith("add #") || origin.startsWith("subtract #")
        || origin.startsWith("multiply #") || origin.startsWith("divide #")
        || origin.startsWith("reverse divide #")
        || origin.startsWith("multiply ctx #") || origin.startsWith("divide ctx #")) {
      checkedArithmetic++;
    } else {
      checkedEdge++;
    }
    if (expected != actual) {
      fail("isAbsLessThanOne mismatch for " + d + " (" + origin + "): "
          + "expected compare(ONE,true)==-1 -> " + expected
          + ", predicate -> " + actual);
    }
  }

  private static boolean absLessThanOne(Decimal d) {
    try {
      return (Boolean) ABS_LESS_THAN_ONE.invoke(d);
    } catch (Exception e) {
      throw new RuntimeException("invoke isAbsLessThanOne failed for " + d, e);
    }
  }

  /**
   * Random finite Decimal built from a well-formed decimal string:
   * optional '-', 1..30 digits with a non-zero leading digit, an optional
   * decimal point, and an optional exponent in [-30, 30].
   */
  private static String randomDecimalString(Random rnd) {
    int len = 1 + rnd.nextInt(30); // 1..30 digits
    StringBuilder digits = new StringBuilder();
    digits.append(1 + rnd.nextInt(9));
    for (int i = 1; i < len; i++) {
      digits.append(rnd.nextInt(10));
    }
    StringBuilder sb = new StringBuilder();
    boolean neg = rnd.nextBoolean();
    if (neg) {
      sb.append('-');
    }
    sb.append(digits);
    if (rnd.nextInt(3) > 0) {
      // Decimal point after 1..len digits (never an empty integer part).
      int pos = 1 + rnd.nextInt(len);
      sb.insert(neg ? 1 + pos : pos, '.');
    }
    if (rnd.nextBoolean()) {
      sb.append(rnd.nextBoolean() ? "e" : "e-").append(rnd.nextInt(31));
    }
    return sb.toString();
  }

  /**
   * Random finite Decimal built directly via the (sign, exp, data, flag)
   * constructor with well-formed radix limbs: every limb other than the most
   * significant holds exactly Constants.RDIGITS digits, and the most
   * significant limb holds 1..RDIGITS digits with no leading zeros.
   */
  private static Decimal randomRawDecimal(Random rnd) {
    int digits = 1 + rnd.nextInt(30); // 1..30 coefficient digits
    int ndata = (digits + Constants.RDIGITS - 1) / Constants.RDIGITS;
    long[] data = new long[ndata];
    int lastDigits = digits - (ndata - 1) * Constants.RDIGITS; // 1..RDIGITS
    long lo = Constants.POWERS10[lastDigits - 1];
    long hi = Constants.POWERS10[lastDigits] - 1;
    data[ndata - 1] = lo + (long) (rnd.nextDouble() * (hi - lo + 1));
    for (int i = 0; i < ndata - 1; i++) {
      // Full 7-digit chunks in [10^6, 10^7 - 1].
      data[i] = Constants.P6 + rnd.nextInt(Constants.P7 - Constants.P6);
    }
    int exp = rnd.nextInt(61) - 30; // -30..30
    int sign = rnd.nextBoolean() ? 1 : -1;
    return new Decimal(sign, exp, data, DecimalFlag.NONE);
  }
}
