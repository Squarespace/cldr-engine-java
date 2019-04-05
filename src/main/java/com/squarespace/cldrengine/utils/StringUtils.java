package com.squarespace.cldrengine.utils;

import java.util.List;

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

}
