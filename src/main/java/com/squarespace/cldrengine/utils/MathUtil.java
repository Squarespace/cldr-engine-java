package com.squarespace.cldrengine.utils;

public class MathUtil {

  public static long floorDiv(long n, long d, long[] rem) {
    if (n >= 0) {
      rem[0] = (long)(n % d);
      return (long)(n / d);
    }
    long q = (long)(((n + 1) / d) - 1);
    rem[0] = (long)(n - ((long)q * d));
    return q;
  }

}
