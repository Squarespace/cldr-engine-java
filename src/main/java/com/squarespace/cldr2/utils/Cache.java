package com.squarespace.cldr2.utils;

import java.util.function.Function;

/**
 * Links an arrow function to an LRU cache. The function converts
 * a string to a value of type T. The string itself is used as
 * the cache key.
 *
 * Examples:
 *  * Caching a number or date pattern. The cache key is the string
 *    representation of the pattern.
 *  * Caching any object that is expensive to create, where the cache
 *    key identifies the type of object to cache.
 *
 * Ported from @phensley/cldr-utils src/cache.ts
 */
public class Cache<V> {

  private LRU<String, V> storage;
  private Function<String, V> builder;

  public Cache(Function<String, V> builder, int capacity) {
    this.builder = builder;
    this.storage = new LRU<>(capacity);
  }

  public int size() {
    return this.storage.size();
  }

  public V get(String raw) {
    V v = this.storage.get(raw);
    if (v == null) {
      v = this.builder.apply(raw);
      this.storage.set(raw, v);
    }
    return v;
  }
}
