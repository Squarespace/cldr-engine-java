package com.squarespace.cldr2.internal;

import java.util.LinkedHashMap;
import java.util.Map;

public class Vector1Arrow<R> {

  private int len;
  private int offset;
  private KeyIndex<String> index;

  public Vector1Arrow(int offset, KeyIndex<String> index) {
    this.len = index.size;
    this.offset = offset + 1; // skip header
    this.index = index;
  }

  boolean exists(PrimitiveBundle bundle) {
    return "E".equals(bundle.get(this.offset - 1));
  }

  public String get(PrimitiveBundle bundle, String key) {
    boolean exists = this.exists(bundle);
    if (exists) {
      int i = this.index.get(key);
      return i == -1 ? "" : bundle.get(this.offset + i);
    }
    return "";
  }

  public Map<String, String> mapping(PrimitiveBundle bundle) {
    Map<String, String> res = new LinkedHashMap<>();
    boolean exists = this.exists(bundle);
    if (!exists) {
      return res;
    }
    String[] keys = this.index.keys();
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

