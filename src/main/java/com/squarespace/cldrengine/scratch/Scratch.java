package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.internal.FormatWidthType;
import com.squarespace.cldrengine.internal.Meta;
import com.squarespace.cldrengine.internal.MetaZoneType;
import com.squarespace.cldrengine.internal.PrimitiveBundle;
import com.squarespace.cldrengine.internal.TimeZoneNameType;

public class Scratch {

  public static void main(String[] args) {
    PrimitiveBundle bundle = new PrimitiveBundle() {
      @Override
      public String get(int offset) {
        // TODO Auto-generated method stub
        return null;
      }
      @Override
      public String id() {
        // TODO Auto-generated method stub
        return null;
      }
      @Override
      public String language() {
        // TODO Auto-generated method stub
        return null;
      }
      @Override
      public String region() {
        // TODO Auto-generated method stub
        return null;
      }
    };

    Meta.SCHEMA.Buddhist.dateFormats.get(bundle, FormatWidthType.FULL);
    Meta.SCHEMA.TimeZones.metaZones.short_.get(bundle, TimeZoneNameType.DAYLIGHT, MetaZoneType.AMERICA_CENTRAL);
  }

}
