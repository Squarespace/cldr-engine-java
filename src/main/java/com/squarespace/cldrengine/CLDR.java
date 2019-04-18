package com.squarespace.cldrengine;

import com.squarespace.cldrengine.calendars.Calendars;
import com.squarespace.cldrengine.calendars.CalendarsImpl;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.locale.LanguageTag;
import com.squarespace.cldrengine.locale.LanguageTagParser;
import com.squarespace.cldrengine.locale.Locale;

/**
 * Contains all functionality for a given locale.
 */
public class CLDR {

  private final Locale locale;
  private final Bundle bundle;

  public final General locales;
  public final Calendars Calendars;

  protected CLDR(Locale locale, Bundle bundle) {
    this.locale = locale;
    this.bundle = bundle;
    this.locales = new GeneralImpl(locale, bundle);
    this.Calendars = new CalendarsImpl(bundle);
  }

  public static CLDR get(String locale) {
    return null;
  }

  public static CLDR get(Locale locale) {
    return null;
  }

  public static CLDR get(java.util.Locale locale) {
    return get(locale.toLanguageTag());
  }

  /**
   * Parse a locale identifier and resolve it. This returns a Locale object
   * that includes the original id string or tag's compact form, and
   * a resolved LanguageTag.
   */
  public static Locale resolveLocale(String id) {
    return null;
  }

  public static Locale resolveLocale(LanguageTag tag) {
    return null;
  }

  public static LanguageTag parseLanguageTag(String tag) {
    return LanguageTagParser.parse(tag);
  }
}
