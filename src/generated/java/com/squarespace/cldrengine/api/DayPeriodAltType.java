package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum DayPeriodAltType implements StringEnum<DayPeriodAltType> {

  NONE("none"),
  CASING("casing")
  ;

  private static final Map<String, DayPeriodAltType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(DayPeriodAltType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private DayPeriodAltType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static DayPeriodAltType fromString(String s) {
    return REVERSE.get(s);
  }
}
