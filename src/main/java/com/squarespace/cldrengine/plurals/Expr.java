package com.squarespace.cldrengine.plurals;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
class Expr {
  public final char operand;
  public final int mod;
  public final int relop;

  // elements are Integer or Range
  public final List<Object> rangelist;
}
