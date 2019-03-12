package com.squarespace.cldrengine;

import com.squarespace.cldrengine.locale.Locale;

public class CLDRFramework {

  /**
   * Parse a locale identifier and resolve it. This returns a Locale object
   * that includes the original id string or tag's compact form, and
   * a resolved LanguageTag.
   */
  public static Locale resolveLocale(String id) {
    return null;
  }

//  export const resolveLocale = (id: string | LanguageTag): Locale => {
//    const _id = typeof id === 'string' ? id : id.compact();
//    const tag = LanguageResolver.resolve(id);
//    return { id: _id, tag };
//  };


  public CLDR get(String locale) {
    return null;
  }

  public CLDR get(Locale locale) {
    return null;
  }
}
