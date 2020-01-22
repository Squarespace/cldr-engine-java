package com.squarespace.cldrengine.calendars;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ZoneInfo {

  public final String timeZoneId;
  public final String stableId;
  public final String metaZoneId;
  public final int offset;
  public final boolean dst;

}
