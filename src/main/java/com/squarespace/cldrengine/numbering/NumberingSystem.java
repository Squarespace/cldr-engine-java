package com.squarespace.cldrengine.numbering;

import java.util.Map;

import com.squarespace.cldrengine.decimal.Decimal;
import com.squarespace.cldrengine.internal.NumberSymbolType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class NumberingSystem {

  String name;
  Map<NumberSymbolType, String> symbols;
  int minimumGroupingDigits;
  int primaryGroupingSize;
  int secondaryGroupingSize;

  public abstract String formatString(long n, boolean groupDigits, int minInt);
  public abstract String formatString(Decimal n, boolean groupDigits, int minInt);

}
