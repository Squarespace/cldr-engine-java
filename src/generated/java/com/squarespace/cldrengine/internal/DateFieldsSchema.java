package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.DateFieldType;

public class DateFieldsSchema {

  public final RelativeTimes relativeTimes;
  public final Vector2Arrow<DateFieldType, DateFieldWidthType> displayName;

  public DateFieldsSchema(
      RelativeTimes relativeTimes,
      Vector2Arrow<DateFieldType, DateFieldWidthType> displayName) {
    this.relativeTimes = relativeTimes;
    this.displayName = displayName;
  }

}
