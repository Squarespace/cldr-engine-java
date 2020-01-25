package com.squarespace.cldrengine.utils;

import java.util.function.Supplier;

public class TypeUtils {

  public static <T> T defaulter(T o, Supplier<T> supplier) {
    return o == null ? supplier.get() : o;
  }

}
