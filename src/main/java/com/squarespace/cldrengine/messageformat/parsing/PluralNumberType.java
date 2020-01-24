package com.squarespace.cldrengine.messageformat.parsing;

import java.util.ArrayList;
import java.util.List;

public enum PluralNumberType {

  CARDINAL(0),
  ORDINAL(1)
  ;

  private static final List<PluralNumberType> REVERSE = new ArrayList<>(PluralNumberType.values().length);

  static {
    for (PluralNumberType p : PluralNumberType.values()) {
      REVERSE.set(p.value, p);
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
