package com.squarespace.cldrengine.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum DateTimePatternFieldType implements StringEnum<DateTimePatternFieldType> {

  ERA("G"),
  YEAR("y"),
  MONTH("M"),
  DAY("d"),
  DAYPERIOD("a"), // "am / pm"
  DAYPERIOD_FLEX("B"), // "in the afternoon"
  HOUR24("H"),
  HOUR12("h"),
  MINUTE("m"),
  SECOND("s")
  ;

  private static final Map<String, DateTimePatternFieldType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(DateTimePatternFieldType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;
  private DateTimePatternFieldType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
  
  public static DateTimePatternFieldType fromString(String s) {
    return REVERSE.get(s);
  }

}
