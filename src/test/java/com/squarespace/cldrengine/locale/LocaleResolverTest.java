package com.squarespace.cldrengine.locale;

import static com.squarespace.cldrengine.locale.LocaleResolver.addLikelySubtags;
import static com.squarespace.cldrengine.locale.LocaleResolver.removeLikelySubtags;
import static com.squarespace.cldrengine.locale.LocaleResolver.resolve;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.LanguageTag;

public class LocaleResolverTest {

  @Test
  public void testResolver() {
    // ISO 3166-1 3ALPHA codes replaced at parse time.
    assertEquals(resolve(tag("en-AAA")), tag("aaa-Latn-AA"));
    assertEquals(resolve(tag("en-eng-eng-AAA")), tag("en-Latn-AA"));
    assertEquals(resolve(tag("en-aaa")), tag("aaa-Latn-AA"));
  }

  @Test
  public void testAddLikelySubtags() {
    assertEquals(addLikelySubtags(tag("en")), tag("en-Latn-US"));
    assertEquals(addLikelySubtags("en"), tag("en-Latn-US"));
  }

  @Test
  public void testRemoveLikelySubtags() {
    assertEquals(removeLikelySubtags("en-Latn-US"), tag("en"));
    assertEquals(removeLikelySubtags("en-US"), tag("en"));
    assertEquals(removeLikelySubtags("en-Latn"), tag("en"));

    assertEquals(removeLikelySubtags(tag("en-Latn-US")), tag("en"));
    assertEquals(removeLikelySubtags(tag("en-US")), tag("en"));
    assertEquals(removeLikelySubtags(tag("en-Latn")), tag("en"));

    assertEquals(removeLikelySubtags(tag("en-Latn-AU")), tag("en-AU"));

    assertEquals(removeLikelySubtags(tag("zh-Hant-TW")), tag("zh-TW"));
    assertEquals(removeLikelySubtags(tag("zh-CN")), tag("zh"));

    assertEquals(removeLikelySubtags(tag("az-Arab")), tag("az-IR"));
    assertEquals(removeLikelySubtags(tag("az-IR")), tag("az-IR"));

    assertEquals(removeLikelySubtags(tag("sr")), tag("sr"));
    assertEquals(removeLikelySubtags(tag("sr-Cyrl")), tag("sr"));
    assertEquals(removeLikelySubtags(tag("sr-Cyrl-RS")), tag("sr"));

    assertEquals(removeLikelySubtags(tag("sr-Cyrl-BA")), tag("sr-BA"));
    assertEquals(removeLikelySubtags(tag("sr-Latn-BA")), tag("sr-Latn-BA"));
    assertEquals(removeLikelySubtags(tag("sr-Latn-RS")), tag("sr-Latn"));

    assertEquals(removeLikelySubtags(tag("be-Cyrl-BY")), tag("be"));
    assertEquals(removeLikelySubtags(tag("be-BY")), tag("be"));
    assertEquals(removeLikelySubtags(tag("und-BY")), tag("be"));
  }

  private static LanguageTag tag(String t) {
    return LanguageTagParser.parse(t);
  }
}
