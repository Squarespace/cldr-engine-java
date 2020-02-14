package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum ListPatternPositionType implements StringEnum<ListPatternPositionType> {

  START("start"),
  MIDDLE("middle"),
  END("end"),
  TWO("two")
  ;

  private static final Map<String, ListPatternPositionType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(ListPatternPositionType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private ListPatternPositionType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static ListPatternPositionType fromString(String s) {
    return REVERSE.get(s);
  }
}
