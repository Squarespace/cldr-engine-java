package com.squarespace.cldrengine.messageformat.parsing;

import java.util.HashMap;
import java.util.Map;

public enum PluralNumberType {

  CARDINAL(0),
  ORDINAL(1)
  ;

  private static final Map<Integer, PluralNumberType> REVERSE = new HashMap<>();

  static {
    for (PluralNumberType p : PluralNumberType.values()) {
      REVERSE.put(p.value, p);
    }
  }

  private final int value;

  private PluralNumberType(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

  public static PluralNumberType fromInt(int i) {
    return REVERSE.get(i);
  }


}
