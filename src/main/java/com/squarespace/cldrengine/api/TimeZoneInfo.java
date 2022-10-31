package com.squarespace.cldrengine.api;

import java.util.Collections;
import java.util.List;

public class TimeZoneInfo {

  private final String id;
  private final ExemplarCity city;
  private final List<String> countries;
  private final double latitude;
  private final double longitude;
  private final long stdoffset;
  private final String metazone;

  public TimeZoneInfo(String id, ExemplarCity city, List<String> countries,
      double latitude, double longitude, long stdoffset, String metazone) {
    this.id = id;
    this.city = city;
    this.countries = Collections.unmodifiableList(countries);
    this.latitude = latitude;
    this.longitude = longitude;
    this.stdoffset = stdoffset;
    this.metazone = metazone;
  }

  public String id() {
    return id;
  }

  public ExemplarCity city() {
    return city;
  }

  public List<String> countries() {
    return countries;
  }

  public double latitude() {
    return latitude;
  }

  public double longitude() {
    return longitude;
  }

  public long stdoffset() {
    return stdoffset;
  }

  public String metazone() {
    return metazone;
  }
}
