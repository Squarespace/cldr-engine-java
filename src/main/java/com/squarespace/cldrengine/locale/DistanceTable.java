package com.squarespace.cldrengine.locale;

import static com.squarespace.cldrengine.locale.DistanceMap.ANY;

import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squarespace.cldrengine.internal.LocaleConstants;
import com.squarespace.cldrengine.locale.DistanceMap.Node;
import com.squarespace.cldrengine.utils.ResourceUtil;

public class DistanceTable {

  public static final int MAX_DISTANCE = 100;
  public static final int DEFAULT_THRESHOLD = 50;

  private static final DistanceMap MAP = load();

  /**
   * Returns the distance between the desired and supported locale, using the
   * default distance threshold.
   */
  public static int distance(LanguageTag desired, LanguageTag supported) {
    return distance(desired, supported, DEFAULT_THRESHOLD);
  }

  /**
   * Returns the distance between the desired and supported locale, using the
   * given distance threshold. Any distance equal to or greater than the threshold
   * will return the maximum distance.
   */
  public static int distance(LanguageTag desired, LanguageTag supported, int threshold) {
    // Query the top level LANGUAGE
    boolean langEquals = desired.language().equals(supported.language());
    Node node = MAP.get(desired.language(), supported.language());
    if (node == null) {
      node = MAP.get(ANY, ANY);
    }
    int distance = node.wildcard() ? (langEquals ? 0 : node.distance()) : node.distance();

    if (distance >= threshold) {
      return MAX_DISTANCE;
    }

    // Go to the next level SCRIPT
    DistanceMap map = node.map();

    boolean scriptEquals = desired.script().equals(supported.script());
    node = map.get(desired.script(), supported.script());
    if (node == null) {
      node = map.get(ANY, ANY);
    }
    distance += node.wildcard() ? (scriptEquals ? 0 : node.distance()) : node.distance();

    if (distance >= threshold) {
      return MAX_DISTANCE;
    }

    // Go to the next level TERRITORY
    map = node.map();

    // If the territories happen to be equal, distance is 0 so we're done.
    if (desired.region().equals(supported.region())) {
      return distance;
    }

    // Check if the map contains a distance between these two regions.
    node = map.get(desired.region(), supported.region());
    if (node == null) {
      // Compare the desired region against supported partitions, and vice-versa.
      node = scanTerritory(map, desired.region(), supported.region());
    }
    if (node != null) {
      distance += node.distance();
      return distance < threshold ? distance : MAX_DISTANCE;
    }

    // Find the maximum distance between the partitions.
    int maxDistance = 0;

    // These partition sets are always guaranteed to exist and contain at least 1 member.
    boolean match = false;
    Set<String> desiredPartitions = PartitionTable.getRegionPartition(desired.region());
    Set<String> supportedPartitions = PartitionTable.getRegionPartition(supported.region());

    for (String desiredPartition : desiredPartitions) {
      for (String supportedPartition : supportedPartitions) {
        node = map.get(desiredPartition, supportedPartition);
        if (node != null) {
          maxDistance = Math.max(maxDistance, node.distance());
          match = true;
        }
      }
    }

    if (!match) {
      node = map.get(ANY, ANY);
      maxDistance = Math.max(maxDistance, node.distance());
    }

    distance += maxDistance;
    return distance < threshold ? distance : MAX_DISTANCE;
  }

  /**
   * Scan the desired region against the supported partitions and vice versa.
   * Return the first matching node.
   */
  private static Node scanTerritory(DistanceMap map, String desired, String supported) {
    Node node;
    for (String partition : PartitionTable.getRegionPartition(desired)) {
      node = map.get(partition, supported);
      if (node != null) {
        return node;
      }
    }

    for (String partition : PartitionTable.getRegionPartition(supported)) {
      node = map.get(desired, partition);
      if (node != null) {
        return node;
      }
    }

    return null;
  }

  /**
   * The distance map JSON is auto-generated and assumed correct here, so
   * we omit type checking and error handling.
   */
  private static DistanceMap load() {
    JsonObject json = (JsonObject) ResourceUtil.parse(LocaleConstants.DISTANCEMAP);
    DistanceMap map = new DistanceMap();
    encode(map, json);
    return map;
  }

  private static void encode(DistanceMap map, JsonObject json) {
    for (String want : json.keySet()) {
      JsonObject child = json.getAsJsonObject(want);
      for (String have : child.keySet()) {
        JsonElement data = child.get(have);
        want = wildcard(want);
        have = wildcard(have);
        if (data.isJsonArray()) {
          JsonArray arr = data.getAsJsonArray();
          int distance = arr.get(0).getAsInt();
          Node node = map.put(want, have, distance);
          node.init();
          encode(node.map(), arr.get(1).getAsJsonObject());
        } else {
          int distance = data.getAsInt();
          map.put(want, have, distance);
        }
      }
    }
  }

  private static String wildcard(String val) {
    return val.equals("$") ? DistanceMap.ANY : val;
  }
}
