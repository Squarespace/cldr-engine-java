package com.squarespace.cldrengine;

import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.locale.CLocale;

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
