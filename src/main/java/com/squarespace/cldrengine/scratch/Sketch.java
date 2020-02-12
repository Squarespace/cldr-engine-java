package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CurrencyType;
import com.squarespace.cldrengine.api.DateFieldType;
import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.EraWidthType;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.api.ListPatternPositionType;
import com.squarespace.cldrengine.api.MetaZoneType;
import com.squarespace.cldrengine.api.NumberSystemCategory;
import com.squarespace.cldrengine.api.Pair;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.RegionIdType;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;
import com.squarespace.cldrengine.api.ScriptIdType;
import com.squarespace.cldrengine.api.TimeZoneNameType;
import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.internal.Meta;
import com.squarespace.cldrengine.internal.Pack;
import com.squarespace.cldrengine.internal.ResourcePacks;

/**
 * Quick verification that the schema generator's offsets are in sync with
 * the resource pack.
 */
public class Sketch {

  public static void main(String[] args) throws Exception {
//    JsonObject root = load("en.json");
//    Pack pack = new Pack(root);

    Pack pack = ResourcePacks.get("en");
    LanguageTag tag = new LanguageTag("en", "Latn", "US");
    Bundle bundle = pack.get(tag);

    String s;
    Pair<String, Integer> d;

    s = Meta.SCHEMA.Names.scripts.displayName.get(bundle, AltType.NONE, ScriptIdType.LATN);
    expect(s, "Latin");

    s = Meta.SCHEMA.Names.regions.displayName.get(bundle, AltType.NONE, RegionIdType.US);
    expect(s, "United States");

    s = Meta.SCHEMA.Numbers.numberSystems.get(bundle, NumberSystemCategory.DEFAULT);
    expect(s, "latn");

    d = Meta.SCHEMA.Numbers.numberSystem.get("latn").decimalFormats.long_.get(bundle, PluralType.OTHER, 11);
    expect(d._1, "00 billion");

    s = Meta.SCHEMA.Numbers.numberSystem.get("latn").scientificFormat.get(bundle);
    expect(s, "#E0");

    s = Meta.SCHEMA.DateFields.displayName.get(bundle, DateFieldType.QUARTER, DateFieldWidthType.WIDE);
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

    s = Meta.SCHEMA.TimeZones.metaZones.long_.get(bundle, TimeZoneNameType.DAYLIGHT, MetaZoneType.AMERICA_EASTERN);
    expect(s, "Eastern Daylight Time");

    s = Meta.SCHEMA.TimeZones.exemplarCity.get(bundle, "America/New_York");
    expect(s, "New York");

    s = Meta.SCHEMA.Currencies.displayName.get(bundle, CurrencyType.USD);
    expect(s, "US Dollar");

    s = Meta.SCHEMA.Units.long_.displayName.get(bundle, UnitType.LIGHT_YEAR);
    expect(s, "light years");

    s = Meta.SCHEMA.Gregorian.availableFormats.get(bundle, "yMd");
    expect(s, "M/d/y");

    s = Meta.SCHEMA.Gregorian.dateFormats.get(bundle, FormatWidthType.FULL);
    expect(s, "EEEE, MMMM d, y");
  }

  private static void expect(String actual, String expected) throws Exception {
    if (!expected.equals(actual)) {
      throw new Exception("Expected " + expected + " got " + actual);
    }
    System.out.println(actual);
  }
}

