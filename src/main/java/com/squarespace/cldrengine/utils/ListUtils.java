package com.squarespace.cldrengine.utils;

import java.util.ArrayList;
import java.util.Collection;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ListUtils {

  public static <T> ArrayList<T> concat(Collection<T> a, Collection<T> b) {
    ArrayList<T> result = new ArrayList<>(a);
    result.addAll(b);
    return result;
  }
}
