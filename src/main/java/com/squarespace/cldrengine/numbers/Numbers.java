package com.squarespace.cldrengine.numbers;

import com.squarespace.cldrengine.decimal.Decimal;

public interface Numbers {

  /**
   * Adjusts a decimal number using the given options.
   */
  Decimal adjustDecimal(Decimal num, DecimalAdjustOptions options);

}
