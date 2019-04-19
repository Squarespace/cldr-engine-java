package com.squarespace.cldrengine;

import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.locale.CLocale;

/**
 * Top-level namespace to expose info about the current locale and bundle,
 * and attach helper methods for dealing with locales.
 */
class GeneralImpl implements General {

  private final CLocale locale;
  private final Bundle bundle;

  public GeneralImpl(CLocale locale, Bundle bundle) {
    this.locale = locale;
    this.bundle = bundle;
  }

  /**
   * The current language bundle.
   */
  public Bundle bundle() {
    return this.bundle;
  }

  /**
   * The current locale.
   */
  public CLocale locale() {
    return this.locale;
  }

  /**
   * Resolve a language tag to a Locale.
   */
  public CLocale resolveLocale(String tag) {
    return CLDR.resolveLocale(tag);
  }

}
