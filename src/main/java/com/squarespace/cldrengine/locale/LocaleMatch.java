package com.squarespace.cldrengine.locale;

import com.squarespace.cldrengine.api.CLocale;

public class LocaleMatch {

  public final CLocale locale;
  public final int distance;

  public LocaleMatch(CLocale locale, int distance) {
    this.locale = locale;
    this.distance = distance;
  }

}
