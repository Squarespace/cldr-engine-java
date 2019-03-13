package com.squarespace.cldrengine.calendars;

/**
 * Construct a date using the rules of the Gregorian calendar.
 *
 * type: gregory
 */
public class GregorianDate extends CalendarDate {

  protected GregorianDate(CalendarType type, int firstDay, int minDays) {
    super(type, firstDay, minDays);
  }

  protected long monthStart(long eyear, double month, boolean useMonth) {
    boolean isLeap = eyear % 4 == 0;
    long y = eyear - 1;
    long jd = 365 * y + Math.floorDiv(y, 4) + (CalendarConstants.JD_GREGORIAN_EPOCH - 3);
    if (eyear >= CalendarConstants.JD_GREGORIAN_CUTOVER_YEAR) {
      isLeap = isLeap && ((eyear % 100 != 0) || (eyear % 400 == 0));
      jd += Math.floorDiv(y, 400) - Math.floorDiv(y, 100) + 2;
    }
    if (month != 0) {
      int m = (int)Math.floor(month);
      double d = month - (double)m;
      jd += MONTH_COUNT[m][isLeap ? 3 : 2];

      // Check if there is a fractional month part, and if so add the number
      // of the days in the next month multiplied by the fraction
      if (d != 0) {
        // note: the 'month' parameter must always be <= # months in the calendar
        // year, so <= 12 in this case.
        jd += d * MONTH_COUNT[m + 1][isLeap ? 1 : 0];
      }
    }
    return jd;
  }

  private static final int[][] MONTH_COUNT = new int[][] {
    new int[] { 31,  31,   0,   0 }, // Jan
    new int[] { 28,  29,  31,  31 }, // Feb
    new int[] { 31,  31,  59,  60 }, // Mar
    new int[] { 30,  30,  90,  91 }, // Apr
    new int[] { 31,  31, 120, 121 }, // May
    new int[] { 30,  30, 151, 152 }, // Jun
    new int[] { 31,  31, 181, 182 }, // Jul
    new int[] { 31,  31, 212, 213 }, // Aug
    new int[] { 30,  30, 243, 244 }, // Sep
    new int[] { 31,  31, 273, 274 }, // Oct
    new int[] { 30,  30, 304, 305 }, // Nov
    new int[] { 31,  31, 334, 335 }  // Dec
  };

}
