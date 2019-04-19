package com.squarespace.cldrengine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResourceUtil {

  private static final JsonParser JSON_PARSER = new JsonParser();

  public static JsonElement parse(String raw) {
    return JSON_PARSER.parse(new StringReader(raw));
  }

  public static JsonObject load(Class<?> cls, String path) throws IOException {
    try (InputStream stream = cls.getResourceAsStream(path)) {
      return (JsonObject) JSON_PARSER.parse(new InputStreamReader(stream));
    }
  }

}
