package com.squarespace.cldrengine.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Vector3Arrow<T, S, U> extends VectorArrowImpl {

  @SuppressWarnings("unchecked")
  public Vector3Arrow(int offset, KeyIndex<T> index1, KeyIndex<S> index2, KeyIndex<U> index3) {
    super(offset, Arrays.asList(
        (KeyIndex<Object>)index1,
        (KeyIndex<Object>)index2,
        (KeyIndex<Object>)index3));
  }

  public String get(PrimitiveBundle bundle, T key1, S key2, U key3) {
    return this.get(bundle, new Object[] { key1, key2, key3 });
  }

  public String get(PrimitiveBundle bundle, T[] key1, S key2, U key3) {
    return this.get(bundle, new Object[] { key1, key2, key3 });
  }

  public String get(PrimitiveBundle bundle, T[] key1, S[] key2, U key3) {
    return this.get(bundle, new Object[] { key1, key2, key3 });
  }

  public String get(PrimitiveBundle bundle, T[] key1, S[] key2, U[] key3) {
    return this.get(bundle, new Object[] { key1, key2, key3 });
  }

  public String get(PrimitiveBundle bundle, T[] key1, S key2, U[] key3) {
    return this.get(bundle, new Object[] { key1, key2, key3 });
  }

  public String get(PrimitiveBundle bundle, T key1, S[] key2, U[] key3) {
    return this.get(bundle, new Object[] { key1, key2, key3 });
  }

  public String get(PrimitiveBundle bundle, T key1, S key2, U[] key3) {
    return this.get(bundle, new Object[] { key1, key2, key3 });
  }

  @SuppressWarnings("unchecked")
  public Map<T, Map<S, Map<U, String>>> mapping(PrimitiveBundle bundle) {
    return this.exists(bundle) ? (Map<T, Map<S, Map<U, String>>>)this._mapping(bundle, 0, 0) : new HashMap<>();
  }
}
