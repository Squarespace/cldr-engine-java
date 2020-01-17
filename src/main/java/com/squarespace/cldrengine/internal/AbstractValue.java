package com.squarespace.cldrengine.internal;

import com.squarespace.cldrengine.parsing.WrapperPattern;

public interface AbstractValue<R> {

  int length();
  void add(String type, String value);
  String get(int number);
  void append(R value);
  void insert(int i, String type, String value);
  R render();
  void reset();
  R join(R[] elems);
  void wrap(WrapperPattern pattern, R[] args);
  R empty();
}
