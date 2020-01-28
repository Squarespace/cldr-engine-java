package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum AltType implements StringEnum<AltType> {

  NONE("none"),
  SHORT("short"),
  NARROW("narrow"),
  VARIANT("variant"),
  STAND_ALONE("stand-alone"),
  LONG("long"),
  MENU("menu")
  ;

  private static final Map<String, AltType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(AltType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private AltType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static AltType fromString(String s) {
    return REVERSE.get(s);
  }
}
