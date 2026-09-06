package com.squarespace.cldrengine.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

/**
 * LRU regression tests, ported alongside @phensley/cldr-utils LRU
 * (packages/cldr-utils/src/lru.ts).
 *
 * Pins the doubly-linked-list integrity of the remove()/moveFront() path.
 * Previously remove() did {@code n.prev.next = n} (a no-op that left the
 * stale link in place) instead of {@code n.prev.next = n.next}, which
 * corrupted the list after any get() on a non-tail node and made
 * toString() loop forever (OOM).
 */
public class LRUTest {

  @Test
  public void testSetGetEvictOrder() {
    LRU<String, Integer> lru = new LRU<>(3);
    lru.set("a", 1);
    lru.set("b", 2);
    lru.set("c", 3);
    assertEquals(lru.size(), 3);
    assertEquals(lru.toString(), "c=3 b=2 a=1");

    // get() bumps the key to the front
    assertEquals(lru.get("a"), Integer.valueOf(1));
    assertEquals(lru.toString(), "a=1 c=3 b=2");

    // setting a 4th key evicts the least-recently-used (b)
    lru.set("d", 4);
    assertEquals(lru.size(), 3);
    assertEquals(lru.get("b"), null);
    assertEquals(lru.toString(), "d=4 a=1 c=3");
  }

  @Test
  public void testRemovePathKeepsListIntact() throws Exception {
    LRU<String, Integer> lru = new LRU<>(3);
    lru.set("a", 1);
    lru.set("b", 2);
    lru.set("c", 3);

    // get() on middle and tail nodes, then an evicting set: every step
    // removes and reinserts a node, which is where the corruption lived.
    lru.get("b");
    lru.get("a");
    lru.set("d", 4);

    // Bounded traversal of the internal list: it must walk exactly size()
    // distinct nodes and return to root. Pre-fix this looped (toString()
    // OOM'd); the bound keeps the test safe either way.
    Traversal t = traverse(lru, 16);
    assertTrue(t.terminatedAtRoot, "list did not return to root; visited: " + t.seen);
    assertFalse(t.revisited, "list revisits a node; visited: " + t.seen);
    assertEquals(t.seen.size(), lru.size());
    assertEquals(lru.toString(), "d=4 a=1 b=2");
  }

  @Test
  public void testZeroCapacity() {
    LRU<String, Integer> lru = new LRU<>(0);
    lru.set("a", 1);
    assertEquals(lru.size(), 0);
    assertEquals(lru.get("a"), null);
  }

  /** Result of a bounded walk of the LRU's internal doubly-linked list. */
  private static class Traversal {
    boolean terminatedAtRoot;
    boolean revisited;
    final Set<Object> seen = new HashSet<>();
  }

  /**
   * Walks root.next at most maxSteps times. A healthy list of n nodes
   * returns to root after exactly n steps; a corrupted one either revisits
   * a node or never reaches root within the bound.
   */
  private static Traversal traverse(LRU<?, ?> lru, int maxSteps) throws Exception {
    Traversal t = new Traversal();

    Field rootField = LRU.class.getDeclaredField("root");
    rootField.setAccessible(true);
    Object root = rootField.get(lru);

    Field nextField = root.getClass().getDeclaredField("next");
    nextField.setAccessible(true);

    Object n = nextField.get(root);
    for (int step = 0; step < maxSteps; step++) {
      if (n == root) {
        t.terminatedAtRoot = true;
        break;
      }
      if (!t.seen.add(n)) {
        t.revisited = true;
        break;
      }
      n = nextField.get(n);
    }
    return t;
  }
}
