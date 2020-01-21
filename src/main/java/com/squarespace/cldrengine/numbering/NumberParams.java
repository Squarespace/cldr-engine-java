package com.squarespace.cldrengine.numbering;

import com.squarespace.cldrengine.internal.NumberSystemName;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NumberParams {

  public NumberSystemName numberSystemName;
  public NumberingSystem system;
  public NumberingSystem latnSystem;
  public String[] digits;
  public String[] latnDigits;
  public NumberSymbols symbols;
  public int minimumGroupingDigits;
  public int primaryGroupingSize;
  public int secondaryGroupingSize;
  public CurrencySpacing currencySpacing;

}
