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
import com.squarespace.cldrengine.api.CurrencyFormatOptions;
import com.squarespace.cldrengine.api.CurrencyFormatStyleType;
import com.squarespace.cldrengine.api.CurrencySymbolWidthType;
import com.squarespace.cldrengine.api.CurrencyType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;
import com.squarespace.cldrengine.api.RoundingModeType;

/**
 * Coverage testing for the numbers API.
 */
public class NumberSuiteTest extends CoverageSuite {

  @Test
  public void testCurrencyFormat() throws Exception {
    run("currencyformat");
  }

  @Test
  public void testCurrencyFormatCompact() throws Exception {
    run("currencyformat-compact");
  }

  @Test
  public void testDecimalFormat() throws Exception {
    run("decimalformat");
  }

  @Test
  public void testDecimalFormatNegzero() throws Exception {
    run("decimalformat-negzero");
  }

  @Test
  public void testDecimalFormatSig() throws Exception {
    run("decimalformat-sig");
  }

  @Test
  public void testDecimalFormatCompact() throws Exception {
    run("decimalformat-compact");
  }

  @Test
  public void testDecimalFormatCompactSig() throws Exception {
    run("decimalformat-compact-sig");
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    String method = null;
    List<String> locales = null;
    List<String> numbers = null;
    List<String> properties = null;
    List<CurrencyType> currencies = null;
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

          // Pre-construct CLDR instance for each locale
          locales = stringArray(row.get("locales"));
          cldrs = locales.stream().map(id -> CLDR.get(id)).collect(Collectors.toList());

          numbers = stringArray(row.get("numbers"));
          properties = stringArray(row.get("properties"));
          if (row.has("currencies")) {
            // Only defined for currency cases
            currencies = stringArray(row.get("currencies")).stream()
                .map(c -> CurrencyType.fromString(c)).collect(Collectors.toList());
          }
          header = true;
          continue;
        }

        switch (method) {

          case "formatDecimal": {
            JsonArray results = row.get("results").getAsJsonArray();
            DecimalFormatOptions opts = decimalFormatOptions(row.get("options"), properties);
            for (int i = 0; i < results.size(); i++) {
              CLDR cldr = cldrs.get(i);
              List<String> result = stringArray(results.get(i));
              for (int j = 0; j < numbers.size(); j++) {
                Decimal n = new Decimal(numbers.get(j));
                String expected = result.get(j);
                try {
                  String actual = cldr.Numbers.formatDecimal(n, opts);
                  Assert.assertEquals(actual, expected);
                  cases++;
                } catch (Exception ex) {
                  String msg = String.format("Unexpected error on inputs: n=%s opts=%s",
                      numbers.get(j), opts);
                  Assert.fail(msg, ex);
                  System.exit(1);
                }

              }
            }
            break;
          }

          case "formatCurrency":
            JsonArray results = row.get("results").getAsJsonArray();
            CurrencyFormatOptions opts = currencyFormatOptions(row.get("options"), properties);
            for (int i = 0; i < results.size(); i++) {
              CLDR cldr = cldrs.get(i);
              List<String> result = stringArray(results.get(i));
              for (int j = 0; j < numbers.size(); j++) {
                Decimal n = new Decimal(numbers.get(j));
                for (int k = 0; k < currencies.size(); k++) {
                  CurrencyType code = currencies.get(k);
                  String expected = result.get((j * currencies.size()) + k);
                  try {
                    String actual = cldr.Numbers.formatCurrency(n, code, opts);
                    Assert.assertEquals(actual, expected);
                    cases++;
                  } catch (Exception ex) {
                    String msg = String.format("Unexpected error on inputs: n=%s code=%s opts=%s",
                        numbers.get(j), code, opts);
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
    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  private static DecimalFormatOptions decimalFormatOptions(JsonElement json, List<String> properties) {
    JsonArray arr = json.getAsJsonArray();
    DecimalFormatOptions opts = DecimalFormatOptions.build();
    for (int i = 0; i < properties.size(); i++) {
      String property = properties.get(i);
      JsonElement raw = arr.get(i);
      switch (property) {
        case "divisor":
          opts.divisor(intValue(raw));
          break;
        case "negativeZero":
          opts.negativeZero(boolValue(raw));
          break;
        case "style":
          opts.style(typeValue(raw, DecimalFormatStyleType::fromString));
          break;
        case "round":
          opts.round(typeValue(raw, RoundingModeType::fromString));
          break;
        case "group":
          opts.group(boolValue(raw));
          break;
        case "minimumIntegerDigits":
          opts.minimumIntegerDigits(intValue(raw));
          break;
        case "minimumFractionDigits":
          opts.minimumFractionDigits(intValue(raw));
          break;
        case "maximumFractionDigits":
          opts.maximumFractionDigits(intValue(raw));
          break;
        case "minimumSignificantDigits":
          opts.minimumSignificantDigits(intValue(raw));
          break;
        case "maximumSignificantDigits":
          opts.maximumSignificantDigits(intValue(raw));
          break;
      }
    }
    return opts;
  }

  private static CurrencyFormatOptions currencyFormatOptions(JsonElement json, List<String> properties) {
    JsonArray arr = json.getAsJsonArray();
    CurrencyFormatOptions opts = CurrencyFormatOptions.build();
    for (int i = 0; i < properties.size(); i++) {
      String property = properties.get(i);
      JsonElement raw = arr.get(i);
      switch (property) {
        case "style":
          opts.style(typeValue(raw, CurrencyFormatStyleType::fromString));
          break;
        case "cash":
          opts.cash(boolValue(raw));
          break;
        case "symbolWidth":
          opts.symbolWidth(typeValue(raw, CurrencySymbolWidthType::fromString));
          break;
        case "divisor":
          opts.divisor(intValue(raw));
          break;
        case "group":
          opts.group(boolValue(raw));
          break;
      }
    }
    return opts;
  }

}
