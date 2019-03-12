package com.squarespace.cldrengine.internal;

public interface Bundle extends PrimitiveBundle {

  String calendarSystem();
  String numberSystem();
  String languageScript();
  String languageRegion();

}
