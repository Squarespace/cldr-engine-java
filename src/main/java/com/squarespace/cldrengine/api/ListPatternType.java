package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ListPatternType {

  AND("and"),
  AND_SHORT("and-short"),
  OR("or"),
  UNIT_LONG("unit-long"),
  UNIT_NARROW("unit-narrow"),
  UNIT_SHORT("unit-short")
  ;

  private static final Map<String, ListPatternType> REVERSE = new HashMap<>();

  static {
    Arrays.stream(ListPatternType.values()).forEach(t -> REVERSE.put(t.value, t));
  }

  private String value;

  private ListPatternType(String v) {
    this.value = v;
  }

  public static ListPatternType fromString(String s) {
    return REVERSE.get(s);
  }

  @Override
  public String toString() {
    return value;
  }
}
