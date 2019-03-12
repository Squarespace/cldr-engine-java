package com.squarespace.cldrengine.internal;

import java.util.LinkedHashMap;
import java.util.Map;

public class Vector1Arrow<R> {

  private int len;
  private int offset;
  private KeyIndex<R> index;

  public Vector1Arrow(int offset, KeyIndex<R> index) {
    this.len = index.size;
    this.offset = offset + 1; // skip header
    this.index = index;
  }

  boolean exists(PrimitiveBundle bundle) {
    return "E".equals(bundle.get(this.offset - 1));
  }

  public String get(PrimitiveBundle bundle, R key) {
    boolean exists = this.exists(bundle);
    if (exists) {
      int i = this.index.get(key);
      return i == -1 ? "" : bundle.get(this.offset + i);
    }
    return "";
  }

  public Map<R, String> mapping(PrimitiveBundle bundle) {
    Map<R, String> res = new LinkedHashMap<>();
    boolean exists = this.exists(bundle);
    if (!exists) {
      return res;
    }
    R[] keys = this.index.keys();
    int offset = this.offset;
    for (int i = 0; i < this.len; i++) {
      String s = bundle.get(offset + i);
      if (s != null) {
        res.put(keys[i], s);
      }
    }
    return res;
  }

}
