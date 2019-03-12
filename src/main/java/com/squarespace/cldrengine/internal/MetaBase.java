package com.squarespace.cldrengine.internal;

public class MetaBase {

  static final KeyIndex<DateTimePatternFieldType> KEY_DATE_TIME_PATTERN_FIELD = new KeyIndex<DateTimePatternFieldType>(new DateTimePatternFieldType[] {
      DateTimePatternFieldType.YEAR  ,
      DateTimePatternFieldType.MONTH  ,
      DateTimePatternFieldType.DAY  ,
      DateTimePatternFieldType.DAYPERIOD  ,
      DateTimePatternFieldType.HOUR  ,
      DateTimePatternFieldType.MINUTE  ,
      DateTimePatternFieldType.SECOND
  });

}
