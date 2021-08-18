package com.squarespace.cldrengine.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.internal.LocaleExternalData;

class Utils {

  public static Map<String, List<String>> REGION_ALIAS_MAP = loadRegionAliases();

  public static String replaceRegion(String region) {
    List<String> aliases = REGION_ALIAS_MAP.get(region);
    return aliases == null || aliases.isEmpty() ? null : aliases.get(0);
  }

  private static Map<String, List<String>> loadRegionAliases() {
    Map<String, List<String>> map = new HashMap<>();
    for (String row : LocaleExternalData.TERRITORYALIASRAW.split("\\|")) {
      String[] parts = row.split(":");
      String key = parts[0];
      List<String> regions = new ArrayList<>();
      for (String region : parts[1].split(" ")) {
        regions.add(region);
      }
      map.put(key, regions);
    }
    return map;
  }

}
