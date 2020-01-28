package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.RegionIdType;

public class RegionNameInfo {

  public final Vector2Arrow<AltType, RegionIdType> displayName;

  public RegionNameInfo(
      Vector2Arrow<AltType, RegionIdType> displayName) {
    this.displayName = displayName;
  }

}
