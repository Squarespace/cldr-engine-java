package com.squarespace.cldrengine.internal;

public class FieldArrow {

  private int offset;

  public FieldArrow(int offset) {
    this.offset = offset;
  }

  public String get(PrimitiveBundle bundle) {
    return bundle.get(this.offset);
  }

}
