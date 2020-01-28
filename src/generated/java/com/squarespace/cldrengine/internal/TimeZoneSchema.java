package com.squarespace.cldrengine.internal;



public class TimeZoneSchema {

  public final MetaZoneInfo metaZones;
  public final Vector1Arrow<String> exemplarCity;
  public final FieldArrow gmtFormat;
  public final FieldArrow hourFormat;
  public final FieldArrow gmtZeroFormat;
  public final FieldArrow regionFormat;

  public TimeZoneSchema(
      MetaZoneInfo metaZones,
      Vector1Arrow<String> exemplarCity,
      FieldArrow gmtFormat,
      FieldArrow hourFormat,
      FieldArrow gmtZeroFormat,
      FieldArrow regionFormat) {
    this.metaZones = metaZones;
    this.exemplarCity = exemplarCity;
    this.gmtFormat = gmtFormat;
    this.hourFormat = hourFormat;
    this.gmtZeroFormat = gmtZeroFormat;
    this.regionFormat = regionFormat;
  }

}
