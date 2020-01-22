package com.squarespace.cldrengine.scratch;

import java.util.Map;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.MetaZoneType;
import com.squarespace.cldrengine.api.TimeZoneNameType;

public class Sketch3 {

  public static void main(String[] args) {
    load();
  }

  private static void load() {
    String id = "en";
    CLDR cldr = CLDR.get(id);

    Bundle bundle = cldr.General.bundle();
    System.out.println(bundle.id());
    String s;

    s = cldr.Schema.ContextTransforms.contextTransforms.get(bundle, ContextTransformFieldType.CALENDAR_FIELD);
    System.out.println(s);

    s = cldr.Schema.TimeZones.metaZones.short_.get(bundle, TimeZoneNameType.DAYLIGHT, MetaZoneType.AMERICA_EASTERN);
    System.out.println(s);

    s = cldr.Schema.TimeZones.metaZones.long_.get(bundle, TimeZoneNameType.DAYLIGHT, MetaZoneType.AMERICA_EASTERN);
    System.out.println(s);

    Map<TimeZoneNameType, Map<MetaZoneType, String>> m = cldr.Schema.TimeZones.metaZones.long_.mapping(bundle);
    System.out.println(m);
  }
}
