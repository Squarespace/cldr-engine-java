package com.squarespace.cldrengine.api;

public interface MessageArgConverter {

  String asString(Object arg);

  Decimal asDecimal(Object arg);

}
