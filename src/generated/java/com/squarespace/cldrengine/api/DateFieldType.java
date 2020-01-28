package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum DateFieldType implements StringEnum<DateFieldType> {

  ERA("era"),
  YEAR("year"),
  QUARTER("quarter"),
  MONTH("month"),
  WEEK("week"),
  WEEKDAY("weekday"),
  WEEKDAYOFMONTH("weekdayOfMonth"),
  SUN("sun"),
  MON("mon"),
  TUE("tue"),
  WED("wed"),
  THU("thu"),
  FRI("fri"),
  SAT("sat"),
  DAY("day"),
  DAYPERIOD("dayperiod"),
  HOUR("hour"),
  MINUTE("minute"),
  SECOND("second"),
  ZONE("zone")
  ;

  private static final Map<String, DateFieldType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(DateFieldType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private DateFieldType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static DateFieldType fromString(String s) {
    return REVERSE.get(s);
  }
}
