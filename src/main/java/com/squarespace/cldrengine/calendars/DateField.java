package com.squarespace.cldrengine.calendars;

public class DateField {

  // Milliseconds from Unix epoch, adjusted by local timezone offset
  public static final int LOCAL_MILLIS = 0;
  // Date in Julian days
  public static final int JULIAN_DAY = 1;
  public static final int ERA = 2;
  public static final int EXTENDED_YEAR = 3;
  public static final int YEAR = 4;
  public static final int YEAR_WOY = 5;
  public static final int WEEK_OF_YEAR = 6;
  public static final int MONTH = 7;
  public static final int WEEK_OF_MONTH = 8;
  public static final int DAY_OF_YEAR = 9;
  public static final int DAY_OF_MONTH = 10;
  public static final int DAY_OF_WEEK = 11;
  public static final int DAY_OF_WEEK_IN_MONTH = 12;
  public static final int MILLIS_IN_DAY = 13;
  public static final int AM_PM = 14;
  public static final int HOUR_OF_DAY = 15;
  public static final int HOUR = 16;
  public static final int MINUTE = 17;
  public static final int SECOND = 18;
  public static final int MILLIS = 19;
  public static final int TZ_OFFSET = 20;
  public static final int IS_LEAP = 21;
  public static final int IS_DST = 22;

  // Length of array to hold all fields
  public static final int LENGTH = 23;

}
