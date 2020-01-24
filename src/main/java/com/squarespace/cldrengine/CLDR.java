package com.squarespace.cldrengine;

import java.util.concurrent.ConcurrentHashMap;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.Calendars;
import com.squarespace.cldrengine.api.General;
import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.api.Numbers;
import com.squarespace.cldrengine.api.Units;
import com.squarespace.cldrengine.calendars.CalendarsImpl;
import com.squarespace.cldrengine.general.GeneralImpl;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.Meta;
import com.squarespace.cldrengine.internal.Pack;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.internal.ResourcePacks;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.internal.SchemaConfig;
import com.squarespace.cldrengine.locale.CLocaleImpl;
import com.squarespace.cldrengine.locale.LanguageTagParser;
import com.squarespace.cldrengine.locale.LocaleResolver;
import com.squarespace.cldrengine.numbers.NumbersImpl;
import com.squarespace.cldrengine.units.UnitsImpl;

/**
 * Contains all functionality for a given locale.
 */
public class CLDR {

  private static final String VERSION = "0.14.1";
  private static final SchemaConfig CONFIG = new SchemaConfig();
  private static final Internals INTERNALS = new Internals(CONFIG, VERSION, false);

  private static final ConcurrentHashMap<String, Bundle> BUNDLES = new ConcurrentHashMap<>(100);

  public final General General;
  public final Calendars Calendars;
  public final Numbers Numbers;
  public final Schema Schema;
  public final Units Units;

  protected CLDR(CLocale locale, Bundle bundle) {
    PrivateApi privateApi = new PrivateApi(bundle, INTERNALS);
    this.General = new GeneralImpl(bundle, locale, INTERNALS);
    this.Calendars = new CalendarsImpl(bundle, INTERNALS, privateApi);
    this.Numbers = new NumbersImpl(bundle, INTERNALS, privateApi);
    this.Units = new UnitsImpl(bundle, INTERNALS, privateApi);
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
