package com.squarespace.cldr2.internal;

public interface Bundle extends PrimitiveBundle {

  String calendarSystem();
  String numberSystem();
  String languageScript();
  String languageRegion();

}
