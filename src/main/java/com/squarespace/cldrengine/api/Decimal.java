package com.squarespace.cldrengine.api;

import static com.squarespace.cldrengine.decimal.DecimalMath.digitCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.squarespace.cldrengine.decimal.Constants;
import com.squarespace.cldrengine.decimal.DecimalFlag;
import com.squarespace.cldrengine.decimal.DecimalFormatter;
import com.squarespace.cldrengine.decimal.DecimalMath;
import com.squarespace.cldrengine.decimal.DecimalMath.DivideResult;
import com.squarespace.cldrengine.decimal.DecimalMath.MathCtx;
import com.squarespace.cldrengine.decimal.StringDecimalFormatter;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Arbitrary precision decimal type.
 */
public class Decimal {

  private static enum Op {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    MOD
  }

  private static class ParseFlags {
    public static final int SIGN = 1;
    public static final int POINT = 2;
    public static final int EXP = 4;
  }

  private static final Set<String> NAN_VALUES = new HashSet<>(Arrays.asList(
      "nan", "NaN"));

  private static final Set<String> POS_INFINITY = new HashSet<>(Arrays.asList(
      "infinity", "+infinity", "Infinity", "+Infinity"));

  private static final Set<String> NEG_INFINITY = new HashSet<>(Arrays.asList(
     "-infinity", "-Infinity"));

  private static final String[] DECIMAL_DIGITS = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

  private static final Decimal ZERO = new Decimal(0);
  private static final Decimal ONE = new Decimal(1);
//  private static final Decimal TWO = new Decimal(2);

  private static final Decimal NAN = new Decimal("nan");
  private static final Decimal NEGATIVE_INFINITY = new Decimal("-infinity");
  private static final Decimal POSITIVE_INFINITY = new Decimal("infinity");

  @AllArgsConstructor
  @ToString
  public static class Properties {
    public final long[] data;
    public final int sign;
    public final int exp;
    public final int flag;
  }

  private long[] data;
  private int sign;
  private int exp;
  private int flag;

  public Decimal(String s) {
    parse(s);
  }

  public Decimal(Decimal d) {
    this.data = Arrays.copyOf(d.data, d.data.length);
    this.sign = d.sign;
    this.exp = d.exp;
    this.flag = d.flag;
  }

  public Decimal(int n) {
    parse(Integer.toString(n));
  }

  public Decimal(long n) {
    parse(Long.toString(n));
  }

  public Decimal(float n) {
    this((double)n);
  }

  public Decimal(double n) {
    if (Double.isNaN(n)) {
      this.flag = DecimalFlag.NAN;
      return;
    }
    if (!Double.isFinite(n)) {
      this.flag = DecimalFlag.INFINITY;
      this.sign = n == Double.POSITIVE_INFINITY ? 1 : -1;
      return;
    }
    parse(Double.toString(n));
  }

  public Decimal(int sign, int exp, long[] data, int flag) {
    this.sign = sign;
    this.exp = exp;
    this.data = Arrays.copyOf(data, data.length);
    this.flag = flag;
  }

  public int exp() {
    return this.exp;
  }

  public boolean isNaN() {
    return this.flag == DecimalFlag.NAN;
  }

  public boolean isFinite() {
    return this.flag == 0;
  }

  public boolean isInfinity() {
    return this.flag == DecimalFlag.INFINITY;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Decimal) {
      Decimal o = (Decimal) other;
      // This does strict equals vs compare(other) == 0 which accounts for
      // adjusted precision and exponents
      return this.flag == o.flag
          && this.exp == o.exp
          && this.sign == o.sign
          && Arrays.equals(this.data, o.data);
    }
    return false;
  }

  public int compare(Decimal v) {
    return compare(v, false);
  }

  /**
   * Compare decimal u to v, returning the following:
   *
   *  -1   if  u &lt; v
   *   0   if  u = v
   *   1   if  u &gt; v
   *
   * If the abs flag is true compare the absolute values.
   *
   * Any NAN argument will always return -1.
   */
  public int compare(Decimal v, boolean abs) {
    Decimal u = this;

    if (u.flag != 0 || v.flag != 0) {
      // NAN is never equal to itself or any other value
      if (u.flag == DecimalFlag.NAN || v.flag == DecimalFlag.NAN) {
        return -1;
      }

      // INFINITY

      // Infinities can be equal if their sign matches
      if (u.flag == v.flag) {
        return u.sign == v.sign ? 0 : u.sign == -1 ? -1 : 1;
      }

      // Negative infinity before all other values
      // Positive infinity after all other values
      return u.flag == DecimalFlag.INFINITY ?
        u.sign == -1 ? -1 : 1 :
        v.sign == -1 ? 1 : -1;
    }

    u = u.stripTrailingZeros();
    v = v.stripTrailingZeros();

    boolean uz = u.isZero();
    boolean vz = v.isZero();
    if (uz && vz) {
      return 0;
    }

    int us = u.sign;
    int vs = v.sign;
    if (!abs && us != vs) {
      return us == -1 ? -1 : 1;
    }

    int ue = u.alignexp();
    int ve = v.alignexp();
    if (ue != ve) {
      if (abs) {
        return ue < ve ? -1 : 1;
      }
      return ue < ve ? -1 * us : us;
    }

    if (u.exp != v.exp) {
      int shift = u.exp - v.exp;
      if (shift > 0) {
        return -DecimalMath.compare(v.data, u.data, shift);
      }
      return DecimalMath.compare(u.data, v.data, -shift);
    }

    // Same number of radix digits.
    int i = u.data.length - 1;
    while (i >= 0) {
      long a = u.data[i];
      long b = v.data[i];
      if (a != b) {
        return (a < b ? -1 : 1) * (abs ? 1 : u.sign);
      }
      i--;
    }

    // Equal
    return 0;
  }

  /**
   * Return the raw internal properties of the number. Use with caution.
   */
  public Properties properties() {
    return new Properties(this.data, this.sign, this.exp, this.flag);
  }

  /**
   * Return the absolute value of the number.
   */
  public Decimal abs() {
    return this.sign == -1 ? new Decimal(-this.sign, this.exp, this.data, this.flag) : this;
  }

  /**
   * Invert this number's sign.
   */
  public Decimal negate() {
    return this.isNaN() ? this : new Decimal(-this.sign, this.exp, this.data, this.flag);
  }

  /**
   * Indicates this number is negative.
   */
  public boolean isNegative() {
    return this.sign == -1;
  }

  /**
   * Signum.
   */
  public int signum() {
    return this.isZero() ? 0 : this.sign;
  }

  /**
   * Check if this number can be represented as an integer without loss of precision.
   * For example, '12.000' is the same number as '12'.
   */
  public boolean isInteger() {
    if (this.flag != 0) {
      return false;
    }
    return (this.exp + this.trailingZeros()) >= 0;
  }

  /**
   * Number is exactly zero. Exponent may exist, e.g. "0e-2" os "0.00"
   */
  public boolean isZero() {
    return this.flag == 0 && this.data.length == 1 && this.data[0] == 0;
  }

  /**
   * Adds v.
   */
  public Decimal add(Decimal v) {
    Decimal r = handleFlags(Op.ADDITION, v);
    if (r == null) {
      if (this.isZero()) {
        return v;
      }
      return v.isZero() ? this : this.addsub(this, v, v.sign);
    }
    return r;
  }

  /**
   * Subtracts v.
   */
  public Decimal subtract(Decimal v) {
    Decimal r = handleFlags(Op.SUBTRACTION, v);
    if (r == null) {
      if (this.isZero()) {
        return v.negate();
      }
      return v.isZero() ? this : this.addsub(this, v, -v.sign);
    }
    return r;
  }

  /**
   * Multplies by v with optional math context.
   */
  public Decimal multiply(Decimal v, MathContext context) {
    Decimal r = this.handleFlags(Op.MULTIPLICATION, v);
    if (r != null) {
      return r;
    }

    MathCtx ctx = DecimalMath.parseMathContext(RoundingModeType.HALF_EVEN, context);
    Decimal u = this;
    Decimal w = new Decimal(ZERO);
    w.sign = u.sign == v.sign ? 1 : -1;
    w.exp = (u.exp + v.exp);

    boolean uz = u.isZero();
    boolean vz = v.isZero();
    if (uz || vz) {
      if (!ctx.usePrecision) {
        w._setScale(ctx.scaleprec, RoundingModeType.HALF_EVEN);
      }
      return w;
    }

    w.data = DecimalMath.multiply(u.data, v.data);
    w.sign = u.sign == v.sign ? 1 : -1;
    w.trim();

    // Adjust coefficient to match precision
    if (ctx.usePrecision) {
      int delta = w.precision() - ctx.scaleprec;
      if (delta > 0) {
        w._shiftright(delta, ctx.rounding);
      }
    } else {
      w._setScale(ctx.scaleprec, ctx.rounding);
    }
    return w;
  }

  /**
   * Divide by v with optional math context.
   */
  public Decimal divide(Decimal v, MathContext context) {
    Decimal r = this.handleFlags(Op.DIVISION, v);
    if (r != null) {
      return r;
    }

    MathCtx ctx = DecimalMath.parseMathContext(RoundingModeType.HALF_EVEN, context);

    Decimal w = new Decimal(0);
    Decimal u = this;
    int sign = u.sign == v.sign ? 1 : -1;

    int shift = ctx.usePrecision
        ? (v.precision() - u.precision()) + ctx.scaleprec + 2
        : v.precision() + u.precision() + Math.abs(ctx.scaleprec) + 2;

    int exp = (u.exp - v.exp) - shift;

    if (shift > 0) {
      u = u.shiftleft(shift);
    } else if (shift < 0) {
      v = v.shiftleft(-shift);
    }

    DivideResult result = DecimalMath.divide(u.data, v.data);
    long[] quo = result.quotient;
    long[] rem = result.remainder;

    w.data = quo;
    w.sign = sign;
    w.exp = exp;
    w.trim();

    boolean hasrem = rem.length != 0 && !DecimalMath.allzero(rem, rem.length);
    if (hasrem) {
      long lsd = w.data[0] % 10;
      if (lsd == 0 || lsd == 5) {
        w.data[0]++;
      }
    }

    if (ctx.usePrecision) {
      // Adjust precision to match context
      int delta = w.precision() - ctx.scaleprec;
      if (delta > 0) {
        w._shiftright(delta, ctx.rounding);
      }
    } else {
      // Adjust scale to match context
      w._setScale(ctx.scaleprec, ctx.rounding);
    }
    if (ctx.usePrecision) {
      w._stripTrailingZeros();
    }
    return w;
  }

  /**
   * Divide by v and return the quotient and remainder.
   */
  public Pair<Decimal, Decimal> divmod(Decimal v) {
    Decimal rq = this.handleFlags(Op.DIVISION, v);
    if (rq != null) {
      Decimal rm = this.handleFlags(Op.MOD, v);
      return Pair.of(rq, rm);
    }

    Decimal u = this;
    int exp = u.exp > v.exp ? v.exp : u.exp;
    if (u.exp != v.exp) {
      int shift = u.exp - v.exp;
      if (shift > 0) {
        u = u.shiftleft(shift);
      } else {
        v = v.shiftleft(-shift);
      }
    }

    // Ensure u digits are >= v
    int dsize = v.data.length - u.data.length;
    if (dsize > 0) {
      if (u == this) {
        u = new Decimal(u);
      }
      u.data = Arrays.copyOf(u.data, u.data.length + dsize);
    }

    DivideResult res = DecimalMath.divide(u.data, v.data);

    int qsign = u.sign == v.sign ? 1 : -1;
    Decimal q = new Decimal(qsign, 0, res.quotient, 0);
    Decimal r = new Decimal(u.sign, exp, res.remainder, 0);
    return Pair.of(q.trim(), r.trim());
  }

  /**
   * Divide by v and return the remainder.
   */
  public Decimal mod(Decimal v) {
    Decimal r = this.handleFlags(Op.MOD, v);
    return r == null ? this.divmod(v)._2 : r;
  }

  /**
   * Number of trailing zeros.
   */
  public int trailingZeros() {
    if (this.flag != 0) {
      return 0;
    }
    int len = this.data.length;
    int r = 0;
    for (int i = 0; i < len; i++) {
      long n = this.data[i];
      if (n != 0) {
        r = i * Constants.RDIGITS;
        while (n % 10 == 0) {
          n /= 10;
          r++;
        }
        break;
      }
    }
    return r;
  }

  /**
   * Strip all trailing zeros.
   */
  public Decimal stripTrailingZeros() {
    if (this.flag != 0) {
      return this;
    }
    Decimal r = new Decimal(this);
    r._stripTrailingZeros();
    return r;
  }

  @AllArgsConstructor
  public static class Scientific {
    public final Decimal coefficient;
    public final int exponent;
  }

  /**
   * Return a scientific representation of the number,
   * Decimal coefficient and adjusted exponent.
   */
  public Scientific scientific(int minIntDigits) {
    if (this.flag != 0) {
      return new Scientific(this, 0);
    }
    minIntDigits = minIntDigits <= 1 ? 1 : minIntDigits;
    int exp = -(this.precision() - 1) + (minIntDigits - 1);
    // ensure exponent is not negative zero
    Decimal coeff = new Decimal(this.sign, exp == 0 ? 0 : exp, this.data, this.flag);
    return new Scientific(coeff, this.exp - coeff.exp);
  }

  /**
   * Number of digits in the unscaled value.
   */
  public int precision() {
    if (this.flag != 0) {
      return 0;
    }
    int len = this.data.length;
    return ((len - 1) * Constants.RDIGITS) + digitCount(this.data[len - 1]);
  }

  /**
   * Scale is the number of digits to the right of the decimal point.
   */
  public int scale() {
    return this.flag != 0 ? 0 : this.exp == 0 ? 0 : -this.exp;
  }

  /**
   * Number of integer digits, 1 or higher.
   */
  public int integerDigits() {
    return this.flag != 0 ? 0 : Math.max(this.precision() + this.exp, 1);
  }

  /**
   * Returns a new number with the given scale, shifting the coefficient as needed.
   */
  public Decimal setScale(int scale) {
    return setScale(scale, RoundingModeType.HALF_EVEN);
  }

  /**
   * Returns a new number with the given scale, shifting the coefficient as needed.
   */
  public Decimal setScale(int scale, RoundingModeType mode) {
    if (this.flag != 0) {
      return this;
    }
    Decimal r = new Decimal(this);
    r._setScale(scale, mode);
    return r;
  }

  /**
   * Adjusted exponent for alignment. Two numbers with the same aligned exponent are
   * aligned for arithmetic operations. If the aligned exponents do not match one
   * number must be shifted.
   */
  public int alignexp() {
    return this.flag != 0 ? 0 : (this.exp + this.precision()) - 1;
  }

  /**
   * Move the decimal point -n (left) or +n (right) places. Does not change
   * precision, only affects the exponent.
   */
  public Decimal movePoint(int n) {
    if (this.flag != 0) {
      return this;
    }
    Decimal w = new Decimal(this);
    w.exp += (int)Math.floor(n);
    return w;
  }

  /**
   * Shifts all digits to the left, increasing the precision.
   */
  public Decimal shiftleft(int shift) {
    if (this.flag != 0) {
      return this;
    }
    Decimal w = new Decimal(this);
    w._shiftleft(shift);
    return w;
  }

  /**
   * Shifts all digits to the right, reducing the precision. Result is rounded
   * using the given rounding mode.
   */
  public Decimal shiftright(int shift, RoundingModeType mode) {
    if (this.flag != 0) {
      return this;
    }
    Decimal w = new Decimal(this);
    w._shiftright(shift, mode);
    return w;
  }

  /**
   * Increment the least-significant integer digit.
   */
  public Decimal increment() {
    if (this.flag != 0) {
      return this;
    }
    Decimal r = new Decimal(this);
    if (r.sign == -1 || r.exp != 0) {
      return r.add(ONE);
    }
    r._increment();
    return r;
  }

  /**
   * Decrement the least-significant integer digit.
   */
  public Decimal decrement() {
    return this.flag != 0 ? this : this.subtract(ONE);
  }

  /**
   * Format the number to a string, using fixed point.
   */
  @Override
  public String toString() {
    return this.flag != 0 ? this.formatFlags() : this.formatString(this, 1);
  }

  // TODO: toParts

  // TODO: toScientificParts

  /**
   * Low-level formatting of string and Part[] forms.
   */
  public <R> void format(DecimalFormatter<R> formatter, String decimal, String group, int minInt, int minGroup,
      int priGroup, int secGroup, boolean zeroScale, String[] digits) {

    // Determine if grouping is enabled, and set the primary and
    // secondary group sizes.
    boolean grouping = !group.equals("");
    if (secGroup <= 0) {
      secGroup = priGroup;
    }
    int exp = this.exp;

    // Determine how many integer digits to emit. If integer digits is
    // larger than the integer coefficient we emit leading zeros.
    int _int = (this.data.length == 1 && this.data[0] == 0) ? 1 : this.precision() + exp;

    if (minInt <= 0 && this.compare(ONE, true) == -1) {
      // If the number is between 0 and 1 and format requested minimum
      // integer digits of zero, don't emit a leading zero digit.
      _int = 0;
    } else {
      _int = Math.max(_int, minInt);
    }

    // Array to append digits in reverse order
    int len = this.data.length;
    int groupSize = priGroup;
    int emitted = 0;

    boolean doGroup = (grouping && priGroup > 0 && _int >= minGroup + priGroup);

    // Push trailing zeros for a positive exponent, inly if the number
    // is non-zero
    int zeros = exp;
    if (!(this.data.length == 1 && this.data[0] == 0)) {
      while (zeros > 0) {
        formatter.add(digits[0]);
        emitted++;
        // Emit grouping
        if (doGroup && emitted > 0 && emitted % groupSize == 0) {
          // Push group character, reset emitted digits, and switch
          // to secondary grouping size.
          formatter.add(group);
          emitted = 0;
          groupSize = secGroup;
        }
        _int--;
        zeros--;
      }
    } else if (zeroScale && exp < 0) {
      // Handle sign of zero which means we have exactly '0'. If we
      // have the 'zeroScale' flag set, a negative exponent here will
      // emit zeros after the decimal point.
      while (exp < 0) {
        exp++;
        formatter.add(digits[0]);
      }
      formatter.add(decimal);
    }

    // Scan coefficient from least- to most-significant digit.
    int last = len - 1;
    for (int i = 0; i < len; i++) {
      // Count the decimal digits c in this radix digit d
      long d = this.data[i];
      int c = i == last ? digitCount(d) : Constants.RDIGITS;

      // Loop over the decimal digits
      for (int j = 0; j < c; j++) {
        // Push decimal digit
        formatter.add(digits[(int)(d % 10)]);
        d = (d / 10);

        // When we've reached exponent of 0, push the decimal point.
        exp++;
        if (exp == 0) {
          formatter.add(decimal);
        }

        // Decrement integer, increment emitted digits when exponent is positive, to
        // trigger grouping logic. We only do this once exp has become positive to
        // avoid counting emitted digits for decimal part.
        if (exp > 0) {
          emitted++;
          _int--;
          if (_int > 0) {
            // Emit grouping
            if (doGroup && emitted > 0 && emitted % groupSize == 0) {
              // Push group character, reset emitted digits, and switch
              // to secondary grouping size.
              formatter.add(group);
              emitted = 0;
              groupSize = secGroup;
            }
          }
        }
      }
    }

    // If exponent still negative, emit leading decimal zeros
    while (exp < 0) {
      formatter.add(digits[0]);

      // When we've reached exponent of 0, push the decimal point
      exp++;
      if (exp == 0) {
        formatter.add(decimal);
      }
    }

    // Leading integer zeros
    while (_int > 0) {
      formatter.add(digits[0]);
      emitted++;
      _int--;
      if (_int > 0) {

        // Emit grouping
        if (doGroup && emitted > 0 && emitted % groupSize == 0) {
          // Push group character, reset emitted digits, and switch
          // to secondary grouping size.
          formatter.add(group);
          emitted = 0;
          groupSize = secGroup;
        }

      }
    }
  }

  protected String formatFlags() {
    switch (this.flag) {
      case DecimalFlag.NAN:
        return "NaN";
      case DecimalFlag.INFINITY:
      default:
        return this.sign == 1 ? "Infinity" : "-Infinity";
    }
  }

  // TODO: formatFlagsParts

  protected String formatString(Decimal d, int minInt) {
    StringDecimalFormatter f = new StringDecimalFormatter();
    d.format(f, ".", "", minInt, 1, 3, 3, true, DECIMAL_DIGITS);
    String r = f.render();
    return d.sign == -1 ? "-" + r : r;
  }

  // TODO: formatParts

  /**
   * Handle setting of flags for operations per the IEEE-754-2008 specification.
   * These rules are also referenced in the EcmaScript specification:
   *
   * 12.7.3.1 - Applying the mul operator:
   * https://tc39.github.io/ecma262/#sec-applying-the-mul-operator
   *
   * 12.7.3.2 - Applying the div operator:
   * https://tc39.github.io/ecma262/#sec-applying-the-div-operator
   *
   * 12.7.3.3 - Applying the mod operator:
   * https://tc39.github.io/ecma262/#sec-applying-the-mod-operator
   *
   * 12.8.5 - Applying the additive operators to numbers:
   * https://tc39.github.io/ecma262/#sec-applying-the-additive-operators-to-numbers
   *
   */
  protected Decimal handleFlags(Op op, Decimal v) {
    Decimal u = this;
    int uflag = u.flag;
    int vflag = v.flag;

    // Any operation involving a NAN returns a NAN
    if (uflag == DecimalFlag.NAN || vflag == DecimalFlag.NAN) {
      return NAN;
    }

    boolean uinf = uflag == DecimalFlag.INFINITY;
    boolean vinf = vflag == DecimalFlag.INFINITY;
    boolean uzero = u.isZero();
    boolean vzero = v.isZero();

    switch (op) {
      case ADDITION:
        if (uinf && vinf) {
          return u.sign == v.sign ? (u.sign == 1 ? POSITIVE_INFINITY : NEGATIVE_INFINITY) : NAN;
        } else if (uinf || vinf) {
          return uinf ? u : v;
        }
        break;

      case SUBTRACTION:
        if (uinf && vinf) {
          return u.sign == v.sign ? NAN : u.sign == 1 ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
        } else if (uinf || vinf) {
          return uinf ? (u.sign == 1 ? POSITIVE_INFINITY : NEGATIVE_INFINITY)
              : v.sign == 1 ? NEGATIVE_INFINITY : POSITIVE_INFINITY;
        }
        break;

      case MULTIPLICATION:
        if (uinf) {
          return vzero ? NAN : u.sign == v.sign ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
        }
        if (vinf) {
          return uzero ? NAN : u.sign == v.sign ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
        }
        break;

      case DIVISION:
        if (uinf && vinf) {
          return NAN;
        }
        if (uinf) {
          return vzero ? (u.sign == 1 ? POSITIVE_INFINITY : NEGATIVE_INFINITY) :
            u.sign == v.sign ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
        }
        if (vinf) {
          return ZERO;
        }
        if (vzero) {
          return uzero ? NAN : u.sign == 1 ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
        }
        break;

      case MOD:
        if (uinf || vzero) {
          return NAN;
        }
        if (!uinf && vinf) {
          return u;
        }
        if (uzero && (!vzero && !vinf)) {
          return u;
        }
        break;
    }
    return null;
  }

  /**
   * Mutating in-place shift left.
   */
  protected void _shiftleft(int shift) {
    if (shift <= 0) {
      return;
    }
    Decimal w = this;
    int prec = w.precision();
    long[] data = new long[w.data.length];
    System.arraycopy(w.data, 0, data, 0, w.data.length);
    int m = data.length;

    // Compute the shift in terms of our radix.
    int q = shift / Constants.RDIGITS;
    int r = shift - (q * Constants.RDIGITS);

    // Expand w to hold shifted result and zero all elements.
    int n = size(prec + shift);
    w.data = new long[n];

    // Trivial case where shift is a multiple of our radix.
    if (r == 0) {
      while (--m >= 0) {
        w.data[m + q] = data[m];
      }
      return;
    }

    // Shift divided by radix leaves a remainder.
    long powlo = Constants.POWERS10[r];
    long powhi = Constants.POWERS10[Constants.RDIGITS - r];
    long hi = 0;
    long lo = 0;
    long loprev = 0;

    n--;
    m--;
    hi = (data[m] / powhi);
    loprev = data[m] - hi * powhi;
    if (hi != 0) {
      w.data[n] = hi;
      n--;
    }
    m--;

    // Divmod each element of u, copying the hi/lo parts to w.
    for (; m >= 0; m--, n--) {
      hi = (data[m] / powhi);
      lo = data[m] - hi * powhi;
      w.data[n] = powlo * loprev + hi;
      loprev = lo;
    }

    w.data[q] = powlo * loprev;
  }

  /**
   * Return the storage space needed to hold the given number of digits.
   */
  private int size(int n) {
    int q = (n / Constants.RDIGITS);
    int r = n - q * Constants.RDIGITS;
    return r == 0 ? q : q + 1;
  }

  /**
   * Mutating in-place shift right.
   */
  protected void _shiftright(int shift) {
    _shiftright(shift, RoundingModeType.HALF_EVEN);
  }

  /**
   * Mutating in-place shift right.
   */
  protected void _shiftright(int shift, RoundingModeType mode) {
    if (shift <= 0) {
      return;
    }
    if (this.isZero()) {
      this.exp += shift;
      return;
    }

    if (mode == null) {
      mode = RoundingModeType.HALF_EVEN;
    }

    Decimal w = this;
    int prec = w.precision();

    // Check if shift exceeds precision, so all digits are shifted to
    // zero with no rounding possible. Just set zero and bump the exponent.
    if (prec < shift) {
      w.data = new long[] { 0 };
      w.exp += shift;
      return;
    }

    // We only want to round up when there is a free zero integer
    // digit to the left. We do this when the number is < 0 or
    // we're not shifting out all of the digits.
    boolean round = w.alignexp() < 0 || prec != shift;

    long[] div = new long[] { 0, 0 };
    DecimalMath.divword(div, shift, Constants.RDIGITS);
    long q = div[0];
    long r = div[1];

    long[] data = Arrays.copyOf(w.data, w.data.length);
    Arrays.fill(w.data, 0);

    int i = 0;
    int j = 0;
    long rnd = 0;
    long rest = 0;

    if (r == 0) {
      // q is always non-zero here, else there would be no shift
      DecimalMath.divpow10(div, data[(int)(q - 1)], Constants.RDIGITS - 1);
      rnd = div[0];
      rest = div[1];
      if (rest == 0) {
        rest = !DecimalMath.allzero(data, (int)(q - 1)) ? 1 : 0;
      }
      for (j = 0; j < data.length - q; j++) {
        w.data[j] = data[(int)(q + j)];
      }
    } else {
      long hiprev = 0;
      int ph = Constants.POWERS10[Constants.RDIGITS - (int)r];
      // q is always < data.length here; otherwise r == 0
      DecimalMath.divpow10(div, data[(int)q], (int)r);
      hiprev = div[0];
      rest = div[1];

      DecimalMath.divpow10(div, rest,  (int)(r - 1));
      rnd = div[0];
      rest = div[1];
      if (rest == 0 && q > 0) {
        rest = !DecimalMath.allzero(data, (int)q) ? 1 : 0;
      }

      for (j = 0, i = (int)(q + 1); i < data.length; i++, j++) {
        DecimalMath.divpow10(div, data[i], (int)r);
        long hi = div[0];
        long lo = div[1];
        w.data[j] = ph * lo + hiprev;
        hiprev = hi;
      }

      if (hiprev != 0) {
        w.data[j] = hiprev;
      }
    }

    w.trim();
    w.exp += shift;

    if (round && w.round(rnd, rest, mode)) {
      w._increment();
    }
  }

  protected void _setScale(int scale, RoundingModeType mode) {
    int diff = scale - this.scale();
    if (diff > 0) {
      this._shiftleft(diff);
    } else {
      this._shiftright(-diff, mode);
    }
    this.exp = scale == 0 ? 0 : -scale;
    this.trim();
  }

  protected void _stripTrailingZeros() {
    int n = 0;
    // Special case for zero with negative exponent
    if (this.data.length == 1 && this.data[0] == 0 && this.exp < 0) {
      n -= this.exp;
    } else {
      n = this.trailingZeros();
    }
    if (n > 0){
      this._shiftright(n, RoundingModeType.DOWN);
    }
  }

  /**
   * Trim leading zeros from a result and reset sign and exponent accordingly.
   */
  protected Decimal trim() {
    this.trimLeadingZeros();
    return this;
  }

  protected void trimLeadingZeros() {
    int count = 0;
    int last = this.data.length - 1;
    int i = last;
    while (i > 0 && this.data[i] == 0) {
      count++;
      i--;
    }
    if (count > 0) {
      this.data = Arrays.copyOfRange(this.data, 0, this.data.length - count);
    }
  }

  /**
   * Increment the least-significant digit of the coefficient.
   */
  protected void _increment() {
    int len = this.data.length;
    long s = 0;
    int k = 1;
    for (int i = 0; k == 1 && i < len; i++) {
      s = this.data[i] + k;
      k = s == Constants.RADIX ? 1 : 0;
      this.data[i] = k == 1 ? 0 : s;
    }
    if (k == 1) {
      this.data = DecimalMath.push(this.data, 1);
    }
  }

  /**
   * Return a rounding indicator for a given rounding mode.
   */
  protected boolean round(long rnd, long rest, RoundingModeType mode) {
    if (rest != 0 && (rnd == 0 || rnd == 5)) {
      rnd++;
    }
    switch (mode) {
      case UP:
        // round away from zero
        return rnd != 0;
      case DOWN:
        // round towards zero
        return false;
      case CEILING:
        // round towards positive infinity
        return !(rnd == 0 || this.sign == -1);
      case FLOOR:
        // round towards negative infinity
        return !(rnd == 0 || this.sign >= 0);
      case HALF_UP:
        // if n >= 5 round up; otherwise round down
        return rnd >= 5;
      case HALF_DOWN:
        // if n > 5 round up; otherwise round down
        return rnd > 5;
      case HALF_EVEN:
        // if n = 5 and digit to left of n is odd round up; if even round down
        return (rnd > 5) || (rnd == 5 && this.isodd());
      default:
        return false;
    }
  }

  /**
   * Return true of this instance is odd.
   */
  protected boolean isodd() {
    return this.data.length > 0 && (this.data[0] % 2 == 1);
  }

  protected Decimal addsub(Decimal u, Decimal v, int vsign) {
    Decimal m = u; // m = bigger
    Decimal n = v; // n = smaller
    int swap = 0;
    if (m.exp < n.exp) {
      Decimal t = m;
      m = n;
      n = t;
      swap++;
    }

    int shift = m.exp - n.exp;
    m = m.shiftleft(shift);

    Decimal w = new Decimal(ZERO);
    w.exp = n.exp;

    if (m.data.length < n.data.length) {
      Decimal t = m;
      m = n;
      n = t;
      swap++;
    }

    if (u.sign == vsign) {
      w.data = DecimalMath.add(m.data, n.data);
      w.sign = vsign;
    } else {
      int ulen = m.data.length;
      int vlen = n.data.length;
      if (ulen == vlen) {
        for (int i = ulen - 1; i >= 0; i--) {
          if (m.data[i] != n.data[i]) {
            if (m.data[i] < n.data[i]) {
              Decimal t = m;
              m = n;
              n = t;
              swap++;
            }
            break;
          }
        }
      }
      w.data = DecimalMath.subtract(m.data, n.data);
      w.sign = (swap & 1) == 1 ? vsign : m.sign;
    }
    return w.trim();
  }

  private void parse(String s) {
    String msg = this._parse(s);
    if (msg != null) {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Parse a string into a Decimal.
   *
   * Expects strings of the form:
   *    "[-+][digits][.][digits][eE][-+][digits]"
   * or:
   *    "[nN]a[nN]"        for a NaN
   *    "[-+]?[iI]nfinity" for positive or negative infinity
   */
  private String _parse(String s) {
    if (NAN_VALUES.contains(s)) {
      this.flag = DecimalFlag.NAN;
      return null;
    }
    if (POS_INFINITY.contains(s)) {
      this.flag = DecimalFlag.INFINITY;
      this.sign = 1;
      return null;
    }
    if (NEG_INFINITY.contains(s)) {
      this.flag = DecimalFlag.INFINITY;
      this.sign = -1;
      return null;
    }

    // Local variables to accumulate digits, sign and exponent
    List<Integer> tmp = new ArrayList<>();

    // Default sign is 1. Negative sign is -1.
    int sign = 1;
    int exp = 0;

    // Flags to control parsing, raise errors.
    int flags = 0;

    // Current number being parse.
    int n = 0;

    // Index of power for current digit.
    int z = 0;

    // Pointer to current character being parsed.
    int i = s.length() - 1;

    // Total number of digits parsed.
    int dig = 0;

    // We parse from the end to avoid multiple passes or splitting of the
    // input string.
    while (i >= 0) {
      char code = s.charAt(i);
      switch (code) {
        case 'e':
        case 'E':
          if ((flags & ParseFlags.EXP) != 0) {
            return "Extra exponent character at " + i;
          }
          if (tmp.size() > 0) {
            // Exponent is currently limited to the size of Constants.RADIX
            return "Exponent too large";
          }
          if (dig == 0) {
            return "Exponent not provided";
          }
          // Indicate we have an exponent, and clear the sign flag.
          flags |= ParseFlags.EXP;
          flags &= ~ParseFlags.SIGN;

          // Copy the parsed number to the exponent and reset the digit count.
          dig = 0;
          exp = sign == -1 ? -n : n;
          sign = 1;
          n = 0;
          z = 0;
          break;

        case '-':
        case '+':
          if (dig == 0) {
            return "Found a bare sign symbol";
          }
          if ((flags & ParseFlags.SIGN) != 0) {
            return "Duplicate sign character at " + i;
          }
          sign = code == '-' ? -1 : 1;
          flags |= ParseFlags.SIGN;
          break;

        case '.':
          if ((flags & ParseFlags.POINT) != 0) {
            return "Extra radix point seen at " + i;
          }
          flags |= ParseFlags.POINT;
          exp -= dig;
          break;

        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          n += (code - '0') * Constants.POWERS10[z];
          z++;
          dig++;
          if (z == Constants.RDIGITS) {
            tmp.add(n);
            n = 0;
            z = 0;
          }
          break;

        default:
          return "Unexpected character at " + i + ": " + s.charAt(i);
      }
      i--;
    }

    if (dig == 0) {
      return "Number must include at least 1 digit";
    }

    tmp.add(n);

    this.data = convert(tmp);
    this.trim();
    this.sign = sign == -1 ? -1 : 1;
    this.exp = exp;
    return null;
  }

  private static long[] convert(List<Integer> list) {
    int len = list.size();
    long[] res = new long[len];
    for (int i = 0; i < len; i++) {
      res[i] = list.get(i);
    }
    return res;
  }

}
