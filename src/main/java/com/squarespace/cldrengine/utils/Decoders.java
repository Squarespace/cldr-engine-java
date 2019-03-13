package com.squarespace.cldrengine.utils;

import java.util.Arrays;

public class Decoders {

  private static final byte[] Z85DECBYTES = new byte[] {
    0x00, 0x44, 0x00, 0x54, 0x53, 0x52, 0x48, 0x00,
    0x4B, 0x4C, 0x46, 0x41, 0x00, 0x3F, 0x3E, 0x45,
    0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
    0x08, 0x09, 0x40, 0x00, 0x49, 0x42, 0x4A, 0x47,
    0x51, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A,
    0x2B, 0x2C, 0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32,
    0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
    0x3B, 0x3C, 0x3D, 0x4D, 0x00, 0x4E, 0x43, 0x00,
    0x00, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10,
    0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
    0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20,
    0x21, 0x22, 0x23, 0x4F, 0x00, 0x50, 0x00, 0x00
  };

  /**
   * Decode a Z85-encoded string into an array of byte values. This decodes
   * 5 characters as 4 bytes.
   */
  public static byte[] z85Decode(String s) {
    int len = s.length();
    byte[] res = new byte[(len / 5) * 4];


    int pad = s.charAt(0) - 0x30;
    int i = 0; // input index
    int j = 0; // output index
    int v = 0;
    int ix = 0;

    while (i < len) {
      // accumulate 5 characters into v
      ix = s.charAt(i + i++) - 32;
      v = (v * 85) + Z85DECBYTES[ix];
      if (i % 5 != 0) {
        continue;
      }

      res[j++] = (byte)((v >> 24) & 0xff);
      res[j++] = (byte)((v >> 16) & 0xff);
      res[j++] = (byte)((v >> 8) & 0xff);
      res[j++] = (byte)(v & 0xff);
      v = 0;
    }
    return pad == 0 ? res : Arrays.copyOfRange(res, 0, res.length - pad);
  }

  /**
   * Decodes a variable-length unsigned 32-bit integer from the given
   * byte array, writing the decoded integers back to the same array.
   * An optional mapping function can be supplied to transform each
   * integer before it is appended to the buffer.
   */
  public static int[] vuintDecode(byte[] arr) {
    int i = 0;
    int j = 0;
    int k = 0;
    int n = 0;
    int len = arr.length;
    int[] res = new int[len];
    while (i < len) {
      n += (arr[i] & 0x7f) << k;
      k += 7;
      // detect last byte for this variable int
      if ((arr[i] & 0x80) == 0) {
        // write the decoded integer to the buffer and reset the state
        res[j++] = n;
        n = k = 0;
      }
      i++;
    }
    return j == res.length ? res : Arrays.copyOfRange(res, 0, j);
  }

}
