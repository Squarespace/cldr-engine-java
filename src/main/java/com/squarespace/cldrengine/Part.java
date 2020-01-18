package com.squarespace.cldrengine;

import lombok.AllArgsConstructor;

/**
 * A single part of a multi-part value.
 *
 * @alpha
 */
@AllArgsConstructor
public class Part {

  final String type;
  final String value;

}
