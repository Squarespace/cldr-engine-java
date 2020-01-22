package com.squarespace.cldrengine.numbers;

import java.util.Map;

import com.squarespace.cldrengine.api.CurrencySpacingPattern;
import com.squarespace.cldrengine.api.CurrencySpacingPos;
import com.squarespace.cldrengine.api.NumberSymbolType;
import com.squarespace.cldrengine.api.NumberSystemName;

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
