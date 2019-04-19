package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;

public class Sketch2 {

  public static void main(String[] args) {
    load();
  }

  private static void load() {
    CLDR cldr = CLDR.get("zh-Hans");
    long start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      cldr = CLDR.get("zh-Hans");
    }
    long elapsed = System.currentTimeMillis() - start;
    System.out.println(elapsed);
    System.out.println(cldr.General.bundle().id());
  }

}
