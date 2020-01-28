package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.CurrencySpacingPattern;
import com.squarespace.cldrengine.api.CurrencySpacingPos;
import com.squarespace.cldrengine.api.PluralType;

public class CurrencyFormats {

  public final FieldArrow standard;
  public final FieldArrow accounting;
  public final DigitsArrow<PluralType> short_;
  public final Vector2Arrow<CurrencySpacingPos, CurrencySpacingPattern> spacing;
  public final Vector1Arrow<PluralType> unitPattern;

  public CurrencyFormats(
      FieldArrow standard,
      FieldArrow accounting,
      DigitsArrow<PluralType> short_,
      Vector2Arrow<CurrencySpacingPos, CurrencySpacingPattern> spacing,
      Vector1Arrow<PluralType> unitPattern) {
    this.standard = standard;
    this.accounting = accounting;
    this.short_ = short_;
    this.spacing = spacing;
    this.unitPattern = unitPattern;
  }

}
