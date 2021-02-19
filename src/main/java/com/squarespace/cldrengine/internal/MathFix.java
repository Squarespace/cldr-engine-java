package com.squarespace.cldrengine.internal;


public class MathFix {

  /**
   * Fixes NoSuchMethodError when JDK 8 code is compiled by JDK 11.
   */
  public static long floorDiv(long x, int y) {
    return floorDiv(x, (long)y);
  }

  public static long floorDiv(long x, long y) {
    long r = x / y;
    // if the signs are different and modulo not zero, round down
    if ((x ^ y) < 0 && (r * y != x)) {
        r--;
    }
    return r;
  }

}
