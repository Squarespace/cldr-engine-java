package com.squarespace.cldrengine.internal;

import java.util.HashMap;
import java.util.Map;

public class Vector2Arrow<R, S> {

  private int size;
  private int size2;
  private int offset;
  private KeyIndex<R> index1;
  private KeyIndex<S> index2;

  public Vector2Arrow(int offset, KeyIndex<R> index1, KeyIndex<S> index2) {
    this.size = index1.size * index2.size;
    this.size2 = index2.size;
    this.offset = offset + 1; // skip header
    this.index1 = index1;
    this.index2 = index2;
  }

  public boolean exists(PrimitiveBundle bundle) {
    return "E".equals(bundle.get(this.offset - 1));
  }

  public int size() {
    return this.size;
  }

  public String get(PrimitiveBundle bundle, R key1, S key2) {
    boolean exists = this.exists(bundle);
    if (exists) {
      int i = this.index1.get(key1);
      if (i != -1) {
        int j = this.index2.get(key2);
        if (j != -1) {
          int k = this.offset + (i * this.size2) + j;
          return bundle.get(k);
        }
      }
    }
    return "";
  }

  /**
   * Return mapping of all keys to all non-empty string values.
   */
  public Map<R, Map<S, String>> mapping(PrimitiveBundle bundle) {
    boolean exists = this.exists(bundle);
    Map<R, Map<S, String>> res = new HashMap<>();
    if (!exists) {
      return res;
    }

    R[] keys1 = this.index1.keys();
    S[] keys2 = this.index2.keys();
    for (int i = 0; i < this.index1.size; i++) {
      Map<S, String> map = new HashMap<>();
      exists = false;
      for (int j = 0; j < this.index2.size; j++) {
        S key2 = keys2[j];
        int k = this.offset + (i * this.size2) + j;
        String val = bundle.get(k);
        if (!val.equals("")) {
          exists = true;
          map.put(key2, val);
        }
      }
      if (exists) {
        res.put(keys1[i], map);
      }
    }
    return res;
  }
}
