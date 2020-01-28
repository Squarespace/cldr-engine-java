package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum ContextTransformFieldType implements StringEnum<ContextTransformFieldType> {

  CALENDAR_FIELD("calendar-field"),
  CURRENCYNAME("currencyName"),
  DAY_FORMAT_EXCEPT_NARROW("day-format-except-narrow"),
  DAY_STANDALONE_EXCEPT_NARROW("day-standalone-except-narrow"),
  ERA_ABBR("era-abbr"),
  ERA_NAME("era-name"),
  KEYVALUE("keyValue"),
  LANGUAGES("languages"),
  MONTH_FORMAT_EXCEPT_NARROW("month-format-except-narrow"),
  MONTH_STANDALONE_EXCEPT_NARROW("month-standalone-except-narrow"),
  NUMBER_SPELLOUT("number-spellout"),
  RELATIVE("relative"),
  SCRIPT("script"),
  TYPOGRAPHICNAMES("typographicNames")
  ;

  private static final Map<String, ContextTransformFieldType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(ContextTransformFieldType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private ContextTransformFieldType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static ContextTransformFieldType fromString(String s) {
    return REVERSE.get(s);
  }
}
