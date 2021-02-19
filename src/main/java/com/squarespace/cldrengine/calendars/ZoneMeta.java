package com.squarespace.cldrengine.calendars;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ZoneMeta {

  /**
   * TIme zone identifier. Corrected if the metadata was looked up using an alias.
   */
  public final String zoneId;

  /**
   * Current standard offset from UTC, in milliseconds.
   */
  public final long stdoffset;

  /**
   * Latitude of the zone's "principal location".
   */
  public final double latitude;

  /**
   * Longitude of the zone's "principal location".
   */
  public final double longitude;

  /**
   * ISO 3166 2-character country code for all countries which overlap the timezone.
   */
  public final String[] countries;

}
