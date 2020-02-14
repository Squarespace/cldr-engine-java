package com.squarespace.cldrengine.locale;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.utils.FileUtils;
import com.squarespace.cldrengine.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.ToString;

public class LanguageDistanceTest {

  @Test
  public void testLanguageDistance() throws Exception {
    for (DistanceCase dc : loadCases()) {
      LanguageTag desired = LocaleResolver.resolve(dc.desired);
      LanguageTag supported = LocaleResolver.resolve(dc.supported);
      int distance = DistanceTable.distance(desired, supported);
      assertEquals(distance, dc.distanceDS);

      distance = DistanceTable.distance(supported, desired);
      assertEquals(distance, dc.distanceSD);
    }
  }

  protected List<DistanceCase> loadCases() throws Exception {
    String raw = FileUtils.loadResource(LocaleMatcherTest.class, "locale-distance-cases.txt");
    List<DistanceCase> results = new ArrayList<>();
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
        int distanceDS = Integer.parseInt(parts.get(2));
        int distanceSD = parts.size() == 3 ? distanceDS : Integer.parseInt(parts.get(3));
        results.add(new DistanceCase(parts.get(0), parts.get(1), distanceDS, distanceSD, i));
      }
    }
    return results;
  }

  @AllArgsConstructor
  @ToString
  static class DistanceCase {
    public final String supported;
    public final String desired;
    public final int distanceDS;
    public final int distanceSD;
    public final int lineno;
  }
}
