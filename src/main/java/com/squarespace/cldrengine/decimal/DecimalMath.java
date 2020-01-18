package com.squarespace.cldrengine.decimal;

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
    int vlen = v.length;
    int n = u.length;
    long[] w = new long[n];

    // S1. Initialize
    int j = 0;
    long k = 0;
    while (j < n) {
      // v may be shorter than u
      long vj = j < vlen ? v[j] : 0;

      // S2. Subtract digits
      long z = u[j] - vj + k;
      w[j] = z < 0 ? z + Constants.RADIX : z;

      // .. k is set to -1 or 0, to borrow
      k = z < 0 ? -1 : 0;

      // S3. Loop on j
      j++;
    }
    return u;
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
