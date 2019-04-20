package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.ContextTransformFieldType;
import com.squarespace.cldrengine.internal.EraWidthType;
import com.squarespace.cldrengine.internal.MetaZoneType;
import com.squarespace.cldrengine.internal.TimeZoneNameType;

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

//    s = cldr.Schema.ContextTransforms.contextTransforms.get(bundle, ContextTransformFieldType.MONTH_FORMAT_EXCEPT_NARROW);
//    System.out.println("|" + s + "|");
//
    s = cldr.Schema.TimeZones.metaZones.short_.get(bundle, TimeZoneNameType.DAYLIGHT, MetaZoneType.AMERICA_EASTERN);
    System.out.println("|" + s + "|");

//    s = cldr.Schema.TimeZones.metaZones.long_.get(bundle, TimeZoneNameType.DAYLIGHT, MetaZoneType.AMERICA_EASTERN);
//    System.out.println("|" + s + "|");
  }
}
