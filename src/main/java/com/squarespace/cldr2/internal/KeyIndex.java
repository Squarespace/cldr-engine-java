package com.squarespace.cldr2.internal;

import java.util.HashMap;
import java.util.Map;

public class KeyIndex<T> {

  private final T[] elements;
  private final Map<T, Integer> map;
  public final int size;

  public KeyIndex(T[] elements) {
    this.elements = elements;
    this.size = elements.length;
    this.map = new HashMap<>();
    for (int i = 0; i < this.size; i++) {
      this.map.put(elements[i], i);
    }
  }

  public int get(T key) {
    Integer i = this.map.get(key);
    return i == null ? -1 : i;
  }

  T[] keys() {
    return this.elements;
  }
}
