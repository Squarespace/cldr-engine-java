package com.squarespace.cldrengine.utils;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

/**
 * Cache is an LRU-bounded cache that builds a value on a miss and evicts
 * the least-recently-used entry when capacity is exceeded. Counts builder
 * invocations to observe evictions and rebuilds. Mirrors the TS
 * @phensley/cldr-utils Cache (packages/cldr-utils/src/cache.ts).
 */
public class CacheTest {

  @Test
  public void testCapacityEvictionAndRebuild() {
    AtomicInteger builds = new AtomicInteger();
    Cache<Integer> cache = new Cache<>(s -> {
      builds.incrementAndGet();
      return Integer.valueOf(s.length());
    }, 3);

    // Fill the cache to capacity.
    assertEquals(cache.get("a"), Integer.valueOf(1));
    assertEquals(cache.get("b"), Integer.valueOf(1));
    assertEquals(cache.get("c"), Integer.valueOf(1));
    assertEquals(cache.size(), 3);
    assertEquals(builds.get(), 3);

    // Touch "a" so it is the most-recently-used key.
    assertEquals(cache.get("a"), Integer.valueOf(1));
    assertEquals(builds.get(), 3);

    // The 4th distinct key evicts the least-recently-used entry (b),
    // not the recently-touched key (a).
    assertEquals(cache.get("d"), Integer.valueOf(1));
    assertEquals(cache.size(), 3);
    assertEquals(builds.get(), 4);
    assertEquals(cache.get("a"), Integer.valueOf(1));
    assertEquals(builds.get(), 4);

    // A 5th distinct key evicts the next-least-recently-used entry (c);
    // "a" is still cached.
    assertEquals(cache.get("e"), Integer.valueOf(1));
    assertEquals(cache.size(), 3);
    assertEquals(builds.get(), 5);
    assertEquals(cache.get("a"), Integer.valueOf(1));
    assertEquals(builds.get(), 5);

    // Evicted keys (b, c) rebuild on re-get; capacity never exceeds 3.
    assertEquals(cache.get("b"), Integer.valueOf(1));
    assertEquals(builds.get(), 6);
    assertEquals(cache.get("c"), Integer.valueOf(1));
    assertEquals(builds.get(), 7);
    assertEquals(cache.size(), 3);
  }
}
