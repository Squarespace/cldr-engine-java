package com.squarespace.cldrengine.decimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Arbitrary precision decimal type.
 */
public class Decimal {

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

  public static Decimal coerce(String n) {
    return new Decimal(n);
  }

  public static Decimal coerce(int n) {
    return new Decimal(n);
  }

  public static Decimal coerce(long n) {
    return new Decimal(n);
  }

  public static Decimal coerce(float n) {
    return new Decimal(n);
  }

  public static Decimal coerce(double n) {
    return new Decimal(n);
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

  public int compare(Decimal v) {
    return compare(v, false);
  }

  public int compare(Decimal v, boolean abs) {
    return -1;
  }

  // TODO: properties

  // TODO: abs

  // TODO: negate

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

  // TODO: isInteger

  /**
   * Number is exactly zero. Exponent may exist, e.g. "0e-2" os "0.00"
   */
  public boolean isZero() {
    return this.flag == 0 && this.data.length == 1 && this.data[0] == 0;
  }

  // TODO: add
  public Decimal add(Decimal v) {
    Decimal r = handleFlags(Op.ADDITION, v);
    return r == null ? this.addsub(this, v, v.sign) : r;
  }

  // TODO: subtract

  // TODO: multiply

  // TODO: divide

  // TODO: divmod

  // TODO: mod

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

  // TODO: scientific

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

  // TODO: setScale

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

  public Decimal shiftleft(int shift) {
    if (this.flag != 0) {
      return this;
    }
    Decimal w = new Decimal(this);
    w._shiftleft(shift);
    return w;
  }

  // TODO: increment

  // TODO: decrement

  // TODO: toString

  // TODO: toParts

  // TODO: toScientificParts

  // TODO: format<R>

  // TODO: formatFlags

  // TODO: formatFlagsParts

  // TODO: formatString

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


  // TODO: fromRaw

  protected void _shiftleft(int shift) {
    if (shift <= 0) {
      return;
    }
    Decimal w = this;
    int prec = w.precision();
    // TODO: resume
//    long[] data = copy(w.data);
  }

  // TODO: _shiftright

  protected void _shiftright(int shift) {
    _shiftright(shift, RoundingModeType.HALF_EVEN);
  }

  protected void _shiftright(int shift, RoundingModeType mode) {

  }

  // TODO: _setScale

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

  protected void _increment() {

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
   * @return
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
      throw new RuntimeException(msg);
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
          n += (code - '0') & POWERS10[z];
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

  private static int digitCount(long w) {
    if (w < Constants.P4) {
      if (w < Constants.P2) {
        return w < Constants.P1 ? 1 : 2;
      }
      return w < Constants.P3 ? 3 : 4;
    }
    if (w < Constants.P6) {
      return w < Constants.P5 ? 5 : 6;
    }
    return w < Constants.P7 ? 7 : 8;
  }

  private static long[] convert(List<Integer> list) {
    int len = list.size();
    long[] res = new long[len];
    for (int i = 0; i < len; i++) {
      res[i] = list.get(i);
    }
    return res;
  }

  private static final Decimal ZERO = new Decimal(0);
  private static final Decimal ONE = new Decimal(1);
  private static final Decimal TWO = new Decimal(2);

  private static final Decimal NAN = new Decimal("nan");
  private static final Decimal NEGATIVE_INFINITY = new Decimal("-infinity");
  private static final Decimal POSITIVE_INFINITY = new Decimal("infinity");

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

  private static final int[] POWERS10 = new int[] {
      Constants.P0,
      Constants.P1,
      Constants.P2,
      Constants.P3,
      Constants.P4,
      Constants.P5,
      Constants.P6,
      Constants.P7,
      Constants.P8
  };

  private static final Set<String> NAN_VALUES = new HashSet<>(Arrays.asList(
      "nan", "NaN"));

  private static final Set<String> POS_INFINITY = new HashSet<>(Arrays.asList(
      "infinity", "+infinity", "Infinity", "+Infinity"));

  private static final Set<String> NEG_INFINITY = new HashSet<>(Arrays.asList(
     "-infinity", "-Infinity"));

  private static final String[] DECIMAL_DIGITS = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

}
