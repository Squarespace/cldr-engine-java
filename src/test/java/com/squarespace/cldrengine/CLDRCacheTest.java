package com.squarespace.cldrengine;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

import java.lang.reflect.Field;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * Tests the CLDR instance cache used by {@link CLDR#get(String)}.
 * <p>
 * Instances are cached under the canonical <i>resolved</i> locale id
 * (e.g. "en-Latn-US"), never under the raw input spelling. Every input
 * spelling that resolves to the same locale must therefore share one
 * instance; spellings that resolve to different locales must stay distinct.
 * <p>
 * See ds4-findings P1-12 and plan todo 031.
 */
public class CLDRCacheTest {

  /**
   * Five spellings of the same resolved locale ("en-Latn-US") must share a
   * single cached instance.
   */
  @Test
  public void spellingsResolveToSameInstance() {
    CLDR en = CLDR.get("en-US");
    assertSame(en, CLDR.get("en-us"));
    assertSame(en, CLDR.get("en_US"));
    assertSame(en, CLDR.get("en-Latn-US"));
    assertSame(en, CLDR.get("EN-US"));
  }

  /**
   * The bare language spelling "en" resolves to the same locale as "en-US"
   * ("en-Latn-US" after adding likely subtags), so it must hit the same
   * cache entry. Before the fix each distinct spelling built and cached its
   * own full CLDR stack.
   */
  @Test
  public void languageAndRegionSpellingResolveToSameInstance() {
    assertSame(CLDR.get("en"), CLDR.get("en-US"));
  }

  /**
   * The same guarantees hold for a second locale: "ja", "ja-JP" and "ja-jp"
   * all resolve to "ja-Jpan-JP".
   */
  @Test
  public void secondLocaleSpellingsResolveToSameInstance() {
    CLDR ja = CLDR.get("ja-JP");
    assertSame(ja, CLDR.get("ja-jp"));
    assertSame(ja, CLDR.get("ja"));
  }

  /**
   * Guard against over-normalization: locales that resolve to different ids
   * must never collide in the cache. "es-MX" stays distinct from "es"
   * (es-Latn-MX vs es-Latn-ES), as does "fr-CA" from "fr".
   */
  @Test
  public void distinctResolvedLocalesStayDistinct() {
    assertNotSame(CLDR.get("en"), CLDR.get("ja"));
    assertNotSame(CLDR.get("es"), CLDR.get("es-MX"));
    assertNotSame(CLDR.get("fr"), CLDR.get("fr-CA"));
  }

  /**
   * The cache keys are the minimal resolved bundle ids, not spellings.
   * After fetching the English instances, the cache contains the canonical
   * "en-Latn-US" entry and none of the raw spellings.
   */
  @Test
  public void cacheIsKeyedByResolvedCompactId() throws Exception {
    CLDR.get("en-US");
    CLDR.get("en-us");
    CLDR.get("en_US");
    CLDR.get("EN-US");
    CLDR.get("en-Latn-US");

    Map<String, CLDR> cache = cache();
    assertSame(cache.get("en-Latn-US"), CLDR.get("en-US"));
    assertFalse(cache.containsKey("en"));
    assertFalse(cache.containsKey("en-US"));
    assertFalse(cache.containsKey("en-us"));
    assertFalse(cache.containsKey("en_US"));
    assertFalse(cache.containsKey("EN-US"));
  }

  @SuppressWarnings("unchecked")
  private static Map<String, CLDR> cache() throws Exception {
    Field field = CLDR.class.getDeclaredField("CLDRS");
    field.setAccessible(true);
    return (Map<String, CLDR>) field.get(null);
  }

}
