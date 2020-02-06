package com.squarespace.cldrengine.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * A single part of a multi-part value.
 */
@AllArgsConstructor
@EqualsAndHashCode
public class Part {

  public final String type;
  public final String value;

  public static Part part(String type, String value) {
    return new Part(type, value);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("{ type='").append(type);
    return buf.append("' value='").append(value).append("' }").toString();
  }
}
