package com.squarespace.cldrengine.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CurrencyFractions {

  public final int digits;
  public final int rounding;
  public final int cashDigits;
  public final int cashRounding;

}
