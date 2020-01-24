package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum UnitLength {

  SHORT("short"),
  NARROW("narrow"),
  LONG("long")
  ;

  public final String value;

  private static final Map<String, UnitLength> REVERSE = new HashMap<>();
  static {
    Arrays.stream(UnitLength.values()).forEach(t -> REVERSE.put(t.value, t));
  }

  private UnitLength(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static UnitLength fromString(String s) {
    return REVERSE.get(s);
  }
}

