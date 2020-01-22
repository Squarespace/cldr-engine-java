package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CurrencyFormatStyleType {

  SYMBOL("symbol"),
  ACCOUNTING("accounting"),
  CODE("code"),
  NAME("name"),
  SHORT("short")
  ;

  private static final Map<String, CurrencyFormatStyleType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(CurrencyFormatStyleType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private CurrencyFormatStyleType(String v) {
    this.value = v;
  }

  public String value() {
    return this.value;
  }

  public static CurrencyFormatStyleType fromString(String s) {
    return REVERSE.get(s);
  }

}
