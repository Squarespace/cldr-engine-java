package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.LanguageIdType;

public class LanguageNameInfo {

  public final Vector2Arrow<AltType, LanguageIdType> displayName;

  public LanguageNameInfo(
      Vector2Arrow<AltType, LanguageIdType> displayName) {
    this.displayName = displayName;
  }

}
