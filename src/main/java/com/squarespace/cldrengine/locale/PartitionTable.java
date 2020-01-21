package com.squarespace.cldrengine.locale;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.squarespace.cldrengine.internal.LocaleExternalData;

/**
 * Builds a set of partitions for use in enhanced language matching. This splits
 * world regions into several clusters based on distance.
*/
class PartitionTable {

  private static final Map<String, Set<String>> REGION_TO_PARTITION = load();

  public static Set<String> getRegionPartition(String region) {
    Set<String> result = REGION_TO_PARTITION.get(region);
    return result == null ? Collections.emptySet() : result;
  }

  private static Map<String, Set<String>> load() {
    Map<String, Set<String>> result = new HashMap<>();
    encode(result, LocaleExternalData.REGIONS);
    encode(result, LocaleExternalData.MACROREGIONS);
    return result;
  }

  private static void encode(Map<String, Set<String>> map, String data) {
    for (String row : data.split("\\|")) {
      String[] parts = row.split(":");
      String region = parts[0];
      Set<String> vals = new HashSet<>();
      map.put(region, vals);
      for (String val : parts[1].split("")) {
        vals.add(val);
      }
    }
  }
}
