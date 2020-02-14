package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum QuarterType implements StringEnum<QuarterType> {

  _1("1"),
  _2("2"),
  _3("3"),
  _4("4")
  ;

  private static final Map<String, QuarterType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(QuarterType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private QuarterType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static QuarterType fromString(String s) {
    return REVERSE.get(s);
  }
}
