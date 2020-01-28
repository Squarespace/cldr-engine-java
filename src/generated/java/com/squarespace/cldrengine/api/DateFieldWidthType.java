package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum DateFieldWidthType implements StringEnum<DateFieldWidthType> {

  SHORT("short"),
  NARROW("narrow"),
  WIDE("wide")
  ;

  private static final Map<String, DateFieldWidthType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(DateFieldWidthType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private DateFieldWidthType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static DateFieldWidthType fromString(String s) {
    return REVERSE.get(s);
  }
}
