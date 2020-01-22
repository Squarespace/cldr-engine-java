package com.squarespace.cldrengine.locale;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.LocaleMatcher;

public class LocaleMatcherTest {

  @Test
  public void testBasic() {
    LocaleMatcher matcher = new LocaleMatcher("en, en_GB, zh, pt_AR, es-419");
    LocaleMatch m = matcher.match("en-AU");
    assertEquals(m.distance, 3);
    assertEquals(m.locale.id(), "en_GB");
  }

  @Test
  public void testConstructorArgs() {
    LocaleMatcher matcher = new LocaleMatcher("en \t en_GB \n , zh ");
    LocaleMatch m = matcher.match("en-AU");
    assertEquals(m.distance, 3);
    assertEquals(m.locale.id(), "en_GB");

    matcher = new LocaleMatcher(" en, pt_AR ', '\t en_GB");
    m = matcher.match("en-AU");
    assertEquals(m.distance, 3);
    assertEquals(m.locale.id(), "en_GB");
  }

  @Test
  public void testExtensions() {
    LocaleMatcher matcher = new LocaleMatcher("en, fr, fa, es");
    LocaleMatch match = matcher.match("en-AU-u-ca-persian");
    assertEquals(match.distance, 5);
    assertEquals(match.locale.id(), "en");
    assertEquals(match.locale.tag().compact(), "en-Latn-US-u-ca-persian");
  }

}
