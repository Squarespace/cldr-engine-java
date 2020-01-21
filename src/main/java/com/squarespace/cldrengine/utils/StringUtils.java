package com.squarespace.cldrengine.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public class StringUtils {

  public static final String join(List<String> list, String sep) {
    StringBuilder buf = new StringBuilder();
    int size = list.size();
    for (int i = 0; i < size; i++) {
      if (i > 0) {
        buf.append(sep);
      }
      buf.append(list.get(i));
    }
    return buf.toString();
  }

  public static final String reverseJoin(List<String> list, String sep) {
    StringBuilder buf = new StringBuilder();
    ListIterator<String> iter = list.listIterator();
    while (iter.hasPrevious()) {
      if (buf.length() > 0) {
        buf.append(sep);
      }
      buf.append(iter.previous());
    }
    return buf.toString();
  }

  public static final String reverse(String s) {
    StringBuilder buf = new StringBuilder();
    for (int i = s.length() - 1; i >= 0; i--) {
      buf.append(s.charAt(i));
    }
    return buf.toString();
  }

  public static long[] longArray(String str) {
    return longArray(str, 36);
  }

  public static long[] longArray(String str, int base) {
    return Arrays.stream(parseArray(str, Long.class, s -> Long.valueOf(s, base)))
        .mapToLong(v -> v).toArray();
  }

  @SuppressWarnings("unchecked")
  public static <R> R[] parseArray(String str, Class<R> cls, Function<String, R> parse) {
    if (str.isEmpty() ) {
      return (R[]) Array.newInstance(cls, 0);
    }
    String[] raw = str.split("\\s+");
    R[] res = (R[]) Array.newInstance(cls, raw.length);
    for (int i = 0; i < raw.length; i++) {
      res[i] = parse.apply(raw[i]);
    }
    return res;
  }

}

