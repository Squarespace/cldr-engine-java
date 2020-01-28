package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum DayPeriodType implements StringEnum<DayPeriodType> {

  NOON("noon"),
  MIDNIGHT("midnight"),
  AM("am"),
  PM("pm"),
  MORNING1("morning1"),
  MORNING2("morning2"),
  AFTERNOON1("afternoon1"),
  AFTERNOON2("afternoon2"),
  EVENING1("evening1"),
  EVENING2("evening2"),
  NIGHT1("night1"),
  NIGHT2("night2")
  ;

  private static final Map<String, DayPeriodType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(DayPeriodType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private DayPeriodType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static DayPeriodType fromString(String s) {
    return REVERSE.get(s);
  }
}
