package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;

public class DateMathSuiteTest extends CoverageSuite {

  @Test
  public void testDateMathDates() throws Exception {
    run("datemath-dates");
  }

  @Test
  public void testDateMathTimes() throws Exception {
    run("datemath-times");
  }

  private static final TimePeriodField FIELDS[] = new TimePeriodField[] {
    TimePeriodField.YEAR,
    TimePeriodField.MONTH,
    TimePeriodField.WEEK,
    TimePeriodField.DAY,
    TimePeriodField.HOUR,
    TimePeriodField.MINUTE,
    TimePeriodField.SECOND,
    TimePeriodField.MILLIS
  };

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    List<String> properties = null;
    List<Long> dates = null;
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
          dates = longArray(row.get("dates"));
          properties = stringArray(row.get("properties"));
          header = true;
          continue;
        }

        JsonObject row = JsonParser.parseString(line).getAsJsonObject();
        TimePeriod period = timePeriod(row.get("options"), properties);
        JsonArray results = row.get("results").getAsJsonArray();
        for (int i = 0; i < dates.size(); i++) {
          int j = i * (4 + FIELDS.length);
          long date = dates.get(i);
          CalendarDate start = en.Calendars.toGregorianDate(date, "UTC");
          CalendarDate e1 = start.add(period);
          CalendarDate e2 = start.subtract(period);
          long e1ex = results.get(j).getAsLong();
          long e2ex = results.get(j + 1).getAsLong();
          TimePeriod d1ex = decodePeriod(results.get(j + 2));
          TimePeriod d2ex = decodePeriod(results.get(j + 3));
          TimePeriod d1 = start.difference(e1, null);
          TimePeriod d2 = start.difference(e2, null);

          assertEquals(e1.unixEpoch(), e1ex, date + " " + period + " add");
          assertEquals(e2.unixEpoch(), e2ex, date + " " + period + " subtract");
          assertEquals(d1.toString(), d1ex.toString());
          assertEquals(d2.toString(), d2ex.toString());

          for (int k = 0; k < FIELDS.length; k++) {
            TimePeriod d3ex = decodePeriod(results.get(j + 4 + k));
            TimePeriod d3 = start.difference(e1, Arrays.asList(FIELDS[k]));
            assertEquals(d3.toString(), d3ex.toString(),
                start.unixEpoch() + " " + e1.unixEpoch() + " " + FIELDS[k]);
          }

          cases++;
          if ((cases % 100000) == 0) {
            System.out.println(name + " " + cases);
          }
        }
      }
    }

    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  protected static TimePeriod decodePeriod(JsonElement elem) {
    JsonObject obj = elem.getAsJsonObject();
    TimePeriod res = TimePeriod.build();
    for (String key : obj.keySet()) {
      updatePeriod(res, key, obj.get(key).getAsDouble());
    }
    return res;
  }

  protected static TimePeriod timePeriod(JsonElement elem, List<String> properties) {
    JsonArray arr = elem.getAsJsonArray();
    TimePeriod res = TimePeriod.build();
    for (int i = 0; i < properties.size(); i++) {
      String property = properties.get(i);
      Double val = doubleValue(arr.get(i));
      updatePeriod(res, property, val);
    }
    return res;
  }

  protected static void updatePeriod(TimePeriod p, String property, Double val) {
    switch (property) {
      case "year":
        p.year(val);
        break;
      case "month":
        p.month(val);
        break;
      case "week":
        p.week(val);
        break;
      case "day":
        p.day(val);
        break;
      case "hour":
        p.hour(val);
        break;
      case "minute":
        p.minute(val);
        break;
      case "second":
        p.second(val);
        break;
      case "millis":
        p.millis(val);
        break;
    }
  }
}
