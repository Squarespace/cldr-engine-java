package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.MathContext;
import com.squarespace.cldrengine.api.RoundingModeType;

public class DecimalMathSuiteTest extends CoverageSuite {

  @Test
  public void testMath() throws Exception {
    run("math");
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    List<Decimal> numbers = null;
    List<MathContext> contexts = null;
    boolean header = false;

    try (BufferedReader reader = getTestCase(name)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        if (!header) {
          JsonObject row = JsonParser.parseString(line).getAsJsonObject();
          numbers = decimalArray(row.get("numbers"));
          contexts = contextArray(row.get("contexts"));
          header = true;
          continue;
        }

        JsonArray result = JsonParser.parseString(line).getAsJsonArray();

        // Perform operations
        int i = result.get(0).getAsInt();
        int j = result.get(1).getAsInt();
        int k = result.get(2).getAsInt();

        Decimal n = numbers.get(i);
        Decimal m = numbers.get(j);
        MathContext c = contexts.get(k);

        String add = n.add(m).toString();
        String addex = result.get(3).getAsString();
        assertEquals(add, addex, n + " + " + m + " :: " + c);

        String sub = n.subtract(m).toString();
        String subex = result.get(4).getAsString();
        assertEquals(sub, subex, n + " - " + m + " :: " + c);

        String mul = n.multiply(m, c).toString();
        String mulex = result.get(5).getAsString();
        assertEquals(mul, mulex, n + " * " + m + " :: " + c);

        String div = n.divide(m, c).toString();
        String divex = result.get(6).getAsString();
        assertEquals(div, divex, n + " / " + m + " :: " + c);

        cases++;
        if (cases % 100000 == 0) {
          System.out.println(name + " " + cases + " cases");
        }
      }
    }
    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  protected List<MathContext> contextArray(JsonElement json) {
    JsonArray arr = json.getAsJsonArray();
    List<MathContext> result = new ArrayList<>();
    for (int i = 0; i < arr.size(); i++) {
      JsonElement elem = arr.get(i);
      if (elem.isJsonNull() ) {
        result.add(null);
        continue;
      }
      JsonObject obj = arr.get(i).getAsJsonObject();
      MathContext ctx = MathContext.build();
      for (String key : obj.keySet()) {
        switch (key) {
          case "precision":
            ctx.precision(obj.get(key).getAsInt());
            break;
          case "scale":
            ctx.scale(obj.get(key).getAsInt());
            break;
          case "round":
            String round = obj.get(key).getAsString();
            ctx.round(RoundingModeType.fromString(round));
            break;
        }
      }
      result.add(ctx);
    }
    return result;
  }
}
