package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum LineOrderType implements StringEnum<LineOrderType> {

  LTR("ltr"),
  RTL("rtl")
  ;

  private static final Map<String, LineOrderType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(LineOrderType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private LineOrderType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static LineOrderType fromString(String s) {
    return REVERSE.get(s);
  }
}
