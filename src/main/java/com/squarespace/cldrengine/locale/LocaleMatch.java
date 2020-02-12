package com.squarespace.cldrengine.locale;

import com.squarespace.cldrengine.api.CLocale;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class LocaleMatch {

  public final CLocale locale;
  public final int distance;

}
