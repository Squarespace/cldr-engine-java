package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MeasurementSystem {

  US("us"),
  UK("uk"),
  METRIC("metric")
  ;

  private static final Map<String, MeasurementSystem> REVERSE = new HashMap<>();

  static {
    Arrays.stream(MeasurementSystem.values()).forEach(t -> REVERSE.put(t.value, t));
  }

  private String value;

  private MeasurementSystem(String v) {
    this.value = v;
  }

  public static MeasurementSystem fromString(String s) {
    return REVERSE.get(s);
  }

  @Override
  public String toString() {
    return value;
  }

}
