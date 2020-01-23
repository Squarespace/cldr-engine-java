package com.squarespace.cldrengine.api;

import java.util.List;

public interface Numbers {

  /**
   * Adjusts a decimal number using the given options.
   */
  Decimal adjustDecimal(Decimal num, DecimalAdjustOptions options);

  /**
   * Formats a decimal number to string.
   */
  String formatDecimal(Decimal n, DecimalFormatOptions options);

  /**
   * Formats a decimal number to an array of parts.
   */
  List<Part> formatDecimalToParts(Decimal n, DecimalFormatOptions options);

  /**
   * Formats a currency value to string.
   */
  String formatCurrency(Decimal n, CurrencyType code, CurrencyFormatOptions options);

  /**
   * Formats a currency value to an array of parts.
   */
//  formatCurrencyToParts(num: DecimalArg, code: CurrencyType, options?: CurrencyFormatOptions): Part[];

}

