package com.squarespace.cldrengine.locale;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.LanguageTag;

public class LanguageTagParserTest {

  @Test
  public void testBasics() {
    assertEquals(compact("!+"), "und");
    assertEquals(compact("en-US"), "en-US");
    assertEquals(compact("en-Latn-US"), "en-Latn-US");
    assertEquals(compact("en-latn-us"), "en-Latn-US");
    assertEquals(compact("xxxxxxxxxxx"), "und");

    assertEquals(expanded("!+#%!"), "und-Zzzz-ZZ");
    assertEquals(expanded("fr"), "fr-Zzzz-ZZ");
    assertEquals(expanded("en-US"), "en-Zzzz-US");
  }

  @Test
  public void testGrandfathered() {
    assertEquals(compact("i-klingon"), "tlh");
  }

  @Test
  public void testExtlangSubtags() {
    // Extlangs are currently parsed but ignored.
    assertEquals(compact("ar-aao"), "ar");
    assertEquals(compact("en-abc-def-us"), "en-US");
  }

  @Test
  public void testPrivateUse() {
    assertEquals(compact("x-mytag"), "und-x-mytag");
    assertEquals(privateUse("x-foo-x-bar-baz"), "x-foo-x-bar-baz");

    assertEquals(compact("zh-x-foobar"), "zh-x-foobar");
    assertEquals(privateUse("zh-x-foobar"), "x-foobar");

    assertEquals(compact("zh-x-foo-x-bar-baz"), "zh-x-foo-x-bar-baz");
    assertEquals(privateUse("zh-x-foo-x-bar-baz"), "x-foo-x-bar-baz");
  }

  @Test
  public void testPrivateUseIncomplete() {
    assertEquals(compact("x-"), "und");
    assertEquals(privateUse("x-"), "");

    assertEquals(compact("x--x--"), "und");
    assertEquals(privateUse("x--x--"), "");
  }

  @Test
  public void testExtensions() {
    assertEquals(compact("en-US-u-cu-usd"), "en-US-u-cu-usd");
    assertEquals(compact("fr-u-ca-islamic"), "fr-u-ca-islamic");
    assertEquals(expanded("fr-u-ca-islamic"), "fr-Zzzz-ZZ-u-ca-islamic");
    assertEquals(extensions("fr-u-ca-islamic-u_co_phonebk"), new HashMap<String, List<String>>() {{
      this.put("u", Arrays.asList(
         "ca-islamic", "co-phonebk"
      ));
    }});
  }

  private String compact(String s) {
    return parse(s).compact();
  }

  private String expanded(String s) {
    return parse(s).expanded();
  }

  private String privateUse(String s) {
    return parse(s).privateUse();
  }

  private Map<String, List<String>> extensions(String s) {
    return parse(s).extensions();
  }

  private LanguageTag parse(String s) {
    return LanguageTagParser.parse(s);
  }
}
