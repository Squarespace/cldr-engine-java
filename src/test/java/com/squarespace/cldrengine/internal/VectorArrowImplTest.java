package com.squarespace.cldrengine.internal;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * VectorArrowImpl._get fallback-arg regression test.
 *
 * Mirrors VectorArrowImpl.prototype._get (packages/cldr-core/src/schema/
 * schema/arrows.ts, tag v1.15.0), which computes
 * {@code const last = args.length - 1} (last FALLBACK ARG) and only gives
 * up when that arg misses. The Java port compared against the last
 * DIMENSION (this.last), so a fallback chain longer than the arrow's
 * dimensionality gave up early: a 4-element chain with only the 4th
 * element present returned "" instead of the 4th value.
 */
public class VectorArrowImplTest {

  /**
   * Bundle layout for a 1x1x1 arrow constructed with offset 0:
   *   0: "E" (existence header, checked via bundle.get(offset - 1))
   *   1: the single value cell (address = offset + 0*1 + 0*1 + 0*1)
   */
  private static PrimitiveBundle bundle() {
    return new PrimitiveBundle() {
      @Override
      public String id() {
        return "test";
      }

      @Override
      public String language() {
        return "test";
      }

      @Override
      public String region() {
        return "";
      }

      @Override
      public String get(int offset) {
        if (offset == 0) {
          return "E";
        }
        if (offset == 1) {
          return "VALUE";
        }
        return "";
      }
    };
  }

  /** 1x1x1 arrow whose only dim-0 key is "f4". */
  private static Vector3Arrow<String, String, String> arrow() {
    return new Vector3Arrow<>(0,
        new KeyIndex<>(new String[] { "f4" }),
        new KeyIndex<>(new String[] { "b" }),
        new KeyIndex<>(new String[] { "c" }));
  }

  @Test
  public void testFourArgFallbackChainUsesFourth() {
    // Only the 4th fallback arg exists in the keyset. Pre-fix, the loop
    // gave up when i hit the last DIMENSION (2) without trying arg 3.
    String[] fallback = { "f1", "f2", "f3", "f4" };
    assertEquals(arrow().get(bundle(), fallback, "b", "c"), "VALUE");
  }

  @Test
  public void testShorterFallbackChains() {
    // 3-element chain, only the 3rd exists: worked before the fix too
    // (this.last == 2 coincided with args.length - 1), must keep working.
    assertEquals(arrow().get(bundle(), new String[] { "f1", "f2", "f4" }, "b", "c"), "VALUE");

    // 2-element chain with the hit second.
    assertEquals(arrow().get(bundle(), new String[] { "f1", "f4" }, "b", "c"), "VALUE");

    // Single-key hit and miss.
    assertEquals(arrow().get(bundle(), "f4", "b", "c"), "VALUE");
    assertEquals(arrow().get(bundle(), "f1", "b", "c"), "");

    // Chain where no arg exists: gives up on the last fallback arg.
    assertEquals(arrow().get(bundle(), new String[] { "f1", "f2" }, "b", "c"), "");
  }
}
