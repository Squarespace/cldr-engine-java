package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.CurrencyDisplayNameOptions;
import com.squarespace.cldrengine.api.CurrencyType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DisplayNameOptions;
import com.squarespace.cldrengine.api.UnitLength;
import com.squarespace.cldrengine.api.UnitType;

public class NamesSuiteTest extends CoverageSuite {

  @Test
  public void testNames() throws Exception {
    run("display-names");
  }

  protected void run(String testCase) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    Decimal ZERO = new Decimal(0);
    Decimal ONE = new Decimal(1);
    Decimal TWO = new Decimal(2);

    List<String> locales = null;
    List<CLDR> cldrs = null;
    boolean header = false;

    DisplayNameOptions displayNameOpts = DisplayNameOptions.build()
        .type(AltType.LONG)
        .context(ContextType.BEGIN_SENTENCE);

    CurrencyDisplayNameOptions currencyOptions = CurrencyDisplayNameOptions.build()
        .context(ContextType.BEGIN_SENTENCE);

    try (BufferedReader reader = getTestCase(testCase)) {
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
          header = true;
          continue;
        }

        JsonObject row = JsonParser.parseString(line).getAsJsonObject();
        String name = row.get("name").getAsString();
        int i = row.get("i").getAsInt();
        String id = row.get("id").getAsString();
        String expected = row.get("result").getAsString();

        String actual = null;

        CLDR cldr = cldrs.get(i);
        switch (name) {
          case "currency-display":
            actual = cldr.Numbers.getCurrencyDisplayName(CurrencyType.fromString(id), currencyOptions);
            break;
          case "currency-plural-0":
            actual = cldr.Numbers.getCurrencyPluralName(ZERO, CurrencyType.fromString(id), currencyOptions);
            break;
          case "currency-plural-1":
            actual = cldr.Numbers.getCurrencyPluralName(ONE, CurrencyType.fromString(id), currencyOptions);
            break;
          case "currency-plural-2":
            actual = cldr.Numbers.getCurrencyPluralName(TWO, CurrencyType.fromString(id), currencyOptions);
            break;
          case "language-display":
            actual = cldr.General.getLanguageDisplayName(id, displayNameOpts);
            break;
          case "script-display":
            actual = cldr.General.getScriptDisplayName(id, displayNameOpts);
            break;
          case "region-display":
            actual = cldr.General.getRegionDisplayName(id, displayNameOpts);
            break;
          case "unit-display":
            actual = cldr.Units.getUnitDisplayName(UnitType.fromString(id), UnitLength.LONG);
            break;
        }

        assertEquals(actual, expected, name + " " + id);

        cases++;
        if ((cases % 100000) == 0) {
          System.out.println(name + " " + cases);
        }

      }
    }

    System.out.println(testCase + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");

  }

}
