package com.squarespace.cldrengine.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.utils.StringUtils;

public abstract class VectorArrowImpl {

  private final int offset;
  private final List<KeyIndex<Object>> keysets;
  private final int len;
  private final int last;
  private final int[] factors;

  public VectorArrowImpl(int offset, List<KeyIndex<Object>> keysets) {
    this.offset = offset + 1; // skip over header
    this.keysets = keysets;
    this.len = keysets.size();
    this.last = this.len - 1;
    this.factors = new int[this.len];

    // Pre-compute the address factor for each dimension:
    //  1-dim:        [ index0 ]
    //  2-dim:        [ (index0 * size1), index1 ]
    //  3-dim:        [ (index0 * size1 * size2), (index1 * size), index2 ]
    //  ...
    for (int i = 0; i < this.len; i++) {
      int k = 1;
      for (int j = i + 1; j < this.len; j++) {
        k *= this.keysets.get(j).size;
      }
      this.factors[i] = k;
    }
  }

  public boolean exists(PrimitiveBundle bundle) {
    return "E".equals(bundle.get(this.offset - 1));
  }

  protected String get(PrimitiveBundle bundle, Object[] keys) {
    if (keys.length != this.len) {
      throw new RuntimeException("Error: impossible vector lookup with keys " + Arrays.toString(keys));
    }
    if (!this.exists(bundle)) {
      return "";
    }
    return this._get(bundle, keys, 0, this.offset);
  }

  protected Object _mapping(PrimitiveBundle bundle, int k, int ix) {
    Map<Object, Object> o = new HashMap<>();
    Object[] keys = this.keysets.get(k).keys();
    boolean last = k == this.last;
    for (int i = 0; i < keys.length; i++) {
      Object key = keys[i];
      if (last) {
        // We're at the value level of the map, so lookup the corresponding string
        String val = bundle.get(this.offset + i + ix);
        if (!StringUtils.isEmpty(val)) {
          o.put(key, val);
        }
      } else {
        // Drill one level deeper
        Object val = this._mapping(bundle, k, ix + (i * this.factors[k]));
        o.put(key, val);
      }
    }
    return o;
  }

  private String _get(PrimitiveBundle bundle, Object[] keys, int ix, int k) {
    Object key = keys[ix];
    Object[] args = null;
    if (key instanceof Object[]) {
      args = (Object[]) key;
    } else {
      args = new Object[] { key };
    }
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        Object arg = args[i];
        KeyIndex<Object> keyset = this.keysets.get(ix);
        int j = keyset.get(arg);
        if (j == -1) {
          if (i != this.last) {
            continue;
          }

          return "";
        }
        int kk = k + j * this.factors[ix];
        String val = ix == this.last ? bundle.get(kk) : this._get(bundle, keys, ix + 1, kk);
        if (!StringUtils.isEmpty(val)) {
          return val;
        }
      }
    }
    return "";
  }

}
