package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum ContextType implements StringEnum<ContextType> {

  MIDDLE_OF_TEXT("middle-of-text"),
  BEGIN_SENTENCE("begin-sentence"),
  STANDALONE("standalone"),
  UI_LIST_OR_MENU("ui-list-or-menu")
  ;

  private static final Map<String, ContextType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(ContextType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private ContextType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static ContextType fromString(String s) {
    return REVERSE.get(s);
  }
}
