package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.PluralType;

public class DecimalFormats {

  public final FieldArrow standard;
  public final DigitsArrow<PluralType> short_;
  public final DigitsArrow<PluralType> long_;

  public DecimalFormats(
      FieldArrow standard,
      DigitsArrow<PluralType> short_,
      DigitsArrow<PluralType> long_) {
    this.standard = standard;
    this.short_ = short_;
    this.long_ = long_;
  }

}
