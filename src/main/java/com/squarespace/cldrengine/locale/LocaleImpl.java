package com.squarespace.cldrengine.locale;

import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.LanguageTag;

public class LocaleImpl implements CLocale {

  public static final LanguageTag UNDEFINED = new LanguageTag(null, null, null);

  private final String id;
  private final LanguageTag tag;

  public LocaleImpl(String id, LanguageTag tag) {
    this.id = id;
    this.tag = tag;
  }

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public LanguageTag tag() {
    return this.tag;
  }
}
