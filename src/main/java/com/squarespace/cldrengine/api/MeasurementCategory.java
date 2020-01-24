package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MeasurementCategory {

  TEMPERATURE("temperature")
  ;

  private static final Map<String, MeasurementCategory> REVERSE = new HashMap<>();

  static {
    Arrays.stream(MeasurementCategory.values()).forEach(t -> REVERSE.put(t.value, t));
  }

  private String value;

  private MeasurementCategory(String v) {
    this.value = v;
  }

  public static MeasurementCategory fromString(String s) {
    return REVERSE.get(s);
  }

  @Override
  public String toString() {
    return value;
  }

}
