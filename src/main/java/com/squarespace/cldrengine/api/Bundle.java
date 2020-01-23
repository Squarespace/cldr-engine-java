package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.internal.PrimitiveBundle;
import com.squarespace.cldrengine.plurals.PluralRules;

public interface Bundle extends PrimitiveBundle {

  String calendarSystem();
  String numberSystem();
  String languageScript();
  String languageRegion();
  PluralRules plurals();

}
