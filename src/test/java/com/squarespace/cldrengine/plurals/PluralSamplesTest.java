package com.squarespace.cldrengine.plurals;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.utils.FileUtils;

import lombok.AllArgsConstructor;

public class PluralSamplesTest {

  private final Map<String, List<TestCase>> cases = loadCases();

  @Test
  public void testSamples() {
    List<String> langs = new ArrayList<>(cases.keySet());
    langs.sort((a, b) -> a.compareTo(b));
    for (String lang : langs) {
      String[] raw = lang.split("-'");
      String language = raw[0];
      String region = raw.length > 1 ? raw[1] : null;
      PluralRules pluralRules = Plurals.get(language, region);

      for (TestCase c : cases.get(lang)) {
        for (String sample : c.samples) {
          Decimal n = new Decimal(sample);
          PluralType actual = c.type.equals("cardinals") ? pluralRules.cardinal(n) : pluralRules.ordinal(n);
          boolean result = actual.value().equals(c.category);
          Assert.assertTrue(result,
              String.format("Expected language '%s' number '%s' to have category '%s' but got '%s'",
                  lang, sample, c.category, actual));
        }
      }
    }
  }

  @AllArgsConstructor
  private static class TestCase {
    public final String type;
    public final String category;
    public final String[] samples;
  }

  @BeforeClass
  protected Map<String, List<TestCase>> loadCases() {
    try {
      Map<String, List<TestCase>> cases = new HashMap<>();
      String raw = FileUtils.loadResource(PluralSamplesTest.class, "samples.txt");
      for (String line : raw.split("\n")) {
        line = line.trim();
        if (isEmpty(line) || line.startsWith("#")) {
          continue;
        }

        String[] row = line.split("\\s+");
        String type = row[0];
        String lang = row[1];
        String category = row[2];
        String[] samples = row[3].split(",");

        List<TestCase> list = cases.get(lang);
        if (list == null) {
          list = new ArrayList<>();
          cases.put(lang, list);
        }
        list.add(new TestCase(type, category, samples));
      }
      return cases;
    } catch (FileNotFoundException e) {
      throw new TestNGException(e.toString());
    }
  }

}
