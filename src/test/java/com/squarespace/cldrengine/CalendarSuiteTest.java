package com.squarespace.cldrengine;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.CalendarType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.FormatWidthType;

public class CalendarSuiteTest extends CoverageSuite {

  @Test
  public void testDateFormat() throws Exception {
    run("dateformat");
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    String method = null;
    List<String> locales = null;
    List<String> properties = null;
    List<Long> dates = null;
    List<String> zones = null;
    List<CLDR> cldrs = null;
    boolean header = false;

    try (BufferedReader reader = getTestCase(name)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        JsonObject row = JsonParser.parseString(line).getAsJsonObject();
        if (!header) {
          // Decode header row
          method = row.get("method").getAsString();
          locales = stringArray(row.get("locales"));
          cldrs = locales.stream().map(id -> CLDR.get(id)).collect(Collectors.toList());
          dates = longArray(row.get("dates"));
          zones = stringArray(row.get("zones"));
          properties = stringArray(row.get("properties"));
          header = true;
          continue;
        }

        switch (method) {
          case "formatDate": {

            JsonArray results = row.get("results").getAsJsonArray();
            DateFormatOptions opts = dateFormatOptions(row.get("options"), properties);
            for (int i = 0; i < results.size(); i++) {
              CLDR cldr = cldrs.get(i);
              List<String> result = stringArray(results.get(i));
              for (int j = 0; j < dates.size(); j++) {
                long epoch = dates.get(j);
                for (int k = 0; k < zones.size(); k++) {
                  String zoneId = zones.get(k);
                  String expected = result.get((j * zones.size()) + k);
                  CalendarDate date = cldr.Calendars.toGregorianDate(epoch, zoneId);
                  try {
                    String actual = cldr.Calendars.formatDate(date, opts);
                    Assert.assertEquals(actual, expected);
                    cases++;
                  } catch (Exception ex) {
                    String msg = String.format("Unexpected error on inputs: epoch=%s zone=%s opts=%s",
                        epoch, zoneId, opts);
                    Assert.fail(msg, ex);
                    System.exit(1);
                  }
                }
              }
            }
            break;
          }

        }
      }
    }
    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  private static DateFormatOptions dateFormatOptions(JsonElement json, List<String> properties) {
    JsonArray arr = json.getAsJsonArray();
    DateFormatOptions opts = DateFormatOptions.build();
    for (int i = 0; i < properties.size(); i++) {
      String property = properties.get(i);
      JsonElement raw = arr.get(i);
      switch (property) {
        case "ca":
          opts.calendar(typeValue(raw, CalendarType::fromString));
          break;
        case "datetime":
          opts.datetime(typeValue(raw, FormatWidthType::fromString));
          break;
        case "date":
          opts.date(typeValue(raw, FormatWidthType::fromString));
          break;
        case "time":
          opts.time(typeValue(raw, FormatWidthType::fromString));
          break;
        case "skeleton":
          opts.skeleton(raw.getAsString());
          break;
        case "context":
          opts.context(typeValue(raw, ContextType::fromString));
          break;
      }
    }
    return opts;
  }

}
