package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.List;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.TimePeriod;

public class DateMathSuiteTest extends CoverageSuite {

  @Test
  public void testDateMathDates() throws Exception {
    run("datemath-dates");
  }

  @Test
  public void testDateMathTimes() throws Exception {
    run("datemath-times");
  }

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
        List<Long> results = longArray(row.get("results"));
        for (int i = 0; i < dates.size(); i++) {
          int j = i * 2;
          long date = dates.get(i);
          CalendarDate start = en.Calendars.toGregorianDate(date, "UTC");
          CalendarDate e1 = start.add(period);
          CalendarDate e2 = start.subtract(period);
          long e1ex = results.get(j);
          long e2ex = results.get(j + 1);
//          System.out.println(e1.unixEpoch() + " " + e1ex);
//          System.out.println(e2.unixEpoch() + " " + e2ex);
          assertEquals(e1.unixEpoch(), e1ex, date + " " + period + " add");
          assertEquals(e2.unixEpoch(), e2ex, date + " " + period + " subtract");
          cases++;
        }
      }
    }

    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  protected static TimePeriod timePeriod(JsonElement elem, List<String> properties) {
    JsonArray arr = elem.getAsJsonArray();
    TimePeriod res = TimePeriod.build();
    for (int i = 0; i < properties.size(); i++) {
      String property = properties.get(i);
      Double val = doubleValue(arr.get(i));
      switch (property) {
        case "year":
          res.year(val);
          break;
        case "month":
          res.month(val);
          break;
        case "week":
          res.week(val);
          break;
        case "day":
          res.day(val);
          break;
        case "hour":
          res.hour(val);
          break;
        case "minute":
          res.minute(val);
          break;
        case "second":
          res.second(val);
          break;
        case "millis":
          res.millis(val);
          break;
      }
    }
    return res;
  }
}
