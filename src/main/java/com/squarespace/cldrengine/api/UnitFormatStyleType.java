package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum UnitFormatStyleType {

  DECIMAL("decimal"),
  SHORT("short"),
  LONG("long"),
  SCIENTIFIC("scientific")
  ;

  private static final Map<String, UnitFormatStyleType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(UnitFormatStyleType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private UnitFormatStyleType(String v) {
    this.value = v;
  }

  public String value() {
    return this.value;
  }

  public static UnitFormatStyleType fromString(String s) {
    return REVERSE.get(s);
  }

}
