package com.squarespace.cldrengine.utils;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HeapTest {

  @Test
  public void testBasic() {
    int limit = 5;
    String[] strings = new String[limit];
    for (int i = 0; i < limit; i++) {
      strings[i] = rand(10);
    }

    Heap<String> heap = new Heap<>(strings, String::compareTo);
    String[] res = new String[limit];
    int j = 0;
    while (!heap.empty()) {
      res[j] = heap.pop();
      j++;
    }

    // Compare to original sorted array
    Arrays.sort(strings);
    Assert.assertEquals(res, strings);
  }

  private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";
  private static final Random RAND = new Random(1309L);

  private static String rand(int n) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < n; i++) {
      int j = Math.abs(RAND.nextInt()) % CHARS.length();
      char r = CHARS.charAt(j);
      buf.append(r);
    }
    return buf.toString();
  }
}
