package com.squarespace.cldrengine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

  public static JsonElement parse(String raw) {
    return JsonParser.parseReader(new StringReader(raw));
  }

  public static JsonObject loadJson(Class<?> cls, String path) throws IOException {
    try (InputStream stream = cls.getResourceAsStream(path)) {
      return (JsonObject) JsonParser.parseReader(new InputStreamReader(stream));
    }
  }

  public static String[] decodeArray(JsonElement elem) {
    JsonArray arr = elem.getAsJsonArray();
    int size = arr.size();
    String[] res = new String[size];
    for (int i = 0; i < size; i++) {
      res[i] = arr.get(i).getAsString();
    }
    return res;
  }

  public static Map<String, String> decodeObject(JsonElement elem) {
    JsonObject obj = elem.getAsJsonObject();
    Map<String, String> map = new HashMap<>();
    for (String key : obj.keySet()) {
      String val = obj.get(key).getAsString();
      map.put(key, val);
    }
    return map;
  }

}
