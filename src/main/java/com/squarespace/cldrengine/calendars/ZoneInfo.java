package com.squarespace.cldrengine.calendars;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ZoneInfo {

  /**
   * Time zone identifier.
   */
  public final String timeZoneId;

  /**
   * Time zone stable id.
   */
  public final String stableId;

  /**
   * Time zone metazone id.
   */
  public final String metaZoneId;

  /**
   * Time zone 3-character abbreviation.
   */
  public final String abbr;

  /**
   * Time zone offset from UTC.
   */
  public final int offset;

  /**
   * Flag indicating the zone is currently in daylight savings time.
   */
  public final boolean dst;

}
