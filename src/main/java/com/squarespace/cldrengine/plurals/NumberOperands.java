package com.squarespace.cldrengine.plurals;

import static com.squarespace.cldrengine.decimal.DecimalMath.digitCount;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.decimal.Constants;

import lombok.ToString;

/**
 * Operands for use in evaluating localized plural rules: See:
 * http://www.unicode.org/reports/tr35/tr35-numbers.html#Plural_Operand_Meanings
 *
 * symbol value ---------------- n absolute value of the source number (integer and decimals) i integer digits of n v
 * number of visible fraction digits in n, with trailing zeros w number of visible fraction digits in n, without
 * trailing zeros f visible fractional digits in n, with trailing zeros t visible fractional digits in n, without
 * trailing zeros
 */
@ToString
public class NumberOperands {

  // When a number crosses this limit we reduce it to avoid overflow.
  // This limit is chosen so that a number <= this limit multiplied
  // by 10 will still be < JavaScript MAX_SAFE_INTEGER.
  private static final long LIMIT = 10000000000000L;

  private final Decimal d;
  public long n = 0;
  public long i = 0;
  public long v = 0;
  public long w = 0;
  public long f = 0;
  public long t = 0;

  public NumberOperands(Decimal d) {
    this.d = d;

    Decimal.Properties props = d.properties();
    if (props.flag != 0) {
      return;
    }

    long[] data = props.data;
    int exp = props.exp;

    int len = data.length;
    int last = len - 1;
    int precision = (last * Constants.RDIGITS) + digitCount(data[last]);

    // Local operands
    long n = 0;
    long v = exp < 0 ? -exp : 0;
    long w = 0;
    long f = 0;
    long t = 0;

    // Count trailing zeros
    int trail = 0;

    // Index of radix digit;
    int x = last;

    // Index of decimal digit in radix digit
    int y = 0;

    int intdigits = precision + exp;

    // Leading decimal zeros aren't part of the operands.
    if (intdigits < 0) {
      intdigits = 0;
    }

    outer:
    // Start at most-significant digit to last
    while (x >= 0) {
      long r = data[x];
      int c = x != last ? Constants.RDIGITS : digitCount(r);
      y = c - 1;

      // Scan each decimal digit of the radix number from
      // most- to least- significant.
      while (y >= 0) {
        long p = Constants.POWERS10[y];
        long q = (r / Constants.POWERS10[y]);

        if (intdigits > 0) {
          // Integer part
          n = (n * 10) + q;

          // If the integer digits exceed the limit we apply modulus.
          if (n > LIMIT) {
            // Stay below the limit but preseve (a) the magnitude and (b) as
            // many of the least-significant digits as possible
            n = (n % LIMIT) + LIMIT;
          }
          intdigits--;

        } else {
          // Decimal part
          if (q == 0) {
            trail++;
          } else {
            trail = 0;
          }
          f = (f * 10) + q;
        }

        // If the decimal digits exceed our limit we bail out early
        if (f > LIMIT) {
          break outer;
        }

        r %= p;
        y--;
      }
      x--;
    }

    // Trailing integer zeros
    while (exp > 0) {
      n *= 10;
      if (n > LIMIT) {
        // Stay below the limit but preserve (a) the magnitude and (b) as
        // many of the least-significant digits as possible
        n = (n % LIMIT) + LIMIT;
      }
      exp--;
    }

    // Special case for zero with exponent, e.g. '0.00'.
    if (len == 1 && data[0] == 0 && exp < 0) {
      w = 0;
    } else {
      w = v - trail;
      t = f;
      while (trail > 0) {
        t /= 10;
        trail--;
      }
    }

    this.n = n;
    this.i = n;
    this.v = v;
    this.w = w;
    this.f = f;
    this.t = t;
  }

  public long get(char op) {
    switch (op) {
      case 'n':
        return n;
      case 'i':
        return i;
      case 'v':
        return v;
      case 'w':
        return w;
      case 'f':
        return f;
      case 't':
        return t;
      default:
        return 0;
    }
  }
}
