package com.squarespace.cldrengine;

import java.util.concurrent.ConcurrentHashMap;

import com.squarespace.cldrengine.calendars.Calendars;
import com.squarespace.cldrengine.calendars.CalendarsImpl;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.Meta;
import com.squarespace.cldrengine.internal.Pack;
import com.squarespace.cldrengine.internal.ResourcePacks;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.internal.SchemaConfig;
import com.squarespace.cldrengine.locale.CLocale;
import com.squarespace.cldrengine.locale.CLocaleImpl;
import com.squarespace.cldrengine.locale.LanguageTag;
import com.squarespace.cldrengine.locale.LanguageTagParser;
import com.squarespace.cldrengine.locale.LocaleResolver;

/**
 * Contains all functionality for a given locale.
 */
public class CLDR {

  private static final String VERSION = "0.14.1";
  private static final SchemaConfig CONFIG = new SchemaConfig();
  private static final Internals INTERNALS = new Internals(CONFIG, VERSION, false);

  private static final ConcurrentHashMap<String, Bundle> BUNDLES = new ConcurrentHashMap<>(100);
//  private final CLocale locale;
//  private final Bundle bundle;

  public final General General;
  public final Calendars Calendars;
  public final Schema Schema;

  protected CLDR(CLocale locale, Bundle bundle) {
//    this.locale = locale;
//    this.bundle = bundle;
    this.General = new GeneralImpl(locale, bundle);
    this.Calendars = new CalendarsImpl(bundle);
    this.Schema = Meta.SCHEMA;
  }

  public static CLDR get(String id) {
    CLocale locale = resolveLocale(id);
    return get(locale);
  }

  public static CLDR get(CLocale locale) {
    LanguageTag tag = locale.tag();
    String key = locale.tag().compact();
    Bundle bundle = BUNDLES.computeIfAbsent(key, (k) -> {
      Pack pack = ResourcePacks.get(tag.language());
      return pack.get(locale.tag());
    });
    return new CLDR(locale, bundle);
  }

  public static CLDR get(java.util.Locale locale) {
    return get(locale.toLanguageTag());
  }

  /**
   * Parse a locale identifier and resolve it. This returns a Locale object
   * that includes the original id string or tag's compact form, and
   * a resolved LanguageTag.
   */
  public static CLocale resolveLocale(String id) {
    LanguageTag tag = LocaleResolver.resolve(id);
    return new CLocaleImpl(id, tag);
  }

  public static CLocale resolveLocale(LanguageTag tag) {
    tag = LocaleResolver.resolve(tag);
    return new CLocaleImpl(tag.compact(), tag);
  }

  public static LanguageTag parseLanguageTag(String tag) {
    return LanguageTagParser.parse(tag);
  }
}
