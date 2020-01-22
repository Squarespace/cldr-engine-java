package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CurrencySymbolWidthType {

  DEFAULT("default"),
  NARROW("narrow")
  ;

  private static final Map<String, CurrencySymbolWidthType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(CurrencySymbolWidthType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private CurrencySymbolWidthType(String v) {
    this.value = v;
  }

  public String value() {
    return this.value;
  }

  public static CurrencySymbolWidthType fromString(String s) {
    return REVERSE.get(s);
  }

}
