package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum DecimalFormatStyleType {

  DECIMAL("decimal"),
  SHORT("short"),
  LONG("long"),
  SCIENTIFIC("scientific"),
  PERCENT("percent"),
  PERCENT_SCALED("percent-scaled"),
  PERMILLE("permille"),
  PERMILLE_SCALED("permille-scaled")
  ;

  private static final Map<String, DecimalFormatStyleType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(DecimalFormatStyleType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private DecimalFormatStyleType(String v) {
    this.value = v;
  }

  public String value() {
    return this.value;
  }

  public static DecimalFormatStyleType fromString(String s) {
    return REVERSE.get(s);
  }

}
