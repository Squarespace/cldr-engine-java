package com.squarespace.cldrengine.numbers;

import java.util.Map;

import com.squarespace.cldrengine.internal.CurrencySpacingPattern;
import com.squarespace.cldrengine.internal.CurrencySpacingPos;
import com.squarespace.cldrengine.internal.NumberSymbolType;
import com.squarespace.cldrengine.internal.NumberSystemName;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NumberParams {

  public NumberSystemName numberSystemName;
  public NumberingSystem system;
  public NumberingSystem latnSystem;
  public String[] digits;
  public String[] latnDigits;
  public Map<NumberSymbolType, String> symbols;
  public int minimumGroupingDigits;
  public int primaryGroupingSize;
  public int secondaryGroupingSize;
  public Map<CurrencySpacingPos, Map<CurrencySpacingPattern, String>> currencySpacing;

}
