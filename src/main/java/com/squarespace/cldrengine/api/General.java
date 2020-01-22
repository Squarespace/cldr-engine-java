package com.squarespace.cldrengine.api;

public interface General {

  /**
   * The current language bundle.
   */
  Bundle bundle();

  /**
   * The current locale.
   */
  CLocale locale();

  /**
   * Resolve a language tag to a Locale.
   */
  CLocale resolveLocale(String tag);

}
