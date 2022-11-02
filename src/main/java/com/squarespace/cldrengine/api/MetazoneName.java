package com.squarespace.cldrengine.api;


public class MetazoneName {

  private final String generic;
  private final String standard;
  private final String daylight;

  public MetazoneName(String generic, String standard, String daylight) {
    this.generic = generic;
    this.standard = standard;
    this.daylight = daylight;
  }

  public String generic() {
    return generic;
  }

  public String standard() {
    return standard;
  }

  public String daylight() {
    return daylight;
  }

}
