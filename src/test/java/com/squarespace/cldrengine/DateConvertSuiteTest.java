package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.List;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.Decimal;

public class DateConvertSuiteTest extends CoverageSuite {

  @Test
  public void testDateConvert() throws Exception {
    run("dateconv");
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    List<Long> dates = null;
    List<Long> incr = null;
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
          incr = longArray(row.get("incr"));
          header = true;
          continue;
        }

        JsonObject row = JsonParser.parseString(line).getAsJsonObject();
        long base = dates.get(row.get("i").getAsInt());
        long inc = incr.get(row.get("k").getAsInt());
        JsonArray arr = row.get("results").getAsJsonArray();

        CalendarDate d = en.Calendars.toGregorianDate(base + inc, "UTC");
        CalendarDate buddhist = en.Calendars.toBuddhistDate(d);
        CalendarDate iso8601 = en.Calendars.toISO8601Date(d);
        CalendarDate japanese = en.Calendars.toJapaneseDate(d);
        CalendarDate persian = en.Calendars.toPersianDate(d);

        String exBuddhist = arr.get(0).getAsString();
        String exIso8601 = arr.get(1).getAsString();
        String exJapanese = arr.get(2).getAsString();
        String exPersian = arr.get(3).getAsString();

        assertEquals(buddhist.toString(), exBuddhist,  "buddhist " + d + " incr " + inc);
        assertEquals(iso8601.toString(), exIso8601, "iso8601 " + d + " incr " + inc);
        assertEquals(japanese.toString(), exJapanese, "japanese " + d + " incr " + inc);
        assertEquals(persian.toString(), exPersian, "persian " + d + " incr " + inc);

        cases++;
        if ((cases % 100000) == 0) {
          System.out.println(name + " " + cases);
        }
      }
    }

    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

}
