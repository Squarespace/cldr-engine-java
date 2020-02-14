package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum CurrencySpacingPos implements StringEnum<CurrencySpacingPos> {

  BEFORE("before"),
  AFTER("after")
  ;

  private static final Map<String, CurrencySpacingPos> REVERSE = new HashMap<>();
  static {
    Arrays.stream(CurrencySpacingPos.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private CurrencySpacingPos(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static CurrencySpacingPos fromString(String s) {
    return REVERSE.get(s);
  }
}
