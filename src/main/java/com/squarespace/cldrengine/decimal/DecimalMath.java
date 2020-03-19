package com.squarespace.cldrengine.decimal;

import com.squarespace.cldrengine.api.MathContext;
import com.squarespace.cldrengine.api.RoundingModeType;

import lombok.AllArgsConstructor;
import lombok.ToString;

public class DecimalMath {

  /**
   * Knuth TAoCP 4.3.1 Algorithm A
   * Addition of nonnegative n-place integers u and v, returning the sum w.
   * Numbers must already be aligned and length u >= length v.
   */
  public static long[] add(long[] u, long[] v) {
    int vlen = v.length;
    int n = u.length;
    long[] w = new long[n];

    // A1. Initialize
    int j = 0;
    long k = 0;
    while (j < n) {
      // v may be shorter than u
      long vj = j < vlen ? v[j] : 0;

      // A2. Add digits
      long z = u[j] + vj + k;
      w[j] = z % Constants.RADIX;

      // .. k is being set to 1 or 0, to carry
      k = (z / Constants.RADIX);

      // A3. Loop on j
      j++;
    }
    if (k == 1) {
      return push(w, 1);
    }
    return w;
  }

  /**
   * Knuth TAoCP 4.3.1 Algorithm S
   * Subtraction of nonnegative n-place integers u >= v, returning the sum w.
   * Numbers must already be aligned and length u >= length v.
   */
  public static long[] subtract(long[] u, long[] v) {
    int m = u.length;
    int n = v.length;
    long[] w = new long[m];

    // S1. Initialize
    int j = 0;

    // borrow flag
    int k = 0;

    // S2. Subtract digits
    while (j < n) {
      long z = u[j] - v[j] - k;
      w[j] = z < 0 ? z + Constants.RADIX : z;
      // borrow
      k = z < 0 ? 1 : 0;
      j++;
    }

    // Propagate the borrow flag up
    while (k > 0 && j < m) {
      long z = u[j] - k;
      w[j] = z < 0 ? z + Constants.RADIX : z;
      k = z < 0 ? 1 : 0;
      j++;
    }

    // Borrow done, copy remainder of larger number
    while (j < m) {
      w[j] = u[j];
      j++;
    }

    return w;
  }

  /**
   * Knuth TAoCP 4.3.1 Algorithm M
   * Multiplication of nonnegative integers u and v, returning the product w.
   */
  public static long[] multiply(long[] u, long[] v) {
    int m = u.length;
    int n = v.length;

    // M1. Initialize, set w all to zero
    long[] w = new long[n + m];

    // Skip M2. Zero multiplier check, just follow the algorithm

    int i = 0;
    int j = 0;
    long k = 0;
    while (j < n) {
      // M3. Initialize i
      i = 0;
      k = 0;
      while (i < m) {
        // M4. Multiply and add
        long p = (k + w[i + j]) + u[i] * v[j];
        k = p / Constants.RADIX;
        w[i + j] = p - k * Constants.RADIX;

        // M5. Loop on i
        i++;
      }

      // Final carry
      w[j + m] = k;

      // M6. Loop on j
      j++;
    }
    return w;
  }

  /**
   * Multiplication of a nonnegative integer u by a single word v, returning the product w.
   * See TAoCP 4.3.1 exercise 13.
   */
  public static void multiplyword(long[] w, long[] u, long n, long v) {
    int i = 0;
    long k = 0;
    for (i = 0; i < n; i++) {
      long p = (k + u[i] * v);
      k = p / Constants.RADIX;
      w[i] = p - k * Constants.RADIX;
    }
    if (k > 0) {
      w[i] = k;
    }
  }

  @AllArgsConstructor
  public static class DivideResult {
    public final long[] quotient;
    public final long[] remainder;
  }

  /**
   * Knuth TAoCP 4.3.1 Algorithm D
   * Division of nonnegative integer u by v, returning the quotient q and remainder r.
   */
  public static DivideResult divide(long[] uc, long[] vc) {
    int n = vc.length;
    int m = uc.length - n;
    if (n == 1) {
      return divideword(uc, vc[0]);
    }

    int nplusm = n + m;
    if (nplusm < m) {
      throw new RuntimeException("n + m must be >= n, got " + m);
    }

    // Storage for copy of u which is modified in place, and v which needs an
    // extra digit.
    long[] u = push(uc, 0);
    long[] v = push(vc, 0);

    // Storage for quotient and remainder.
    long[] q = new long[nplusm + 1];

    // D1. Normalize
    long d = Constants.RADIX / (v[n - 1] + 1);
    if (d != 1) {
      multiplyword(u, uc, nplusm, d);
      multiplyword(v, vc, n, d);
    }

    long k = 0;
    long p = 0;
    long hi = 0;
    long lo = 0;

    int j = m;
    while (j >= 0) {
      // D3. Calculate q̂ and r̂.
      p = u[j + n - 1] + (u[j + n] * Constants.RADIX);
      long qhat = p / v[n - 1];
      long rhat = p - qhat * v[n - 1];
      for (;;) {
        // D3. Test if q̂ = radix ...
        if (qhat < Constants.RADIX) {
          long z = qhat * v[n - 2];
          hi = z / Constants.RADIX;
          lo = z - hi * Constants.RADIX;
          if (hi <= rhat) {
            if (hi != rhat || lo <= u[j + n - 2]) {
              break;
            }
          }
        }

        // D3. ... decrease q̂ by 1, increase r̂ by v[n - 1]
        qhat--;
        rhat += v[n - 1];
        if (rhat >= Constants.RADIX) {
          break;
        }
      }

      // D4. Multiply and subtract
      int i = 0;
      k = 0;
      for (i = 0; i <= n; i++) {
        // Multiply.
        p = qhat * v[i] + k;
        hi = p / Constants.RADIX;
        lo = p - hi * Constants.RADIX;

        // Subtract and determine carry.
        long x = u[i + j] - lo;
        k = x < 0 ? 1 : 0;
        u[i + j] = k != 0 ? x + Constants.RADIX : x;
        k += hi;
      }

      // Set the j-th quotient digit
      q[j] = qhat;

      // D5. Test remainder of D4.
      if (k > 0) {
        // D6. Add back. Quotient digit is too large by 1.
        q[j] -= 1;
        addhelper(u, j, v, n + 1, n);
      }

      // D7. Loop on j.
      j--;
    }

    // D8. Unnormalize remainder.
    long[] r = new long[n];
    k = 0;
    for (int i = n - 1; i >= 0; i--) {
      p = u[i] + (k * Constants.RADIX);
      r[i] = p / d;
      k = p - r[i] * d;
    }
    return new DivideResult(q, r);
  }

  @AllArgsConstructor
  @ToString
  public static class MathCtx {
    public final boolean usePrecision;
    public final int scaleprec;
    public final RoundingModeType rounding;
  }

  private static final int DEFAULT_PRECISION = 28;

  public static MathCtx parseMathContext(RoundingModeType rounding, MathContext context) {
    boolean usePrecision = true;
    int scaleprec = DEFAULT_PRECISION;
    if (context != null) {
      if (context.scale.ok()) {
        scaleprec = context.scale.get();
        usePrecision = false;
      } else if (context.precision.ok()) {
        scaleprec = Math.max(context.precision.get(), 0);
      }
      if (context.round.ok()) {
        rounding = context.round.get();
      }
    }
    return new MathCtx(usePrecision, scaleprec, rounding);
  }

  public static int digitCount(long w) {
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

  /**
   * divide() "add back" helper, adds v to u.
   */
  private static void addhelper(long[] u, int j, long[] v, int m, int n) {
    int i = 0;
    long k = 0;
    long s = 0;

    while (i < n) {
      s = u[i + j] + (v[i] + k);
      k = s >= Constants.RADIX ? 1 : 0;
      u[i + j] = k != 0 ? s - Constants.RADIX : s;
      i++;
    }

    while (k != 0 && i < m) {
      s = u[i + j] + k;
      k = s == Constants.RADIX ? 1 : 0;
      u[i + j] = k == 1 ? s - Constants.RADIX : s;
      i++;
    }

    // Final carry is ignored
  }

  /**
   * Knuth TAoCP 4.3.1 Exercise 16
   * Division of a nonnegative integer u by a single word v, returning the quotient q
   * and remainder r.
   */
  public static DivideResult divideword(long[] u, long v) {
    int n = u.length;
    long[] q = new long[n];
    long r = 0;
    for (int i = n - 1; i >= 0; i--) {
      long p = u[i] + (r * Constants.RADIX);
      q[i] = p / v;
      r = p - q[i] * v;
    }
    return new DivideResult(q, new long[] { r });
  }

  public static int compare(long[] a, long[] b, int shift) {
    int n = a.length;
    int m = b.length;
    long[] div = new long[] { 0, 0 };
    divword(div, shift, Constants.RDIGITS);
    long q = div[0];
    long r = div[1];

    if (r == 0) {
      while (--m >= 0) {
        int c = cmp(a[(int)(m + q)], b[m]);
        if (c != 0) {
          return c;
        }
      }
    } else {
      int ph = Constants.POWERS10[(int)r];
      int c = 0;
      long hi = 0;
      long loprev = 0;
      long lo = 0;
      --m;
      --n;
      divpow10(div, b[m--], (int)(Constants.RDIGITS - r));
      hi = div[0];
      loprev = div[1];
      if (hi != 0) {
        c = cmp(a[n], hi);
        if (c != 0) {
          return c;
        }
        --n;
      }
      long x = 0;
      for (; m >= 0; m--, n--) {
        divpow10(div, b[m], (int)(Constants.RDIGITS - r));
        hi = div[0];
        lo = div[1];
        x = ph * loprev + hi;
        c = cmp(a[n], x);
        if (c != 0) {
          return c;
        }
        loprev = lo;
      }
      x = ph * loprev;
      c = cmp(a[(int)q], x);
      if (c != 0) {
        return c;
      }
    }
    return allzero(a, (int)q) ? 0 : 1;
  }

  public static boolean allzero(long[] data, int len) {
    if (len > data.length) {
      return true;
    }
    while (--len >= 0) {
      if (data[len] != 0) {
        return false;
      }
    }
    return true;
  }

  public static int cmp(long a, long b) {
    return a < b ? -1 : a == b ? 0 : 1;
  }

  public static long[] push(long[] arr, long elem) {
    long[] res = new long[arr.length + 1];
    System.arraycopy(arr, 0, res, 0, arr.length);
    res[res.length - 1] = elem;
    return res;
  }

  public static long[] divpow10(long[] d, long n, int exp) {
    int p = Constants.POWERS10[exp];
    d[0] = n / p;
    d[1] = n - d[0] * p;
    return d;
  }

  public static long[] divword(long[] d, long n, long div) {
    d[0] = n / div;
    d[1] = n - d[0] * div;
    return d;
  }

}
