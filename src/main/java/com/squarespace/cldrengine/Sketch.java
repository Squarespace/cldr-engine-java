package com.squarespace.cldrengine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.internal.AltType;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.CurrencyType;
import com.squarespace.cldrengine.internal.DateFieldType;
import com.squarespace.cldrengine.internal.EraWidthType;
import com.squarespace.cldrengine.internal.FormatWidthType;
import com.squarespace.cldrengine.internal.LanguageTag;
import com.squarespace.cldrengine.internal.ListPatternPositionType;
import com.squarespace.cldrengine.internal.Meta;
import com.squarespace.cldrengine.internal.MetaZoneType;
import com.squarespace.cldrengine.internal.NumberSystemCategory;
import com.squarespace.cldrengine.internal.Pair;
import com.squarespace.cldrengine.internal.PluralType;
import com.squarespace.cldrengine.internal.RegionIdType;
import com.squarespace.cldrengine.internal.RelativeTimeFieldType;
import com.squarespace.cldrengine.internal.ScriptIdType;
import com.squarespace.cldrengine.internal.StringBundle;
import com.squarespace.cldrengine.internal.TimeZoneNameType;
import com.squarespace.cldrengine.internal.TimeZoneType;
import com.squarespace.cldrengine.internal.UnitType;

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

    s = Meta.SCHEMA.Names.scripts.displayName.get(bundle, ScriptIdType.LATN);
    expect(s, "Latin");

    s = Meta.SCHEMA.Names.regions.displayName.get(bundle, AltType.NONE, RegionIdType.US);
    expect(s, "United States");

    s = Meta.SCHEMA.Numbers.numberSystems.get(bundle, NumberSystemCategory.DEFAULT);
    expect(s, "latn");

    d = Meta.SCHEMA.Numbers.numberSystem.get("latn").decimalFormats.long_.get(bundle, PluralType.OTHER, 11);
    expect(d._1, "00 billion");

    s = Meta.SCHEMA.Numbers.numberSystem.get("latn").scientificFormat.get(bundle);
    expect(s, "#E0");

    s = Meta.SCHEMA.DateFields.relativeTimes.wide.displayName.get(bundle, DateFieldType.QUARTER);
    expect(s, "quarter");

    s = Meta.SCHEMA.DateFields.relativeTimes.wide.next.get(bundle, RelativeTimeFieldType.DAY);
    expect(s, "tomorrow");

    s = Meta.SCHEMA.ListPatterns.and.get(bundle, ListPatternPositionType.TWO);
    expect(s, "{0} and {1}");

    s = Meta.SCHEMA.Buddhist.eras.get(bundle, EraWidthType.NAMES, "0");
    expect(s, "BE");

    s = Meta.SCHEMA.Gregorian.eras.get(bundle, EraWidthType.NAMES, "0");
    expect(s, "Before Christ");

    s = Meta.SCHEMA.Gregorian.eras.get(bundle, EraWidthType.NAMES, "1");
    expect(s, "Anno Domini");

    s = Meta.SCHEMA.TimeZones.metaZones.short_.get(bundle, TimeZoneNameType.DAYLIGHT, MetaZoneType.AMERICA_EASTERN);
    expect(s, "Eastern Daylight Time");

    s = Meta.SCHEMA.TimeZones.exemplarCity.get(bundle, TimeZoneType.AMERICA_NEW_YORK);
    expect(s, "New York");

    s = Meta.SCHEMA.Currencies.displayName.get(bundle, CurrencyType.USD);
    expect(s, "US Dollar");

    s = Meta.SCHEMA.Units.long_.displayName.get(bundle, UnitType.LIGHT_YEAR);
    expect(s, "light years");

    s = Meta.SCHEMA.Gregorian.availableFormats.get(bundle, PluralType.OTHER, "yMd");
    expect(s, "dd/MM/y");

    s = Meta.SCHEMA.Gregorian.dateFormats.get(bundle, FormatWidthType.FULL);
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
    System.out.println(actual);
  }
}

