package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.calendars.TimeZoneData;

public class Sketch4 {

  public static void main(String[] args) {
    load();
  }

  private static void load() {
//    String id = "en";
//    CLDR cldr = CLDR.get(id);

    String z = TimeZoneData.getMetazone("America/New_York", System.currentTimeMillis());
    System.out.println(z);
  }
}
