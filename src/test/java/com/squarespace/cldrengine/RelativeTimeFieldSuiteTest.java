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
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.RelativeTimeFieldFormatOptions;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;

public class RelativeTimeFieldSuiteTest extends CoverageSuite {

  @Test
  public void testRelativeTimeField() throws Exception {
    run("relativetime-field");
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    List<String> locales = null;
    List<Decimal> numbers = null;
    List<RelativeTimeFieldFormatOptions> options = null;
    List<RelativeTimeFieldType> fields = null;
    List<CLDR> cldrs = null;
    boolean header = false;

    try (BufferedReader reader = getTestCase(name)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        if (!header) {
          // Decode header row
          JsonObject row = JsonParser.parseString(line).getAsJsonObject();
          locales = stringArray(row.get("locales"));
          cldrs = locales.stream().map(id -> CLDR.get(id)).collect(Collectors.toList());
          numbers = stringArray(row.get("numbers")).stream().map(s -> new Decimal(s)).collect(Collectors.toList());
          fields = stringArray(row.get("fields"))
              .stream()
              .map(s -> RelativeTimeFieldType.fromString(s))
              .collect(Collectors.toList());
          options = typeArray(row.get("options"), RelativeTimeFieldSuiteTest::options);
          header = true;
          continue;
        }

        JsonObject row = JsonParser.parseString(line).getAsJsonObject();
        int i = row.get("i").getAsInt();
        int j = row.get("j").getAsInt();
        int k = row.get("k").getAsInt();
        JsonArray results = row.get("results").getAsJsonArray();

        CLDR cldr = cldrs.get(i);
        Decimal num = numbers.get(j);
        RelativeTimeFieldFormatOptions opts = options.get(k);

        for (int m = 0; m < fields.size(); m++) {
          RelativeTimeFieldType field = fields.get(m);
          String actual = cldr.Calendars.formatRelativeTimeField(num, field, opts);
          String expected = results.get(m).getAsString();
          Assert.assertEquals(actual, expected, num + " " + field + " " + opts);
          cases++;
        }
      }
    }
    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  private static RelativeTimeFieldFormatOptions options(JsonElement elem) {
    JsonObject obj = elem.getAsJsonObject();
    RelativeTimeFieldFormatOptions opts = RelativeTimeFieldFormatOptions.build();
    for (String key : obj.keySet()) {
      JsonElement raw = obj.get(key);
      switch (key) {
        case "context":
          opts.context(ContextType.fromString(raw.getAsString()));
          break;
        case "alwaysNow":
          opts.alwaysNow(raw.getAsBoolean());
          break;
        case "numericOnly":
          opts.numericOnly(raw.getAsBoolean());
          break;
        case "width":
          opts.width(DateFieldWidthType.fromString(raw.getAsString()));
          break;
      }
    }
    return opts;
  }
}
