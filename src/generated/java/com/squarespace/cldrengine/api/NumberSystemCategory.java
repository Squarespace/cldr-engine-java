package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum NumberSystemCategory implements StringEnum<NumberSystemCategory> {

  DEFAULT("default"),
  NATIVE("native"),
  FINANCE("finance"),
  TRADITIONAL("traditional")
  ;

  private static final Map<String, NumberSystemCategory> REVERSE = new HashMap<>();
  static {
    Arrays.stream(NumberSystemCategory.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private NumberSystemCategory(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static NumberSystemCategory fromString(String s) {
    return REVERSE.get(s);
  }
}
