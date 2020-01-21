package com.squarespace.cldrengine.calendars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The internal type name for Gregorian calendar is "gregory" so that it can fit
 * into a language tag ("zh-u-ca-gregory") as "gregorian" exceeds the 8-char
 * limit.
 * See https://www.unicode.org/reports/tr35/#Key_And_Type_Definitions_
 */
public enum CalendarType {

  BUDDHIST("buddhist"),
  GREGORY("gregory"),
  ISO8601("iso8601"),
  JAPANESE("japanese"),
  PERSIAN("persian")
  ;

  public final String value;

  private static final Map<String, CalendarType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(CalendarType.values()).forEach(t -> REVERSE.put(t.value, t));
  }

  private CalendarType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static CalendarType fromString(String s) {
    return REVERSE.get(s);
  }
}

