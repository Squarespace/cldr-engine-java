package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum TimeZoneNameType implements StringEnum<TimeZoneNameType> {

  DAYLIGHT("daylight"),
  GENERIC("generic"),
  STANDARD("standard")
  ;

  private static final Map<String, TimeZoneNameType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(TimeZoneNameType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private TimeZoneNameType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static TimeZoneNameType fromString(String s) {
    return REVERSE.get(s);
  }
}
