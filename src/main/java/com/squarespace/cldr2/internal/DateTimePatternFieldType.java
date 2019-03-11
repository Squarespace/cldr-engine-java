package com.squarespace.cldr2.internal;

public enum DateTimePatternFieldType implements StringEnum {

  YEAR("y"),
  MONTH("M"),
  DAY("d"),
  DAYPERIOD("a"), // am / pm
  HOUR("H"),
  MINUTE("m"),
  SECOND("s")
  ;

  private final String value;
  private DateTimePatternFieldType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}
