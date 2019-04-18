package com.squarespace.cldrengine;

import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.locale.Locale;

public interface General {

  /**
   * The current language bundle.
   */
  Bundle bundle();

  /**
   * The current locale.
   */
  Locale locale();

  /**
   * Resolve a language tag to a Locale.
   */
  Locale resolveLocale(String tag);

}
