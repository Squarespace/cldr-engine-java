package com.squarespace.cldrengine.utils;

import java.util.List;
import java.util.ListIterator;

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
}

