package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum RoundingModeType {

  UP("up"),
  DOWN("down"),
  CEILING("ceiling"),
  FLOOR("floor"),
  HALF_UP("half-up"),
  HALF_DOWN("half-down"),
  HALF_EVEN("half-even")
  ;

  private static final Map<String, RoundingModeType> REVERSE = new HashMap<>();

  static {
    Arrays.stream(RoundingModeType.values()).forEach(t -> REVERSE.put(t.value, t));
  }

  private String value;

  private RoundingModeType(String v) {
    this.value = v;
  }

  public static RoundingModeType fromString(String s) {
    return REVERSE.get(s);
  }

  @Override
  public String toString() {
    return value;
  }
}
