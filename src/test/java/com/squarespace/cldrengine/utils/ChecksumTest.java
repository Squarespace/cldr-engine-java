package com.squarespace.cldrengine.utils;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ChecksumTest {

  @Test
  public void testVectors() {
    assertEquals(checksum(""), 0x811c9dc5);
    assertEquals(checksum("a"), 0xe40c292c);
    assertEquals(checksum("foobar"), 0xbf9cf968);
    assertEquals(checksum("hello world"), 0xd58b3fa7);

    // This is a UTF-16 checksum, not UTF-8
    assertEquals(checksum("\u2018hello\u2019"), 0xf92de24c);

  }

  private static int checksum(String s) {
    return new Checksum().update(s).get();
  }
}
