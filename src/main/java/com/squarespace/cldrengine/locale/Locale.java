package com.squarespace.cldrengine.locale;

/**
 * Wrapper pairing an application's opaque locale identifier with a
 * parsed and resolved language tag object.
 */
public interface Locale {

  /**
   * Application's own identifier for the locale, e.g. 'en_US', 'fr-CA', etc.
   * We preserve this since applications may use it as a unique key to
   * resolve translated messages, and may be forced to use a legacy
   * identifier.
   */
  String id();

  /**
   * Language tag that has been parsed and resolved. Parsing canonicalizes
   * the subtags, while resolution includes substituting language and
   * territory aliases and adding likely subtags.
   */
  LanguageTag tag();

}
