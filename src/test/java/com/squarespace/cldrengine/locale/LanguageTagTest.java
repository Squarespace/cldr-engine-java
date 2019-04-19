package com.squarespace.cldrengine.locale;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

public class LanguageTagTest {

  @Test
  public void testBasic() {
    LanguageTag tag = new LanguageTag("", "", "");
    assertEquals(tag.compact(), "und");

    tag = new LanguageTag("en", "", "");
    assertEquals(tag.compact(), "en");

    tag = new LanguageTag("en", "", "", "",
        extensions().add("u", "nu-latn").build(), "");
    assertEquals(tag.compact(), "en-u-nu-latn");

    tag = new LanguageTag("en", "", "", "",
        extensions().add("u", "nu-latn", "ca-gregory").build(), "");
    assertEquals(tag.compact(), "en-u-nu-latn-ca-gregory");

    tag = new LanguageTag("en", "", "", "", extensions().add("u").build(), "");
    assertEquals(tag.compact(), "en");

    // NOTE: constructing language tags directly allows creation
    // of malformed tags. validation and normalization is done in
    // the language tag parser, not the constructor.
    tag = new LanguageTag("en", "", "", "", null, "q");
    assertEquals(tag.compact(), "en-q");
  }

  static class Extensions {
    final Map<String, List<String>> map = new HashMap<>();

    public Extensions add(String tag) {
      this.map.put(tag, null);
      return this;
    }

    public Extensions add(String tag, String ...values) {
      this.map.put(tag, Arrays.asList(values));
      return this;
    }

    public Map<String, List<String>> build() {
      return this.map;
    }
  }

  static Extensions extensions() {
    return new Extensions();
  }
}
