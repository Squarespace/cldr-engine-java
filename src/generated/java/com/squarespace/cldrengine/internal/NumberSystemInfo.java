package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.NumberMiscPatternType;
import com.squarespace.cldrengine.api.NumberSymbolType;
public class NumberSystemInfo {

  public final Vector1Arrow<NumberSymbolType> symbols;
  public final CurrencyFormats currencyFormats;
  public final DecimalFormats decimalFormats;
  public final FieldArrow percentFormat;
  public final FieldArrow scientificFormat;
  public final Vector1Arrow<NumberMiscPatternType> miscPatterns;

  public NumberSystemInfo(
      Vector1Arrow<NumberSymbolType> symbols,
      CurrencyFormats currencyFormats,
      DecimalFormats decimalFormats,
      FieldArrow percentFormat,
      FieldArrow scientificFormat,
      Vector1Arrow<NumberMiscPatternType> miscPatterns) {
    this.symbols = symbols;
    this.currencyFormats = currencyFormats;
    this.decimalFormats = decimalFormats;
    this.percentFormat = percentFormat;
    this.scientificFormat = scientificFormat;
    this.miscPatterns = miscPatterns;
  }

}
