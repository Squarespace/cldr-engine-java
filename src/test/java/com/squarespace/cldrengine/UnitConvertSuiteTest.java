package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.units.conversion.Factors;
import com.squarespace.cldrengine.units.conversion.UnitConversion;
import com.squarespace.cldrengine.units.conversion.UnitFactors;

public class UnitConvertSuiteTest extends CoverageSuite {

  @Test
  public void testUnitConvert() throws Exception {
    run("unit-convert");
  }

  protected void run(String testCase) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    Map<String, UnitFactors> factorsets = new HashMap<>();
    factorsets.put("ACCELERATION", new UnitFactors(Factors.ACCELERATION));
    factorsets.put("ANGLE", new UnitFactors(Factors.ANGLE));
    factorsets.put("AREA", new UnitFactors(Factors.AREA));
    factorsets.put("CONSUMPTION", new UnitFactors(Factors.CONSUMPTION));
    factorsets.put("DIGITAL", new UnitFactors(Factors.DIGITAL));
    factorsets.put("DIGITAL_DECIMAL", new UnitFactors(Factors.DIGITAL_DECIMAL));
    factorsets.put("DURATION", new UnitFactors(Factors.DURATION));
    factorsets.put("ELECTRIC", new UnitFactors(Factors.ELECTRIC));
    factorsets.put("ENERGY", new UnitFactors(Factors.ENERGY));
    factorsets.put("FORCE", new UnitFactors(Factors.FORCE));
    factorsets.put("FREQUENCY", new UnitFactors(Factors.FREQUENCY));
    factorsets.put("GRAPHICS_PER", new UnitFactors(Factors.GRAPHICS_PER));
    factorsets.put("GRAPHICS_PIXEL", new UnitFactors(Factors.GRAPHICS_PIXEL));
    factorsets.put("LENGTH", new UnitFactors(Factors.LENGTH));
    factorsets.put("MASS", new UnitFactors(Factors.MASS));
    factorsets.put("POWER", new UnitFactors(Factors.POWER));
    factorsets.put("PRESSURE", new UnitFactors(Factors.PRESSURE));
    factorsets.put("SPEED", new UnitFactors(Factors.SPEED));
    factorsets.put("TORQUE", new UnitFactors(Factors.TORQUE));
    factorsets.put("VOLUME", new UnitFactors(Factors.VOLUME));
    factorsets.put("VOLUME_UK", new UnitFactors(Factors.VOLUME_UK));

    try (BufferedReader reader = getTestCase(testCase)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        JsonObject row = JsonParser.parseString(line).getAsJsonObject();
        String name = row.get("name").getAsString();
        UnitType src = UnitType.fromString(row.get("src").getAsString());
        UnitType dst = UnitType.fromString(row.get("dst").getAsString());
        List<String> pathex = stringArray(row.get("path"));
        List<String> factorsex = stringArray(row.get("factors"));

        UnitFactors set = factorsets.get(name);
        UnitConversion actual = set.get(src, dst);
        assertNotNull(actual, cases + " " + name + " " + src + " -> " + dst);

        List<String> path = actual.path.stream().map(e -> e.value()).collect(Collectors.toList());
        List<String> factors = actual.factors.stream().map(e -> e.toString()).collect(Collectors.toList());

        assertEquals(path, pathex, cases + " " + name + " " + src + " -> " + dst);
        assertEquals(factors, factorsex, cases + " " + name + " " + src + " -> " + dst);

        cases++;
      }
    }

    System.out.println(testCase + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }
}
