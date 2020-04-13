package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum EraAltType implements StringEnum<EraAltType> {

  NONE("none"),
  SENSITIVE("sensitive")
  ;

  private static final Map<String, EraAltType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(EraAltType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private EraAltType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static EraAltType fromString(String s) {
    return REVERSE.get(s);
  }
}
