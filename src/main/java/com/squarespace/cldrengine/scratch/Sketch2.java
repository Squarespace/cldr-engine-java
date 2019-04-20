package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.EraWidthType;

public class Sketch2 {

  public static void main(String[] args) {
    load();
  }

  private static void load() {
    String id = "en";
    CLDR cldr = CLDR.get(id);
    long start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      cldr = CLDR.get(id);
    }
    long elapsed = System.currentTimeMillis() - start;
    System.out.println(elapsed);
    System.out.println(cldr.General.bundle().id());

    Bundle bundle = cldr.General.bundle();
    String s = cldr.Schema.Gregorian.availableFormats.get(bundle, "yMd");
    System.out.println(s);

    s = cldr.Schema.Japanese.eras.get(bundle, EraWidthType.NAMES, "236");
    System.out.println(s);
  }

}
