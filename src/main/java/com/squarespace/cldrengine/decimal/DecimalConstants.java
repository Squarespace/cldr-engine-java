package com.squarespace.cldrengine.decimal;

import com.squarespace.cldrengine.api.Decimal;

public class DecimalConstants {

  public static final Decimal ZERO = new Decimal(0);
  public static final Decimal ONE = new Decimal(1);
  public static final Decimal TWO = new Decimal(2);

  // 105 digits of pi - https://oeis.org/A000796/constant
  public static final Decimal PI = new Decimal(
    "3.141592653589793238462643383279502884197169399375105" +
    "82097494459230781640628620899862803482534211706798214");

  // 105 digits of e - https://oeis.org/A001113/constant
  public static final Decimal E = new Decimal(
      "2.718281828459045235360287471352662497757247093699959" +
      "57496696762772407663035354759457138217852516642742746");

  public static final Decimal NAN = new Decimal("nan");
  public static final Decimal POSITIVE_INFINITY = new Decimal("+infinity");
  public static final Decimal NEGATIVE_INFINITY = new Decimal("-infinity");

}
