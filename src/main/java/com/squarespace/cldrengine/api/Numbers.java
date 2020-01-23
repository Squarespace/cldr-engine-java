package com.squarespace.cldrengine.api;

public interface Numbers {

  /**
   * Adjusts a decimal number using the given options.
   */
  Decimal adjustDecimal(Decimal num, DecimalAdjustOptions options);

  /**
   * Formats a decimal number to string.
   */
  String formatDecimal(Decimal n, DecimalFormatOptions options);
}

