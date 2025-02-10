package com.squarespace.cldrengine.internal;

public class MetaBase {

  static final KeyIndex<DateTimePatternFieldType> KEY_DATE_TIME_PATTERN_FIELD = new KeyIndex<DateTimePatternFieldType>(new DateTimePatternFieldType[] {
      DateTimePatternFieldType.ERA,
      DateTimePatternFieldType.YEAR,
      DateTimePatternFieldType.MONTH,
      DateTimePatternFieldType.DAY,
      DateTimePatternFieldType.DAYPERIOD,
      DateTimePatternFieldType.DAYPERIOD_FLEX,
      DateTimePatternFieldType.HOUR24,
      DateTimePatternFieldType.HOUR12,
      DateTimePatternFieldType.MINUTE,
      DateTimePatternFieldType.SECOND
  });

}
