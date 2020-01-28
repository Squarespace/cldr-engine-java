package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum CurrencySpacingPattern implements StringEnum<CurrencySpacingPattern> {

  CURRENCYMATCH("currencyMatch"),
  SURROUNDINGMATCH("surroundingMatch"),
  INSERTBETWEEN("insertBetween")
  ;

  private static final Map<String, CurrencySpacingPattern> REVERSE = new HashMap<>();
  static {
    Arrays.stream(CurrencySpacingPattern.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private CurrencySpacingPattern(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static CurrencySpacingPattern fromString(String s) {
    return REVERSE.get(s);
  }
}
