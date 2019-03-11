package com.squarespace.cldr2.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldr2.internal.Bundle;
import com.squarespace.cldr2.internal.LanguageTag;
import com.squarespace.cldr2.internal.Meta;
import com.squarespace.cldr2.internal.Pair;
import com.squarespace.cldr2.internal.StringBundle;

/**
 * Quick verification that the schema generator's offsets are in sync with
 * the resource pack.
 */
public class Sketch {

  private static final JsonParser JSON_PARSER = new JsonParser();

  public static void main(String[] args) throws Exception {
    JsonObject root = load("en.json");

    LanguageTag tag = new LanguageTag() {
      @Override
      public String language() {
        return "en";
      }
      @Override
      public String script() {
        return "Latn";
      }
      @Override
      public String region() {
        return "US";
      }
    };

    JsonObject scripts = root.get("scripts").getAsJsonObject();
    JsonObject latn = scripts.get("Latn").getAsJsonObject();
    String[] strings = latn.get("strings").getAsString().split("\t");
    StringBundle bundle = new StringBundle(
        "en", tag, strings, new String[] {}, new HashMap<>());

    String s;
    Pair<String, Integer> d;

    s = Meta.SCHEMA.Names.scripts.displayName.get(bundle, "Latn");
    expect(s, "Latin");

    s = Meta.SCHEMA.Names.regions.displayName.get(bundle, "none", "US");
    expect(s, "United States");

    s = Meta.SCHEMA.Numbers.numberSystems.get(bundle, "default");
    expect(s, "latn");

    d = Meta.SCHEMA.Numbers.numberSystem.get("latn").decimalFormats.long_.get(bundle, "other", 11);
    expect(d._1, "00 billion");

    s = Meta.SCHEMA.Numbers.numberSystem.get("latn").scientificFormat.get(bundle);
    expect(s, "#E0");

    s = Meta.SCHEMA.DateFields.relativeTimes.wide.displayName.get(bundle, "quarter");
    expect(s, "quarter");

    s = Meta.SCHEMA.DateFields.relativeTimes.wide.next.get(bundle, "day");
    expect(s, "tomorrow");

    s = Meta.SCHEMA.ListPatterns.and.get(bundle, "two");
    expect(s, "{0} and {1}");

    s = Meta.SCHEMA.Buddhist.eras.get(bundle, "names", "0");
    expect(s, "BE");

    s = Meta.SCHEMA.Gregorian.eras.get(bundle, "names", "0");
    expect(s, "Before Christ");

    s = Meta.SCHEMA.Gregorian.eras.get(bundle, "names", "1");
    expect(s, "Anno Domini");

    s = Meta.SCHEMA.TimeZones.metaZones.short_.get(bundle, "daylight", "America_Eastern");
    expect(s, "Eastern Daylight Time");

    s = Meta.SCHEMA.TimeZones.exemplarCity.get(bundle, "America/New_York");
    expect(s, "New York");

    s = Meta.SCHEMA.Currencies.displayName.get(bundle, "USD");
    expect(s, "US Dollar");

    s = Meta.SCHEMA.Units.long_.displayName.get(bundle, "light-year");
    expect(s, "light years");

    s = Meta.SCHEMA.Gregorian.availableFormats.get(bundle, "other", "yMd");
    expect(s, "dd/MM/y");

    s = Meta.SCHEMA.Gregorian.dateFormats.get(bundle, "full");
    expect(s, "EEEE, d MMMM y");
  }


  private static JsonObject load(String path) throws IOException {
    try (InputStream stream = Bundle.class.getResourceAsStream(path)) {
      return (JsonObject) JSON_PARSER.parse(new InputStreamReader(stream));
    }
  }

  private static void expect(String actual, String expected) throws Exception {
    if (!actual.equals(expected)) {
      throw new Exception("Expected " + expected + " got " + actual);
    }
  }
}

