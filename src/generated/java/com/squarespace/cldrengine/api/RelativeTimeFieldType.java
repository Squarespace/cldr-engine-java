package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum RelativeTimeFieldType implements StringEnum<RelativeTimeFieldType> {

  YEAR("year"),
  QUARTER("quarter"),
  MONTH("month"),
  WEEK("week"),
  DAY("day"),
  SUN("sun"),
  MON("mon"),
  TUE("tue"),
  WED("wed"),
  THU("thu"),
  FRI("fri"),
  SAT("sat"),
  HOUR("hour"),
  MINUTE("minute"),
  SECOND("second")
  ;

  private static final Map<String, RelativeTimeFieldType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(RelativeTimeFieldType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private RelativeTimeFieldType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static RelativeTimeFieldType fromString(String s) {
    return REVERSE.get(s);
  }
}
