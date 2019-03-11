package com.squarespace.cldr2.internal;

public interface PrimitiveBundle {

  String id();
  String language();
  String region();
  String get(int offset);

}
