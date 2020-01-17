package com.squarespace.cldrengine.internal;

import com.squarespace.cldrengine.decimal.Decimal;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class NumberingSystem {

  String name;
  NumberSymbols symbols;
  int minimumGroupingDigits;
  int primaryGroupingSize;
  int secondaryGroupingSize;

  public abstract String formatString(Decimal n, boolean groupDigits, int minInt);

}
