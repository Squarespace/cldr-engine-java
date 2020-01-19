package com.squarespace.cldrengine.utils;

import java.util.concurrent.ConcurrentHashMap;
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
 * Modified to use ConcurrentHashMap
 */
public class Cache<V> {

  private ConcurrentHashMap<String, V> storage;
  private Function<String, V> builder;

  public Cache(Function<String, V> builder, int initialCapacity) {
    this.builder = builder;
    this.storage = new ConcurrentHashMap<>(initialCapacity);
  }

  public int size() {
    return this.storage.size();
  }

  public V get(String raw) {
    return this.storage.computeIfAbsent(raw, this.builder);
  }
}
