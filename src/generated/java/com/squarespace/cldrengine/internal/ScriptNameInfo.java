package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.ScriptIdType;

public class ScriptNameInfo {

  public final Vector2Arrow<AltType, ScriptIdType> displayName;

  public ScriptNameInfo(
      Vector2Arrow<AltType, ScriptIdType> displayName) {
    this.displayName = displayName;
  }

}
