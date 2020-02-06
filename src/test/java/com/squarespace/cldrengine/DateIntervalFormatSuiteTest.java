package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateIntervalFormatOptions;
import com.squarespace.cldrengine.api.Decimal;

public class DateIntervalFormatSuiteTest extends CoverageSuite {

  @Test
  public void testDateIntervalFormat() throws Exception {
    run("dateinterval");
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    String method = null;
    List<String> locales = null;
    List<CLDR> cldrs = null;
    List<DateIntervalFormatOptions> options = null;
    List<Long> dates = null;
    List<String> zones = null;
    boolean header = false;

    try (BufferedReader reader = getTestCase(name)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        if (!header) {
          JsonObject row = JsonParser.parseString(line).getAsJsonObject();
          method = row.get("method").getAsString();
          locales = stringArray(row.get("locales"));
          cldrs = locales.stream().map(id -> CLDR.get(id)).collect(Collectors.toList());
          dates = longArray(row.get("dates"));
          zones = stringArray(row.get("zones"));
          options = typeArray(row.get("options"), DateIntervalFormatSuiteTest::dateIntervalOptions);
          header = true;
          continue;
        }

        switch (method) {
          case "formatDateInterval": {
            JsonObject row = JsonParser.parseString(line).getAsJsonObject();
            int i = row.get("i").getAsInt();
            int j = row.get("j").getAsInt();
            int k = row.get("k").getAsInt();
            int m = row.get("m").getAsInt();
            JsonArray results = row.get("results").getAsJsonArray();

            CLDR cldr = cldrs.get(i);
            String zoneId = zones.get(m);
            long startEpoch = dates.get(j).longValue();
            long endEpoch = dates.get(k).longValue();
            CalendarDate start = cldr.Calendars.toGregorianDate(startEpoch, zoneId);
            CalendarDate end = cldr.Calendars.toGregorianDate(endEpoch, zoneId);
            for (int n = 0; n < options.size(); n++) {
              DateIntervalFormatOptions opts = options.get(n);
              String expected = results.get(n).getAsString();
              String actual = cldr.Calendars.formatDateInterval(start, end, opts);
              assertEquals(actual, expected, "case " + cases + " id=" + locales.get(i) +
                  " " + start + "  " + end + " " + opts);
              cases++;
            }
            break;
          }
        }
      }
    }
    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  private static DateIntervalFormatOptions dateIntervalOptions(JsonElement elem) {
    JsonObject obj = elem.getAsJsonObject();
    DateIntervalFormatOptions opts = DateIntervalFormatOptions.build();
    for (String key : obj.keySet()) {
      switch (key) {
        case "skeleton":
          opts.skeleton(stringValue(obj.get(key)));
          break;
      }

    }
    return opts;
  }
}
