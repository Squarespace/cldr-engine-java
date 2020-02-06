package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TimePeriodField {

  YEAR("year"),
  MONTH("month"),
  WEEK("week"),
  DAY("day"),
  HOUR("hour"),
  MINUTE("minute"),
  SECOND("second"),
  MILLIS("millis")
  ;

  private static final Map<String, TimePeriodField> REVERSE = new HashMap<>();

  static {
    Arrays.stream(TimePeriodField.values()).forEach(t -> REVERSE.put(t.value, t));
  }

  private String value;

  private TimePeriodField(String v) {
    this.value = v;
  }

  public static TimePeriodField fromString(String s) {
    return REVERSE.get(s);
  }

  @Override
  public String toString() {
    return value;
  }
}
