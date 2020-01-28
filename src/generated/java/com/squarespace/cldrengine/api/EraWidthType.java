package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum EraWidthType implements StringEnum<EraWidthType> {

  NAMES("names"),
  ABBR("abbr"),
  NARROW("narrow")
  ;

  private static final Map<String, EraWidthType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(EraWidthType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private EraWidthType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static EraWidthType fromString(String s) {
    return REVERSE.get(s);
  }
}
