package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum PluralType implements StringEnum<PluralType> {

  OTHER("other"),
  ZERO("zero"),
  ONE("one"),
  TWO("two"),
  FEW("few"),
  MANY("many")
  ;

  private static final Map<String, PluralType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(PluralType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private PluralType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static PluralType fromString(String s) {
    return REVERSE.get(s);
  }
}
