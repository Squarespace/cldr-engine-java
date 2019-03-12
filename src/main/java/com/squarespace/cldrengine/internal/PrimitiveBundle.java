package com.squarespace.cldrengine.internal;

public interface PrimitiveBundle {

  String id();
  String language();
  String region();
  String get(int offset);

}
