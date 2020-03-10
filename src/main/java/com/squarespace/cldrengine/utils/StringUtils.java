package com.squarespace.cldrengine.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;

public class StringUtils {

  public static boolean isEmpty(String s) {
    return s == null || s.isEmpty();
  }

  public static String substring(String s, int i, int j) {
    if (isEmpty(s)) {
      return "";
    }
    int len = s.length();
    return (i <= j && i < len && j <= len) ? s.substring(i, j) : "";
  }

  public static List<String> split(String s, char sep) {
    List<String> res = new ArrayList<>();
    int i = 0;
    for (;;) {
      int j = s.indexOf(sep, i);
      if (j == -1) {
        res.add(s.substring(i));
        break;
      }
      res.add(s.substring(i, j));
      i = j + 1;
    }
    return res;
  }

  public static String firstChar(String s) {
    if (isEmpty(s)) {
      return "";
    }
    return s.substring(0, 1);
  }

  public static <T> String safeGet(Map<T, String> map, T key) {
    String value = map.get(key);
    return isEmpty(value) ? "" : value;
  }

  public static String lastChar(String s) {
    if (isEmpty(s)) {
      return "";
    }
    int len = s.length();
    return s.substring(len -1, len);
  }

  public static final String join(Collection<String> list, String sep) {
    StringBuilder buf = new StringBuilder();
    for (String s : list) {
      if (buf.length() > 0) {
        buf.append(sep);
      }
      buf.append(s);
    }
    return buf.toString();
  }

  public static final String reverseJoin(List<String> list, String sep) {
    StringBuilder buf = new StringBuilder();
    ListIterator<String> iter = list.listIterator(list.size());
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

  public static int[] intArray(String str) {
    return intArray(str, 36);
  }

  public static int[] intArray(String str, int base) {
    return Arrays.stream(parseArray(str, Integer.class, s -> Integer.valueOf(s, base)))
        .mapToInt(v -> v).toArray();
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

