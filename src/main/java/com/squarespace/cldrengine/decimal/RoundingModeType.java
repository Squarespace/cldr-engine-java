package com.squarespace.cldrengine.decimal;

public enum RoundingModeType {

  UP("up"),
  DOWN("down"),
  CEILING("ceiling"),
  FLOOR("floor"),
  HALF_UP("half-up"),
  HALF_DOWN("half-down"),
  HALF_EVEN("half-even")
  ;

  private String value;

  private RoundingModeType(String v) {
    this.value = v;
  }
}
