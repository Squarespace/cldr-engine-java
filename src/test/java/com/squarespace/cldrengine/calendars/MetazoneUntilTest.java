package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateFormatOptions;

/**
 * Verifies that metazone `untils` transition times are interpreted as epoch
 * milliseconds in the zoneinfo of the display date. CLDR metazone data stores
 * the transition times as epoch seconds; the Java loader must scale them to
 * milliseconds before comparing against a milliseconds UTC timestamp
 * (mirroring the TypeScript engine, which maps raw untils via
 * `numarray(raw.untils, 36).map(n => n === -1 ? n : n * 1000)`).
 *
 * All expected values below were generated with the TypeScript oracle
 * (CLDRFramework @ v1.15.0, packages/cldr at tag v1.15.0):
 *   framework.get('en').Calendars.formatDate(
 *     { date: <epochMillis>, zoneId: <zone> }, { skeleton: 'vvvv' })
 */
public class MetazoneUntilTest {

  private static final CLDR EN = CLDR.get("en");

  private static final long JUN_1_1985_UTC = 486432000000L;    // 1985-06-01T00:00:00Z
  private static final long JUN_1_2023_UTC = 1685577600000L;   // 2023-06-01T00:00:00Z

  private static final String WHITEHORSE = "America/Whitehorse";
  private static final String TRIPOLI = "Africa/Tripoli";

  /**
   * Format the given UTC timestamp in the given zone with the vvvv skeleton
   * (long generic non-location zone name). The expected values are the full
   * oracle strings, e.g. "17 Pacific Time" (hour + zone name), matching the
   * TypeScript engine output for the same input and skeleton.
   */
  private static String vvvv(long utc, String zone) {
    CalendarDate date = EN.Calendars.toGregorianDate(utc, zone);
    return EN.Calendars.formatDate(date, DateFormatOptions.build().skeleton("vvvv"));
  }

  /**
   * America/Whitehorse switched to the Yukon time metazone only in 2020.
   * In 1985 it used the Pacific metazone: TS oracle "17 Pacific Time".
   */
  @Test
  public void testWhitehorse1985Pacific() {
    assertEquals(vvvv(JUN_1_1985_UTC, WHITEHORSE), "17 Pacific Time");
  }

  /**
   * Africa/Tripoli used Central European Time in 1985: TS oracle
   * "02 Central European Time".
   */
  @Test
  public void testTripoli1985CentralEuropean() {
    assertEquals(vvvv(JUN_1_1985_UTC, TRIPOLI), "02 Central European Time");
  }

  /**
   * Modern (post-2020) Whitehorse is inside an open-ended metazone range; the
   * "-1" sentinel terminates the record. The metazone has no generic non-
   * location name in en, so the formatter falls back to the localized GMT
   * offset: TS oracle "17 GMT-07:00".
   */
  @Test
  public void testWhitehorse2023OpenEndedRange() {
    assertEquals(vvvv(JUN_1_2023_UTC, WHITEHORSE), "17 GMT-07:00");
  }

  /**
   * Africa/Tripoli's metazone changed from Central European Time to Eastern
   * European Time in 2012. Modern timestamps use the newer metazone: TS
   * oracle "02 Eastern European Time".
   */
  @Test
  public void testTripoli2023EasternEuropean() {
    assertEquals(vvvv(JUN_1_2023_UTC, TRIPOLI), "02 Eastern European Time");
  }
}
