package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum FieldWidthType implements StringEnum<FieldWidthType> {

  ABBREVIATED("abbreviated"),
  NARROW("narrow"),
  SHORT("short"),
  WIDE("wide")
  ;

  private static final Map<String, FieldWidthType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(FieldWidthType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private FieldWidthType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static FieldWidthType fromString(String s) {
    return REVERSE.get(s);
  }
}
