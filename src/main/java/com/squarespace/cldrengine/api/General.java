package com.squarespace.cldrengine.api;

import java.util.List;

public interface General {

  /**
   * Returns the character order for the current locale, e.g. "ltr" for left-to-right
   * or "rtl" for right-to-left.
   */
  CharacterOrderType characterOrder();

  /**
   * Returns the line order for the current locale, e.g. "ttb" for top-to-bottom
   * or "btt" for bottom-to-top.
   */
  LineOrderType lineOrder();

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

  /**
   * Parses a language tag and canonicalizes its fields.
   */
  LanguageTag parseLanguageTag(String tag);

  /**
   * Returns the measurement system in use for the current locale generally.
   */
  MeasurementSystem measurementSystem();

  /**
   * Returns the measurement system in use for the current locale generally,
   * or for a specific measurement category. For example, to get the correct
   * measurement system for temperature you must pass in the category 'temperature'.
   */
  MeasurementSystem measurementSystem(MeasurementCategory category);

  /**
   * Format a list of items with the given type.
   */
  String formatList(List<String> items, ListPatternType type);

  /**
   * Format a list of items with the given type to an array of parts.
   */
  List<Part> formatListToParts(List<String> items, ListPatternType type);

  /**
   * Returns the display name for the given language code.
   */
  String getLanguageDisplayName(String code, DisplayNameOptions options);

  /**
   * Returns the display name for the given language code.
   */
  String getLanguageDisplayName(LanguageTag tag, DisplayNameOptions options);

  /**
   * Returns the display name for the given script code.
   */
  String getScriptDisplayName(String code, DisplayNameOptions options);

  /**
   * Returns the display name for the given script code.
   */
  String getScriptDisplayName(LanguageTag tag, DisplayNameOptions options);

  /**
   * Returns the display name for the given region code.
   */
  String getRegionDisplayName(String code, DisplayNameOptions options);

  /**
   * Returns the display name for the given region code.
   */
  String getRegionDisplayName(LanguageTag tag, DisplayNameOptions options);
}
