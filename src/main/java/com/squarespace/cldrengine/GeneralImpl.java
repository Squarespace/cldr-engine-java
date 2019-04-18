package com.squarespace.cldrengine;

import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.locale.Locale;

/**
 * Top-level namespace to expose info about the current locale and bundle,
 * and attach helper methods for dealing with locales.
 */
class GeneralImpl implements General {

  private final Locale locale;
  private final Bundle bundle;

  public GeneralImpl(Locale locale, Bundle bundle) {
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
  public Locale locale() {
    return this.locale;
  }

  /**
   * Resolve a language tag to a Locale.
   */
  public Locale resolveLocale(String tag) {
    return CLDR.resolveLocale(tag);
  }

}
