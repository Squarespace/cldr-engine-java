package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum CharacterOrderType implements StringEnum<CharacterOrderType> {

  LTR("ltr"),
  RTL("rtl")
  ;

  private static final Map<String, CharacterOrderType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(CharacterOrderType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private CharacterOrderType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static CharacterOrderType fromString(String s) {
    return REVERSE.get(s);
  }
}
