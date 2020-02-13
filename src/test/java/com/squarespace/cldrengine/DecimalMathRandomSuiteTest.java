package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Decimal;

public class DecimalMathRandomSuiteTest extends CoverageSuite {

  @Test
  public void testMath() throws Exception {
    run("math-random");
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    List<Decimal> numbers = null;
    boolean header = false;

    try (BufferedReader reader = getTestCase(name)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        if (!header) {
          JsonObject row = JsonParser.parseString(line).getAsJsonObject();
          numbers = stringArray(row.get("numbers"))
              .stream()
              .map(s -> new Decimal(s))
              .collect(Collectors.toList());
          header = true;
          continue;
        }

        JsonObject result = JsonParser.parseString(line).getAsJsonObject();
        int i = result.get("i").getAsInt();
        JsonArray results = result.get("results").getAsJsonArray();
        Decimal n = numbers.get(i);

        for (int j = 0; j < numbers.size(); j++) {
          Decimal m = numbers.get(j);

          int k = j * 4;
          String addex = results.get(k).getAsString();
          String subex = results.get(k + 1).getAsString();
          String mulex = results.get(k + 2).getAsString();
          String divex = results.get(k + 3).getAsString();

          String add = n.add(m).toString();
          String sub = n.subtract(m).toString();
          String mul = n.multiply(m, null).toString();
          String div = n.divide(m, null).toString();

          assertEquals(add, addex, n + " + " + m);
          assertEquals(sub, subex, n + " - " + m);
          assertEquals(mul, mulex, n + " * " + m);
          assertEquals(div, divex, n + " / " + m);
        }

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
