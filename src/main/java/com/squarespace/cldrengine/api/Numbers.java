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
  List<Part> formatCurrencyToParts(Decimal n, CurrencyType code, CurrencyFormatOptions options);

  /**
   * Returns the currency symbol of the given width.
   */
  String getCurrencySymbol(CurrencyType code, CurrencySymbolWidthType width);

  /**
   * Returns the display name of the currency.
   */
  String getCurrencyDisplayName(CurrencyType code, CurrencyDisplayNameOptions options);

  /**
   * Returns the pluralized display name of the currency.
   */
  String getCurrencyPluralName(Decimal n, CurrencyType code, CurrencyDisplayNameOptions options);

  /**
   * Return the currency fraction info for a given currency code.
   */
  CurrencyFractions getCurrencyFractions(CurrencyType code);

  /**
   * Return the currency code to use for a given region.
   */
  CurrencyType getCurrencyForRegion(String region);

  /**
   * Returns the plural cardinal category of the given decimal number.
   */
  PluralType getPluralCardinal(Decimal n, DecimalAdjustOptions options);

  /**
   * Returns the plural ordinal category of the given decimal number.
   */
  PluralType getPluralOrdinal(Decimal n, DecimalAdjustOptions options);
}
