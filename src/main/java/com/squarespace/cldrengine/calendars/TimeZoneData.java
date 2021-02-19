package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.JsonUtils.decodeArray;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squarespace.cldrengine.internal.TimeZoneExternalData;
import com.squarespace.cldrengine.utils.JsonUtils;
import com.squarespace.cldrengine.utils.Search;
import com.squarespace.cldrengine.utils.StringUtils;

public class TimeZoneData {

  // Mapping of additional aliases to canonical timezone identifier
  private static final Map<String, String> ZONEALIASES = new HashMap<>();

  // Mapping of CLDR stable timezone identifiers
  private static final Map<String, String> ZONE_TO_STABLEID = new HashMap<>();

  // Set to check if a timezone id is a CLDR stable id
  private static final Set<String> CLDR_STABLEIDS = new HashSet<>();

  // Maps a canonical timezone identifier to the index offset its zone info
  private static final Map<String, Integer> ZONEINDEX = new HashMap<>();

  // Map aliases and lowercase forms to canonical timezone identifier
  private static final Map<String, String> LINKINDEX = new HashMap<>();

  // Map a timezone identifier to a metazone index
  private static final Map<String, Integer> ZONETOMETAZONE = new HashMap<>();

  // Static mapping of characters to integer
  private static Map<String, Integer> TYPES = new HashMap<>();

  // Default UTC zone here for quick access.
  private static final TZInfo UTC = new TZInfo("Etc/UTC", "UTC", 0, 0);

  // Array of decoded timezone identifiers
  private static String[] TIMEZONEIDS;

  // Array of until timestamps sorted by usage frequency
  private static long[] UNTILINDEX;

  // Decoded zoneinfo records
  private static ZoneRecord[] ZONERECORDS;

  // Array of metazone identifiers
  private static String[] METAZONEIDS;

  // Decoded metazone records
  private static MetazoneRecord[] METAZONES;

  static {
    encodeTypes();
    loadStableIds();
    loadAliases();
    loadTimezones();
    loadMetazones();
  }

  /**
   * Maps a possible timezone alias to the correct id.
   */
  public static String substituteZoneAlias(String id) {
    String zoneId = ZONEALIASES.get(id);
    return zoneId == null ? id : zoneId;
  }

  /**
   * Lookup the zoneinfo record for the given timezone id and UTC timestamp.
   */
  public static ZoneInfo zoneInfoFromUTC(String zoneId, long utc) {
    TZInfo info = lookup(zoneId, utc, true);
    if (info == null) {
      info = UTC;
    }

    // For the purposes of CLDR stable timezone ids, check if the passed-in
    // id is an alias to a current/valid tzdb id.
    boolean isstable = CLDR_STABLEIDS.contains(zoneId);

    // Use the passed-in id as the stable id if it is an alias,
    // otherwise lookup the id in the stable map.
    String stableId = isstable ? zoneId : getStableId(zoneId);

    String metazoneId = getMetazone(info.zoneId, utc);
    return new ZoneInfo(
      info.zoneId,
      stableId,
      metazoneId == null ? "" : metazoneId,
      info.abbr,
      info.offset,
      info.dst == 1
    );
  }

  /**
   * Metadata related to a zone, such as the list of country codes that overlap with
   * the zone, the latitude and longitude, and the current standard offset, in milliseconds.
   * These can be used to display user interfaces for selecting a zone.
   *
   * If the zone identifier does not match a known zone or alias this returns null.
   */
  public static ZoneMeta zoneMeta(String id) {
    ZoneRecord rec = record(id);
    if (rec != null) {
      return new ZoneMeta(rec.zoneId, rec.stdoff, rec.latitude, rec.longitude, rec.countries);
    }
    return null;
  }

  /**
   * For a given timezone identifier and UTC timestamp, return the
   * metazone identifier or null if none exists.
   */
  public static String getMetazone(String zoneId, long utc) {
    Integer i = ZONETOMETAZONE.get(zoneId);
    if (i != null) {
      MetazoneRecord rec = METAZONES[i];
      if (rec != null) {
        // Note: we don't bother with binary search here since the metazone
        // until arrays are quite short.
        int len = rec.untils.length;
        for (int j = len - 1; j > 0; j--) {
          if (rec.untils[j] <= utc) {
            return METAZONEIDS[(int)rec.offsets[j]];
          }
        }

        // Hit the end, return the initial metazone id
        return METAZONEIDS[(int)rec.offsets[0]];
      }
    }

    // This zone has no metazone id, e.g. "Etc/GMT+1"
    return null;
  }

  /**
   * Get the info for a time zone using a UTC timestamp.
   */
  public static TZInfo fromUTC(String zoneId, long utc) {
    return lookup(zoneId, utc, true);
  }

  /**
   * UTC zone info.
   */
  public static TZInfo utcZone() {
    return TimeZoneData.UTC;
  }

  /**
   * Resolve a lowercase time zone id or alias into the canonical proper-cased id.
   */
  public static String resolveId(String id) {
    return LINKINDEX.get(id);
  }

  /**
   * Returns a list of time zone ids.
   */
  public static List<String> zoneIds() {
    return Arrays.asList(TIMEZONEIDS);
  }

  /**
   * Map a timezone identifier to the CLDR stable id
   */
  private static String getStableId(String id) {
    return ZONE_TO_STABLEID.getOrDefault(id, id);
  }

  private static TZInfo lookup(String id, long utc, boolean isUTC) {
    ZoneRecord rec = record(id);
    return rec == null ? null : rec.fromUTC(utc);
  }

  private static ZoneRecord record(String zoneId) {
    String id = LINKINDEX.get(zoneId);
    if (id == null) {
      return null;
    }

    // Find the offset to the record for this zone. This should
    // always be non-null.
    int i = ZONEINDEX.get(id);
    return ZONERECORDS[i];
  }

  /**
   * Encode a mapping of a character to its integer offset for decoding
   * the tzdb type values.
   */
  private static void encodeTypes() {
    String[] parts = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    for (int i = 0; i < parts.length; i++) {
      TYPES.put(parts[i], i);
    }
  }

  /**
   * Load the mapping of timezone alias to canonical identifier.
   */
  private static void loadAliases() {
    for (String row : TimeZoneExternalData.ZONEALIASRAW.split("\\|")) {
      String[] parts = row.split(":");
      ZONEALIASES.put(parts[0], parts[1]);
    }
  }

  /**
   * Load all timezone data.
   */
  private static void loadTimezones() {
    JsonObject root;
    try {
      root = JsonUtils.loadJson(TimeZoneExternalData.class, "zonedata.json");
    } catch (IOException e) {
      throw new RuntimeException("Failed to load timezone data resource", e);
    }

    // Decode timezone ids and index array position
    TIMEZONEIDS = split(root.get("zoneids"), "\\|");
    for (int i = 0; i < TIMEZONEIDS.length; i++) {
      String id = TIMEZONEIDS[i];
      ZONEINDEX.put(id, i);
      addLink(id, id);
    }

    // Decode links
    String[] parts = split(root.get("links"), "\\|");
    for (String part : parts) {
      String[] kv = part.split(":");
      String alias = kv[0];
      String id = TIMEZONEIDS[Integer.parseInt(kv[1])];
      addLink(alias, id);
    }

    // Decode timezone until index and raw zone info
    UNTILINDEX = StringUtils.longArray(root.get("index").getAsString());

    // Decode all zoneinfo records
    String[] rawzoneinfo = decodeArray(root.get("zoneinfo"));
    ZONERECORDS = new ZoneRecord[rawzoneinfo.length];
    for (int i = 0; i < rawzoneinfo.length; i++) {
      ZONERECORDS[i] = new ZoneRecord(TIMEZONEIDS[i], rawzoneinfo[i]);
    }
  }

  /**
   * Load CLDR metazone records and CLDR stable identifiers.
   *
   * Example: "America_Eastern" represents a metazone that
   * references standard and daylight savings identifiers for
   * "America/New_York", etc.
   *
   * A CLDR stable timezone identifier never changes, even as
   * canonical TZDB identifiers are deprecated. Each time a
   * new TZDB is released we generate the mappings back to the
   * corresponding CLDR stable identifier, where they differ.
   */
  private static void loadMetazones() {
    JsonObject root = (JsonObject) JsonUtils.parse(TimeZoneExternalData.METAZONEDATA);
    METAZONEIDS = decodeArray(root.get("metazoneids"));

    long[] index = StringUtils.longArray(root.get("index").getAsString());
    long[] offsets = StringUtils.longArray(root.get("offsets").getAsString());
    long[] untils = StringUtils.longArray(root.get("untils").getAsString());

    // Decode all metazone records
    METAZONES = new MetazoneRecord[index.length / 2];
    for (int i = 0; i < index.length; i += 2) {
      int start = (int)index[i];
      int end = (int)index[i + 1];
      METAZONES[i / 2] = new MetazoneRecord(
        Arrays.copyOfRange(offsets, start, end),
        Arrays.copyOfRange(untils, start, end)
      );
    }

    // Map timezone identifiers to corresponding metazone record offset
    long[] zoneindex = StringUtils.longArray(root.get("zoneindex").getAsString());
    for (int i = 0; i < zoneindex.length; i++) {
      int mi = (int) zoneindex[i];
      if (mi != -1) {
        ZONETOMETAZONE.put(TIMEZONEIDS[i], mi);
        ZONETOMETAZONE.put(TIMEZONEIDS[i].toLowerCase(), mi);
      }
    }

    // Map timezone identifier back to CLDR stable identifier
    String[] parts = split(root.get("stableids"), "\\|");
    for (int i = 0; i < parts.length; i++) {
      String[] kv = parts[i].split(":");
      String zoneid = TIMEZONEIDS[Integer.parseInt(kv[0])];
      ZONE_TO_STABLEID.put(zoneid, kv[1]);
    }
  }

  /**
   * Load the set of CLDR stable timezone ids.
   */
  private static void loadStableIds() {
    String[] ids = decodeArray(JsonUtils.parse(TimeZoneExternalData.STABLEIDS));
    for (String id : ids) {
      CLDR_STABLEIDS.add(id);
    }
  }

  private static void addLink(String src, String dst) {
    LINKINDEX.put(src, dst);
    LINKINDEX.put(src.toLowerCase(), dst);
  }

  private static String[] split(JsonElement elem, String delim) {
    return split(elem.getAsString(), delim);
  }

  private static String[] split(String raw, String delim) {
    return raw.isEmpty() ? new String[] { } : raw.split(delim);
  }

  /**
   * Holds paired until timestamps and offsets used to determine
   * which metazone identifier to use at a given point in time.
   */
  private static class MetazoneRecord {

    final long[] offsets;
    final long[] untils;

    public MetazoneRecord(long[] offsets, long[] untils) {
      this.offsets = offsets;
      this.untils = untils;
    }

    @Override
    public String toString() {
      return "MetazoneRecord(offsets=" + Arrays.toString(this.offsets)
        + " untils=" + Arrays.toString(this.untils)
        + ")";
    }
  }

  /**
   * Record for a single timezone, used to determine which localtime
   * record is in effect at a given point in time.
   */
  private static class ZoneRecord {
    final String zoneId;
    final long stdoff;
    final double latitude;
    final double longitude;
    final String[] countries;

    final TZInfo[] localtime;
    final int[] types;
    final long[] untils;

    public ZoneRecord(String zoneId, String raw) {
      this.zoneId = zoneId;
      String[] parts = split(raw, "_");
      String _std = parts[0];
      String _lat = parts[1];
      String _lon = parts[2];
      String _countries = parts[3];
      String _info = parts[4];
      String _types = parts.length > 5 ? parts[5] : "";
      String _untils = parts.length > 6 ? parts[6] : "";

      long[] untils = StringUtils.longArray(_untils);

      int len = untils.length;
      if (len > 0) {
        untils[0] = UNTILINDEX[(int)untils[0]] * 1000;
        for (int i = 1; i < len; i++) {
          untils[i] = untils[i - 1] + (UNTILINDEX[(int)untils[i]] * 1000);
        }
      }

      parts = split(_info, "\\|");
      this.localtime = new TZInfo[parts.length];
      for (int i = 0; i < parts.length; i++) {
        this.localtime[i] = TZInfo.decode(zoneId, parts[i]);
      }

      parts = split(_types, "");
      this.types = new int[parts.length];
      for (int i = 0; i < parts.length; i++) {
        this.types[i] = TYPES.get(parts[i]);
      }
      this.untils = untils;

      this.stdoff = Long.parseLong(_std, 36) * 1000;
      this.latitude = Long.parseLong(_lat, 36) / 1e6;
      this.longitude = Long.parseLong(_lon, 36) / 1e6;
      this.countries = _countries.isEmpty() ? new String[] {} : _countries.split(",");
    }

    public TZInfo fromUTC(long utc) {
      int i = Search.binarySearch(this.untils, true, utc);
      int type = i == -1 ? 0 : this.types[i];
      return this.localtime[type];
    }

    @Override
    public String toString() {
      return "ZoneRecord(untils=" + Arrays.toString(untils)
        + " localtime=" + Arrays.toString(localtime)
        + " types=" + Arrays.toString(types)
        + ")";
    }
  }

  /**
   * Holds a timezone abbreviation, daylight savings (dst) flag,
   * and an offset from UTC.
   */
  static class TZInfo {
    final String zoneId;
    final String abbr;
    final int dst;
    final int offset;

    public TZInfo(String zoneId, String abbr, int dst, int offset) {
      this.zoneId = zoneId;
      this.abbr = abbr;
      this.dst = dst;
      this.offset = offset;
    }

    static TZInfo decode(String zoneId, String raw) {
      String[] parts = raw.split(":");
      String abbr = parts[0];
      int dst = Integer.parseInt(parts[1]);
      int offset = Integer.parseInt(parts[2], 36) * 1000;
      return new TZInfo(zoneId, abbr, dst, offset);
    }

    @Override
    public String toString() {
      return "ZoneInfo(id=" + zoneId
          + " abbr=" + abbr
          + " dst=" + dst
          + " offset=" + offset
          + ")";
    }
  }
}
