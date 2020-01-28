package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum NumberMiscPatternType implements StringEnum<NumberMiscPatternType> {

  AT_LEAST("at-least"),
  AT_MOST("at-most"),
  APPROX("approx"),
  RANGE("range")
  ;

  private static final Map<String, NumberMiscPatternType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(NumberMiscPatternType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private NumberMiscPatternType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static NumberMiscPatternType fromString(String s) {
    return REVERSE.get(s);
  }
}
