package com.squarespace.cldrengine;

import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.locale.Locale;

public interface Locales {

  /**
   * The current language bundle.
   */
  Bundle bundle();

  /**
   * The current locale.
   */
  Locale current();

  /**
   * Resolve a language tag to a Locale.
   */
  Locale resolve(String tag);

}
