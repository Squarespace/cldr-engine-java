package com.squarespace.cldrengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squarespace.cldrengine.api.Decimal;

/**
 * High coverage by comparing output against the TypeScript library as
 * source of truth. The codegen tool generates files containing JSON-encoded
 * test cases, consisting of inputs and expected output. This package
 * reads those files, executes the corresponding API method and compares
 * the output.
 *
 * This allows us to achieve high test coverage at the API level without
 * having to duplicate all low-level test cases from the original library.
 */
public class CoverageSuite {

  public BufferedReader getTestCase(String name) throws IOException {
    Path root = Paths.get(System.getProperty("user.dir"), ".cldrsuite");
    Path path = root.resolve(name + ".txt");
    File file = path.toFile();
    if (!file.exists()) {
      throw new RuntimeException("Test suite must be generated using the codegen tool");
    }
    return new BufferedReader(new FileReader(file));
  }

  public static Boolean boolValue(JsonElement e) {
    return e.isJsonNull() ? null : e.getAsBoolean();
  }

  public static Integer intValue(JsonElement e) {
    return e.isJsonNull() ? null : e.getAsInt();
  }

  public static <T> T typeValue(JsonElement e, Function<String, T> f) {
    return e.isJsonNull() ? null : f.apply(e.getAsString());
  }

  public static List<Long> longArray(JsonElement json) {
    JsonArray arr = json.getAsJsonArray();
    List<Long> result = new ArrayList<>();
    for (int i = 0; i < arr.size(); i++) {
      Long value = arr.get(i).getAsLong();
      result.add(value);
    }
    return result;
  }

  public static List<String> stringArray(JsonElement json) {
    JsonArray arr = json.getAsJsonArray();
    List<String> result = new ArrayList<>();
    for (int i = 0; i < arr.size(); i++) {
      String value = arr.get(i).getAsString();
      result.add(value);
    }
    return result;
  }

  public static List<Decimal> decimalArray(JsonElement json) {
    List<Decimal> result = new ArrayList<>();
    JsonArray arr = json.getAsJsonArray();
    for (int i = 0; i < arr.size(); i++) {
      JsonArray raw = arr.get(i).getAsJsonArray();
      long[] data = longArray(raw.get(0)).stream().mapToLong(e -> e).toArray();
      int sign = raw.get(1).getAsInt();
      int exp = raw.get(2).getAsInt();
      int flag = raw.get(3).getAsInt();
      Decimal n = new Decimal(sign, exp, data, flag);
      result.add(n);
    }
    return result;
  }

}
