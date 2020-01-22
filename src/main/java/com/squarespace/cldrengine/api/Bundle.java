package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.internal.PrimitiveBundle;

public interface Bundle extends PrimitiveBundle {

  String calendarSystem();
  String numberSystem();
  String languageScript();
  String languageRegion();

}
