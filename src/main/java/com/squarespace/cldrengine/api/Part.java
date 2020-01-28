package com.squarespace.cldrengine.api;

import lombok.AllArgsConstructor;

/**
 * A single part of a multi-part value.
 */
@AllArgsConstructor
public class Part {

  public final String type;
  public final String value;

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("{ type='").append(type);
    return buf.append("' value='").append(value).append("' }").toString();
  }
}
