package com.squarespace.cldrengine.locale;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.LanguageTag;

/**
 * BCP-47 grandfathered tag resolution, ported from @phensley/cldr 1.15.0
 * (packages/language-tag/src/parser.ts).
 *
 * The TS parser builds its grandfathered map from the generated alias data
 * first, then applies the ICU fallbacks on top so they win. The Java port
 * originally populated the ICU fallbacks in the map's field initializer and
 * then overwrote them with the generated data in the static block, so the
 * fallbacks were silently lost (cel-gaulish, i-default, i-enochian, i-mingo
 * and zh-min all resolved to "und"). The fix mirrors the TS construction
 * order.
 *
 * Expected values in grandfathered-tags-expected.json were captured from the
 * published 1.15.0 TS engine by running its LanguageTagParser over the full
 * grandfathered tag list; the Java engine must produce byte-identical output.
 * Note the v1.15.0 TS behavior for en-GB-oed: its ICU fallback key is
 * mixed-case while the map lookup lowercases the input, so the entry never
 * matches and the tag parses directly to "en-GB". Java reproduces this
 * exactly (no deviation).
 */
public class GrandfatheredTagsTest {

  private static JsonObject expectedRoot() throws Exception {
    try (InputStream in = GrandfatheredTagsTest.class.getResourceAsStream("grandfathered-tags-expected.json")) {
      return JsonParser.parseReader(new InputStreamReader(in, "UTF-8")).getAsJsonObject();
    }
  }

  private static List<String> tags(JsonObject root) {
    List<String> tags = new ArrayList<>(root.keySet());
    java.util.Collections.sort(tags);
    return tags;
  }

  /**
   * Every grandfathered tag plus the en-US negative control resolves to the
   * same subtags as the TS v1.15.0 oracle.
   */
  @Test
  public void testGrandfatheredResolution() throws Exception {
    JsonObject root = expectedRoot();
    for (String tag : tags(root)) {
      JsonObject expected = root.getAsJsonObject(tag);
      LanguageTag actual = LanguageTagParser.parse(tag);
      assertEquals(actual.compact(), expected.get("compact").getAsString(), tag + " compact");
      assertEquals(actual.language(), expected.get("language").getAsString(), tag + " language");
      assertEquals(actual.script(), expected.get("script").getAsString(), tag + " script");
      assertEquals(actual.region(), expected.get("region").getAsString(), tag + " region");
      assertEquals(actual.variant(), expected.get("variant").getAsString(), tag + " variant");
      assertEquals(actual.privateUse(), expected.get("privateUse").getAsString(), tag + " privateUse");
    }
  }

  /**
   * Negative control: a plain, non-grandfathered tag is unaffected.
   */
  @Test
  public void testPlainTagUnaffected() throws Exception {
    LanguageTag en = LanguageTagParser.parse("en-US");
    assertEquals(en.language(), "en");
    assertEquals(en.region(), "US");
    assertEquals(en.privateUse(), "");
    assertEquals(en.compact(), "en-US");
  }

  /**
   * The audit's bonus finding: en-GB-oed must not resolve to the generated
   * data's "en-GB-oxendict" nor to the dead ICU fallback. TS v1.15.0 never
   * matches the mixed-case map key and parses directly; Java must match.
   */
  @Test
  public void testEnGboed() throws Exception {
    LanguageTag oed = LanguageTagParser.parse("en-GB-oed");
    assertEquals(oed.language(), "en");
    assertEquals(oed.region(), "GB");
    assertEquals(oed.variant(), "");
    assertEquals(oed.privateUse(), "");
    assertEquals(oed.compact(), "en-GB");
  }
}
