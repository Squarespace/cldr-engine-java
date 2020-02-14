package com.squarespace.cldrengine.locale;

import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.LanguageTag;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.ToString;

@Generated
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CLocaleImpl implements CLocale {

  private final String id;
  private final LanguageTag tag;

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public LanguageTag tag() {
    return this.tag;
  }
}
