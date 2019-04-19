package com.squarespace.cldrengine.calendars;

public class CalendarConstants {

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

  public static final int ONE_DAY_MS = 86400000;

}