package com.squarespace.cldrengine.parsing;

import java.util.ArrayList;

public class NumberPattern {

  public ArrayList<Object> nodes = new ArrayList<>();
  public int minInt = 0;
  public int maxFrac = 0;
  public int minFrac = 0;
  public int priGroup = 0;
  public int secGroup = 0;

  public NumberPattern() {

  }

  public NumberPattern(NumberPattern other) {
    this.nodes = new ArrayList<>(other.nodes);
    this.minInt = other.minInt;
    this.maxFrac = other.maxFrac;
    this.minFrac = other.minFrac;
    this.priGroup = other.priGroup;
    this.secGroup = other.secGroup;
  }

  public static enum Field {
    MINUS,
    PERCENT,
    CURRENCY,
    NUMBER,
    EXPONENT,
    PLUS
  }

}
