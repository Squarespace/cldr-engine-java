package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.DisplayNameOptions;
import com.squarespace.cldrengine.api.LanguageTag;

public class Sketch19 {

  public static void main(String[] args) throws Exception {
    CLDR cldr = CLDR.get("en");
    String s;

    DisplayNameOptions opts = DisplayNameOptions.build();

    for (String id : new String[] { "en", "en-GB", "en-US", "und-US", "und-ZZ" }) {
      s = cldr.General.getLanguageDisplayName(id, opts);
      System.out.println(s);
    }

    for (String id : new String[] { "Arab", "Zzzz", "zh-CN", "zh-TW" }) {
      s = cldr.General.getScriptDisplayName(id, opts);
      System.out.println(s);
    }

    for (String id : new String[] { "US", "ZZ", "DE" }) {
      s = cldr.General.getRegionDisplayName(id, opts);
      System.out.println(s);
    }

    for (String id : new String[] { "und-US", "und-ZZ" }) {
      LanguageTag tag = cldr.General.parseLanguageTag(id);
      s = cldr.General.getRegionDisplayName(tag, opts);
      System.out.println(s);
    }
  }
}
