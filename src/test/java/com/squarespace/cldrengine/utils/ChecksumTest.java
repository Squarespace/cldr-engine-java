package com.squarespace.cldrengine.utils;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ChecksumTest {

  @Test
  public void testVectors() {
    assertEquals(checksum(""), "811c9dc5");
    assertEquals(checksum("a"), "e40c292c");
    assertEquals(checksum("foobar"), "bf9cf968");
    assertEquals(checksum("hello world"), "d58b3fa7");

    // This is a UTF-16 checksum, not UTF-8
    assertEquals(checksum("\u2018hello\u2019"), "f92de24c");

  }

  private static String checksum(String s) {
    return new Checksum().update(s).get();
  }
}
