package com.squarespace.cldrengine.plurals;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
class Rule {
  public final int index;
  public final int[][] indices;
}
