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
    while (j < k) {
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

  public static long[] push(long[] arr, long elem) {
    long[] res = new long[arr.length + 1];
    System.arraycopy(arr, 0, res, 0, arr.length);
    res[res.length - 1] = elem;
    return res;
  }
}
