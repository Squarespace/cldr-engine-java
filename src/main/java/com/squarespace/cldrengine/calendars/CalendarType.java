package com.squarespace.cldrengine.calendars;

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

  private CalendarType(String value) {
    this.value = value;
  }

}

