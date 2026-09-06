package com.squarespace.cldrengine.decimal;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Rational;

/**
 * Common Rational values as constants.
 */
public class RationalConstants {

  public static final Rational ZERO = new Rational(new Decimal(0), DecimalConstants.ONE);
  public static final Rational ONE = new Rational(DecimalConstants.ONE, DecimalConstants.ONE);

}
