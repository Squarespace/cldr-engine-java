package com.squarespace.cldrengine.utils;

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
 *
 * The cache is genuinely LRU-bounded: {@code capacity} is a hard limit
 * on the number of entries held. When the cache is full, inserting a
 * new key evicts the least-recently-used entry (looked-up or inserted
 * longest ago).
 *
 * get() performs a check-then-build under per-operation synchronization:
 * under concurrency, two threads can both observe a miss for the same
 * key and both invoke the builder. This is harmless — builders return
 * fresh immutable parsed objects, so both callers receive a valid value
 * and the last set wins.
 *
 * Builders must not return null: null is indistinguishable from a cache
 * miss, so a null value would be rebuilt on every call. None of the
 * current builders can return null.
 */
public class Cache<V> {

  private final LRU<String, V> storage;
  private final Function<String, V> builder;

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
