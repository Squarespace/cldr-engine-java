package com.squarespace.cldrengine.api;

class CalendarConstants {

  public static final int ISO8601_MIN_DAYS = 4;

  // Min and max Julian day form a range of full years whose midpoint is the
  // UNIX epoch Jan 1 1970
  public static final long JD_MIN = 0;               // Mon Jan  1 4713 BC
  public static final long JD_UNIX_EPOCH = 2440588;  // Thu Jan  1 1970 AD
  public static final long JD_MAX = 4881503;         // Fri Dec 31 8652 AD

  // Date of cutover to the Gregorian calendar, Oct 15, 1582
  public static final long JD_GREGORIAN_CUTOVER = 2299161;
  public static final long JD_GREGORIAN_CUTOVER_YEAR = 1582;

  // Julian day for Jan 1, 1 first day of the Gregorian calendar common era
  public static final long JD_GREGORIAN_EPOCH = 1721426;

  // Julian day for Mar 21, 622 first day of the Persian calendar
  public static final long JD_PERSIAN_EPOCH = 1948320;

  public static final long BUDDHIST_ERA_START = -543;

  public static final int ONE_SECOND_MS = 1000;
  public static final int ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
  public static final int ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
  public static final int ONE_DAY_MS = 24 * ONE_HOUR_MS;

}
