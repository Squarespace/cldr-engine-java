package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.numbers.DecimalAdjustOptions;

public interface Numbers {

  /**
   * Adjusts a decimal number using the given options.
   */
  Decimal adjustDecimal(Decimal num, DecimalAdjustOptions options);

}
