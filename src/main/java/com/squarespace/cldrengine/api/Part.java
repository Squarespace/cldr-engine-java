package com.squarespace.cldrengine.api;

import lombok.AllArgsConstructor;

/**
 * A single part of a multi-part value.
 *
 * @alpha
 */
@AllArgsConstructor
public class Part {

  public final String type;
  public final String value;

}
