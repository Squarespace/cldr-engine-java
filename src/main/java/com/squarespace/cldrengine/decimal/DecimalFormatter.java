package com.squarespace.cldrengine.decimal;

public interface DecimalFormatter<T> {

  void add(String c);
  T render();

}
