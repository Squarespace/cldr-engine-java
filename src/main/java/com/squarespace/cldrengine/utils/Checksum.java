package com.squarespace.cldrengine.utils;

/**
 * FNV-1A incremental checksum 32-bit.
 */
public class Checksum {

  private static final long MASK = 0x00000000FFFFFFFFL;
  private static final int FNV1A_BASIS = 0x811C9DC5;

  private int v = FNV1A_BASIS;

  public Checksum update(String s) {
    int r = this.v;
    for (int i = 0; i < s.length(); i++) {
      r ^= s.charAt(i);
      r += ((r << 1) + (r << 4) + (r << 7) + (r << 8) + (r << 24));
    }
    this.v = r;
    return this;
  }

  /**
   * Return hex unsigned 32-bit representation.
   */
  public String get() {
    return Long.toString(this.v & MASK, 16);
  }

}
