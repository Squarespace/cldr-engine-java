package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.TimeZoneNameType;
import com.squarespace.cldrengine.api.MetaZoneType;

public class MetaZoneInfo {

  public final Vector2Arrow<TimeZoneNameType, MetaZoneType> short_;
  public final Vector2Arrow<TimeZoneNameType, MetaZoneType> long_;

  public MetaZoneInfo(
      Vector2Arrow<TimeZoneNameType, MetaZoneType> short_,
      Vector2Arrow<TimeZoneNameType, MetaZoneType> long_) {
    this.short_ = short_;
    this.long_ = long_;
  }

}
