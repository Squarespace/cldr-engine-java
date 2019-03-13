package com.squarespace.cldrengine.calendars;

public class ZoneInfo {

  public final String timeZoneId;
  public final String metaZoneId;
  public final int offset;
  public final boolean dst;

  public ZoneInfo(String timeZoneId, String metaZoneId, int offset, boolean dst) {
    this.timeZoneId = timeZoneId;
    this.metaZoneId = metaZoneId;
    this.offset = offset;
    this.dst = dst;
  }

}
