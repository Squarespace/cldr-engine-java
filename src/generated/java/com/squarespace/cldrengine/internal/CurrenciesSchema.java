package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.CurrencyType;
public class CurrenciesSchema {

  public final Vector1Arrow<CurrencyType> displayName;
  public final Vector1Arrow<CurrencyType> decimal;
  public final Vector2Arrow<PluralType, CurrencyType> pluralName;
  public final Vector2Arrow<AltType, CurrencyType> symbol;

  public CurrenciesSchema(
      Vector1Arrow<CurrencyType> displayName,
      Vector1Arrow<CurrencyType> decimal,
      Vector2Arrow<PluralType, CurrencyType> pluralName,
      Vector2Arrow<AltType, CurrencyType> symbol) {
    this.displayName = displayName;
    this.decimal = decimal;
    this.pluralName = pluralName;
    this.symbol = symbol;
  }

}
