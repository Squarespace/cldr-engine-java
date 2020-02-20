package com.squarespace.cldrengine;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CLDRException;
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
import com.squarespace.cldrengine.internal.MiscData;
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

  private static final SchemaConfig CONFIG = new SchemaConfig();
  private static final Internals INTERNALS = new Internals(CONFIG, MiscData.VERSION, false);
  private static final String CHECKSUM_ERROR = "Checksum mismatch on resource pack! The schema config used to " +
      "generate the resource pack must be identical to the one used at runtime.";

  private static final ConcurrentHashMap<String, CLDR> CLDRS = new ConcurrentHashMap<>(100);

  public final General General;
  public final Calendars Calendars;
  public final Numbers Numbers;
  public final Schema Schema;
  public final Units Units;
  private final String cldrVersion;

  protected CLDR(CLocale locale, Bundle bundle, String cldrVersion) {
    PrivateApi privateApi = new PrivateApi(bundle, INTERNALS);
    this.General = new GeneralImpl(bundle, locale, INTERNALS, privateApi);
    this.Calendars = new CalendarsImpl(bundle, INTERNALS, privateApi);
    this.Numbers = new NumbersImpl(bundle, INTERNALS, privateApi);
    this.Units = new UnitsImpl(bundle, INTERNALS, privateApi);
    this.Schema = Meta.SCHEMA;
    this.cldrVersion = cldrVersion;
  }

  /**
   * Returns the version of the CLDR used by this library.
   */
  public String cldrVersion() {
    return this.cldrVersion;
  }

  /**
   * The compatibility version identifies which version of the TypeScript
   * phensley/cldr library this build is compatible with.
   */
  public static String compatVersion() {
    return MiscData.VERSION;
  }

  /**
   * Return the schema used to configure the library. This contains arrays of
   * raw identifiers for locales, languages, etc.
   */
  public static SchemaConfig config() {
    return CONFIG;
  }

  /**
   * Return a list of the available locales.
   */
  public static List<String> availableLocales() {
    return ResourcePacks.availableLocales();
  }

  /**
   * Fetch the CLDR instance for the given locale id. If not an exact
   * match, the id will be resolved before fetching.
   */
  public static CLDR get(String id) {
    CLDR cldr = CLDRS.get(id);
    return cldr == null ? get(resolveLocale(id), false) : cldr;
  }

  /**
   * Fetch the CLDR instance for the given locale id. If not an exact
   * match, the id will be resolved before fetching.
   */
  public static CLDR get(CLocale locale) {
    CLDR cldr = CLDRS.get(locale.id());
    return cldr == null ? get(locale, true) : cldr;
  }

  /**
   * Fetch the CLDR instance for the given locale.
   */
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

  /**
   * Parse a locale identifier and resolve it. This returns a Locale object
   * that includes the original id string or tag's compact form, and
   * a resolved LanguageTag.
   */
  public static CLocale resolveLocale(LanguageTag tag) {
    tag = LocaleResolver.resolve(tag);
    return new CLocaleImpl(tag.compact(), tag);
  }

  /**
   * Parse a locale identifier and resolve it. This returns a Locale object
   * that includes the original id string or tag's compact form, and
   * a resolved LanguageTag.
   */
  public static LanguageTag parseLanguageTag(String tag) {
    return LanguageTagParser.parse(tag);
  }

  private static CLDR get(CLocale locale, boolean resolve) {
    // Always resolve the locale before fetching to ensure the "id" is
    // the minimal bundle identifier. This ensures the BUNDLES map
    // does not grow without bound.
    CLocale resolved = resolve ? resolveLocale(locale.tag()) : locale;
    return CLDRS.computeIfAbsent(resolved.id(), (id) -> {
      LanguageTag tag = resolved.tag();
      Pack pack = ResourcePacks.get(tag.language());
      // If checksum mismatch, severe error. Means the library was
      // distributed with the wrong CLDR resource packs.
      if (!INTERNALS.checksum.equals(pack.checksum())) {
        throw new CLDRException(CHECKSUM_ERROR);
      }
      Bundle bundle = pack.get(tag);
      return new CLDR(resolved, bundle, pack.cldrVersion());
    });
  }

}
