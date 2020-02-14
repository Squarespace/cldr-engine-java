package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum WeekdayType implements StringEnum<WeekdayType> {

  _1("1"),
  _2("2"),
  _3("3"),
  _4("4"),
  _5("5"),
  _6("6"),
  _7("7")
  ;

  private static final Map<String, WeekdayType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(WeekdayType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private WeekdayType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static WeekdayType fromString(String s) {
    return REVERSE.get(s);
  }
}
