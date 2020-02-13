package com.squarespace.cldrengine.messageformat.parsing;

import java.util.HashMap;
import java.util.Map;

public enum PluralChoiceType {

  EXACT(0),
  CATEGORY(1)
  ;

  private static final Map<Integer, PluralChoiceType> REVERSE_INT = new HashMap<>();

  static {
    for (PluralChoiceType p : PluralChoiceType.values()) {
      REVERSE_INT.put(p.value, p);
    }
  }

  private final int value;

  private PluralChoiceType(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

  public static PluralChoiceType fromInt(int i) {
    return REVERSE_INT.get(i);
  }

}
