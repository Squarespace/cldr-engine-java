package com.squarespace.cldrengine.internal;


public class LayoutSchema {

  public final FieldArrow characterOrder;
  public final FieldArrow lineOrder;

  public LayoutSchema(
      FieldArrow characterOrder,
      FieldArrow lineOrder) {
    this.characterOrder = characterOrder;
    this.lineOrder = lineOrder;
  }

}
