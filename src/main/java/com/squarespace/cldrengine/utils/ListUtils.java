package com.squarespace.cldrengine.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ListUtils {

  public static <T> ArrayList<T> concat(List<T> a, List<T> b) {
    ArrayList<T> result = new ArrayList<>(a);
    result.addAll(b);
    return result;
  }
}
