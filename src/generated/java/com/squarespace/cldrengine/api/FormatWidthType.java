package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum FormatWidthType implements StringEnum<FormatWidthType> {

  SHORT("short"),
  MEDIUM("medium"),
  LONG("long"),
  FULL("full")
  ;

  private static final Map<String, FormatWidthType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(FormatWidthType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private FormatWidthType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static FormatWidthType fromString(String s) {
    return REVERSE.get(s);
  }
}
