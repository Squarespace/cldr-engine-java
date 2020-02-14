package com.squarespace.cldrengine.locale;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.LocaleMatcher;
import com.squarespace.cldrengine.utils.FileUtils;
import com.squarespace.cldrengine.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.ToString;

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

  @Test
  public void testMatchCases() throws Exception {
    for (MatchCase mc : loadCases()) {
      LocaleMatcher matcher = new LocaleMatcher(mc.supported);
      LocaleMatch match = matcher.match(mc.desired);
      assertEquals(match.locale.id(), mc.result, "case " + mc.lineno);
    }
  }

  protected List<MatchCase> loadCases() throws Exception {
    String raw = FileUtils.loadResource(LocaleMatcherTest.class, "locale-match-cases.txt");
    List<MatchCase> results = new ArrayList<>();
    int i = 0;
    try (BufferedReader reader = new BufferedReader(new StringReader(raw))) {
      for (;;) {
        i++;
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        String[] cols = line.trim().split("#");
        if (cols.length == 0) {
          continue;
        }
        line = cols[0].trim();
        if (StringUtils.isEmpty(line)) {
          continue;
        }
        List<String> parts = Arrays.stream(line.split(";")).map(s -> s.trim()).collect(Collectors.toList());
        results.add(new MatchCase(parts.get(0), parts.get(1), parts.get(2), i));
      }
    }
    return results;
  }

  @AllArgsConstructor
  @ToString
  static class MatchCase {
    public final String supported;
    public final String desired;
    public final String result;
    public final int lineno;
  }
}


