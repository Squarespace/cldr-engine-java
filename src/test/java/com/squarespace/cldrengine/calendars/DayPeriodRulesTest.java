package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDateFields;
import com.squarespace.cldrengine.api.DateRawFormatOptions;
import com.squarespace.cldrengine.api.GregorianDate;

/**
 * DayPeriodRules below-first-boundary behavior.
 *
 * fa's dayperiod rules start at minute 60, so any time in 00:00-00:59 falls
 * below the first boundary and the binary search index is -1. The TS engine
 * reads keys[-1] as undefined and the flex dayperiod formatter falls back to
 * the am/pm (extended) dayperiod; the Java port used to throw an
 * ArrayIndexOutOfBoundsException here.
 *
 * Expected values were generated with the published 1.15.0 engine via
 * CLDRFramework (packages/cldr at tag v1.15.0), formatDateRaw pattern 'B'.
 */
public class DayPeriodRulesTest {

  private static final CLDR FA = CLDR.get("fa");
  private static final CLDR EN = CLDR.get("en");

  private static String flexDayPeriod(CLDR cldr, int hour, int minute) {
    GregorianDate d = cldr.Calendars.newGregorianDate(CalendarDateFields.build()
        .year(2020).month(1).day(6).hour(hour).minute(minute));
    return cldr.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("B"));
  }

  /** Crash case: below fa's first boundary (minute 60) fell back to am/pm. */
  @Test
  public void testFaBelowFirstBoundary() {
    // TS oracle: "ق.ظ." (abbreviated am), same as the 'a' pattern.
    assertEquals(flexDayPeriod(FA, 0, 30), "ق.ظ.");
    assertEquals(flexDayPeriod(FA, 0, 59), "ق.ظ.");
    assertEquals(flexDayPeriod(FA, 0, 0), "ق.ظ.");
  }

  /** Positive control: just past the first boundary resolves to the flex key. */
  @Test
  public void testFaFirstBoundary() {
    // TS oracle: "بامداد" (dawn).
    assertEquals(flexDayPeriod(FA, 1, 1), "بامداد");
    // TS oracle: "ظهر" (noon).
    assertEquals(flexDayPeriod(FA, 12, 0), "ظهر");
  }

  /** Unaffected locale control. */
  @Test
  public void testEn() {
    // TS oracle: "in the morning" (en's first boundary is minute 0, so the
    // flex dayperiod key is "morning").
    assertEquals(flexDayPeriod(EN, 0, 30), "in the morning");
    assertEquals(flexDayPeriod(EN, 0, 59), "in the morning");
    assertEquals(flexDayPeriod(EN, 1, 1), "in the morning");
  }

}
