package com.squarespace.cldrengine.internal;

import com.squarespace.cldrengine.utils.Pair;

public class DigitsArrow<R> {

  private static final Pair<String, Integer> EMPTY = Pair.of("", 0);

  private static int[] DIGITS = new int[] {
      1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
  };
  private static int DIGITS_LEN = DIGITS.length;

  private int offset;
  private KeyIndex<R> index;

  public DigitsArrow(int offset, KeyIndex<R> index) {
    this.offset = offset;
    this.index = index;
  }

  public Pair<String, Integer> get(PrimitiveBundle bundle, R key, int digits) {
    if (digits > DIGITS_LEN) {
      digits = DIGITS_LEN;
    }
    if (digits > 0) {
      int i = this.index.get(key);
      if (i != -1) {
        int k = this.offset + (i * DIGITS_LEN) + ((digits - 1) * 2);
        String p = bundle.get(k);
        String d = bundle.get(k + 1);
        return Pair.of(p, Integer.valueOf(d));
      }
    }
    return EMPTY;
  }

}
