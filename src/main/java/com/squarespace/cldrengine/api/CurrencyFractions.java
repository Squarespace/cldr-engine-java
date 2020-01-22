package com.squarespace.cldrengine.api;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class CurrencyFractions {

  public final int digits;
  public final int rounding;
  public final int cashDigits;
  public final int CashRounding;

}
