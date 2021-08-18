package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum NumberSymbolType implements StringEnum<NumberSymbolType> {

  APPROXIMATELYSIGN("approximatelySign"),
  CURRENCYDECIMAL("currencyDecimal"),
  CURRENCYGROUP("currencyGroup"),
  DECIMAL("decimal"),
  EXPONENTIAL("exponential"),
  GROUP("group"),
  INFINITY("infinity"),
  LIST("list"),
  MINUSSIGN("minusSign"),
  NAN("nan"),
  PERMILLE("perMille"),
  PERCENTSIGN("percentSign"),
  PLUSSIGN("plusSign"),
  SUPERSCRIPTINGEXPONENT("superscriptingExponent"),
  TIMESEPARATOR("timeSeparator")
  ;

  private static final Map<String, NumberSymbolType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(NumberSymbolType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private NumberSymbolType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static NumberSymbolType fromString(String s) {
    return REVERSE.get(s);
  }
}
