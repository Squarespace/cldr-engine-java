package com.squarespace.cldrengine.messageformat.parsing;

import java.util.ArrayList;
import java.util.List;

public enum PluralChoiceType {

  EXACT(0),
  CATEGORY(1)
  ;

  private static final List<PluralChoiceType> REVERSE_INT;

  static {
    int len = PluralChoiceType.values().length;
    REVERSE_INT = new ArrayList<>(len);
    for (PluralChoiceType p : PluralChoiceType.values()) {
      REVERSE_INT.set(p.value, p);
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
