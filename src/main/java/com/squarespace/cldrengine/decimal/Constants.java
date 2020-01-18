package com.squarespace.cldrengine.decimal;

class Constants {

  // 10^7 < sqrt(Number.MAX_SAFE_INTEGER) in JavaScript
  public static final int RADIX = (int)1e7;
  public static final int RDIGITS = 7;

  public static final int P0 = 1;
  public static final int P1 = 10;
  public static final int P2 = 100;
  public static final int P3 = 1000;
  public static final int P4 = 10000;
  public static final int P5 = 100000;
  public static final int P6 = 1000000;
  public static final int P7 = 10000000;
  public static final int P8 = 100000000;

  public static final int[] POWERS10 = new int[] {
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

}
