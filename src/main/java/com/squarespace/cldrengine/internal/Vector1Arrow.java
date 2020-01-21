package com.squarespace.cldrengine.internal;

import java.util.LinkedHashMap;
import java.util.Map;

public class Vector1Arrow<R> {

  private int size;
  private int offset;
  private KeyIndex<R> index;

  public Vector1Arrow(int offset, KeyIndex<R> index) {
    this.size = index.size;
    this.offset = offset + 1; // skip header
    this.index = index;
  }

  public boolean exists(PrimitiveBundle bundle) {
    return "E".equals(bundle.get(this.offset - 1));
  }

  public int size() {
    return this.size;
  }

  public String get(PrimitiveBundle bundle, R key) {
    boolean exists = this.exists(bundle);
    if (exists) {
      int i = this.index.get(key);
      return i == -1 ? "" : bundle.get(this.offset + i);
    }
    return "";
  }

  /**
   * Return mapping of all keys to all non-empty string values.
   */
  public Map<R, String> mapping(PrimitiveBundle bundle) {
    Map<R, String> res = new LinkedHashMap<>();
    boolean exists = this.exists(bundle);
    if (!exists) {
      return res;
    }
    R[] keys = this.index.keys();
    int offset = this.offset;
    for (int i = 0; i < this.size; i++) {
      String s = bundle.get(offset + i);
      if (s != null) {
        res.put(keys[i], s);
      }
    }
    return res;
  }

}

