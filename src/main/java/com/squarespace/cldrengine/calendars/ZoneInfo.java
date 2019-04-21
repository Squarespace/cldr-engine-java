package com.squarespace.cldrengine.calendars;

public class ZoneInfo {

  public final String timeZoneId;
  public final String stableId;
  public final String metaZoneId;
  public final int offset;
  public final boolean dst;

  public ZoneInfo(String timeZoneId, String stableId, String metaZoneId, int offset, boolean dst) {
    this.timeZoneId = timeZoneId;
    this.stableId = stableId;
    this.metaZoneId = metaZoneId;
    this.offset = offset;
    this.dst = dst;
  }

}
