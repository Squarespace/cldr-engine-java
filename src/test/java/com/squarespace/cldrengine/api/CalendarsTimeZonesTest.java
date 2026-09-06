package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.calendars.ZoneInfo;

/**
 * Port of the TS api/calendars.ts timeZoneFromUTC / timeZoneFromWall methods
 * (packages/cldr-core/src/api/calendars.ts, tag v1.15.0), which delegate to
 * @phensley/timezone TZ.fromUTC / TZ.fromWall.
 *
 * Expected offset/dst/abbr/timeZoneId values were generated with the published
 * 1.15.0 engine via CLDRFramework (packages/cldr at tag v1.15.0). The stableId
 * and metaZoneId fields are Java enrichments consistent with
 * TimeZoneData.zoneInfoFromUTC (used by CalendarDate).
 */
public class CalendarsTimeZonesTest {

  private static final CLDR EN = CLDR.get("en");

  /**
   * Assert this zone info matches the expected timezone info, ignoring the
   * timezone identifier, which is asserted separately where it matters.
   */
  private static void assertZoneInfo(ZoneInfo info, String stableId, String metaZoneId,
      String abbr, int offset, boolean dst) {
    assertEquals(info.stableId, stableId);
    assertEquals(info.metaZoneId, metaZoneId);
    assertEquals(info.abbr, abbr);
    assertEquals(info.offset, offset);
    assertEquals(info.dst, dst);
  }

  /**
   * Fall back on 2025-11-02 in America/Vancouver. 1762074000000 is the
   * transition instant 2025-11-02T09:00:00Z, when the clock has just moved
   * back from 2:00 AM PDT to 1:00 AM PST.
   *
   * TS oracle: {"abbr":"PST","dst":0,"offset":-28800000,"zoneid":"America/Vancouver"}
   */
  @Test
  public void testTimeZoneFromUTCFallBack() {
    ZoneInfo info = EN.Calendars.timeZoneFromUTC(1762074000000L, "America/Vancouver");
    assertEquals(info.timeZoneId, "America/Vancouver");
    assertZoneInfo(info, "America/Vancouver", "America_Pacific", "PST", -28800000, false);
    assertFalse(info.dst);
  }

  /**
   * The same wall clock time may occur twice when the clock jumps backwards.
   * On 2025-11-02 in America/Vancouver the wall time 1:30 AM occurs once in
   * PDT (before 2:00 AM) and again in PST (after the clock is set back).
   * The wall timestamp 1762047000000 (01:30) resolves to the earlier instant
   * using the pre-transition offset.
   *
   * TS oracle: [1762072200000,{"abbr":"PDT","dst":1,"offset":-25200000,"zoneid":"America/Vancouver"}]
   */
  @Test
  public void testTimeZoneFromWallAmbiguous() {
    Pair<Long, ZoneInfo> res = EN.Calendars.timeZoneFromWall(1762047000000L, "America/Vancouver");
    assertEquals(res._1.longValue(), 1762072200000L);
    ZoneInfo info = res._2;
    assertEquals(info.timeZoneId, "America/Vancouver");
    assertZoneInfo(info, "America/Vancouver", "America_Pacific", "PDT", -25200000, true);
    assertTrue(info.dst);
  }

  /**
   * An unambiguous wall time resolves to wall minus the raw offset.
   * TS oracle: timeZoneFromWall(1762074000000, 'America/Vancouver')
   *   -> [1762102800000,{"abbr":"PST","dst":0,"offset":-28800000,"zoneid":"America/Vancouver"}]
   */
  @Test
  public void testTimeZoneFromWallUnambiguous() {
    Pair<Long, ZoneInfo> res = EN.Calendars.timeZoneFromWall(1762074000000L, "America/Vancouver");
    assertEquals(res._1.longValue(), 1762102800000L);
    assertZoneInfo(res._2, "America/Vancouver", "America_Pacific", "PST", -28800000, false);
  }

  /**
   * A spring-forward gap: on 2026-03-08 in America/Vancouver the wall time
   * 02:30 does not exist. The resolved UTC makes the local time 3:30 AM PDT.
   * TS oracle: [1772965800000,{"abbr":"PDT","dst":1,"offset":-25200000,"zoneid":"America/Vancouver"}]
   */
  @Test
  public void testTimeZoneFromWallGap() {
    Pair<Long, ZoneInfo> res = EN.Calendars.timeZoneFromWall(1772937000000L, "America/Vancouver");
    assertEquals(res._1.longValue(), 1772965800000L);
    assertZoneInfo(res._2, "America/Vancouver", "America_Pacific", "PDT", -25200000, true);
  }

  /**
   * Daylight savings time case. TS oracle:
   * timeZoneFromUTC(1752537600000, 'America/Vancouver') -> PDT, -25200000.
   */
  @Test
  public void testTimeZoneFromUTCDaylight() {
    ZoneInfo info = EN.Calendars.timeZoneFromUTC(1752537600000L, "America/Vancouver");
    assertEquals(info.timeZoneId, "America/Vancouver");
    assertZoneInfo(info, "America/Vancouver", "America_Pacific", "PDT", -25200000, true);
    assertTrue(info.dst);
  }

  /**
   * A timezone alias resolves to the canonical zone id. TS oracle:
   * timeZoneFromUTC(1752537600000, 'US/Pacific')
   *   -> {"abbr":"PDT","dst":1,"offset":-25200000,"zoneid":"America/Los_Angeles"}
   */
  @Test
  public void testTimeZoneFromUTCZoneAlias() {
    ZoneInfo info = EN.Calendars.timeZoneFromUTC(1752537600000L, "US/Pacific");
    assertEquals(info.timeZoneId, "America/Los_Angeles");
    assertZoneInfo(info, "US/Pacific", "America_Pacific", "PDT", -25200000, true);
  }

  /**
   * Zone ids are case-insensitive, matching the @phensley/timezone link index.
   */
  @Test
  public void testTimeZoneFromUTCCaseInsensitive() {
    ZoneInfo info = EN.Calendars.timeZoneFromUTC(1752537600000L, "america/vancouver");
    assertEquals(info.timeZoneId, "America/Vancouver");
    assertZoneInfo(info, "america/vancouver", "America_Pacific", "PDT", -25200000, true);
  }

  /**
   * Fixed-offset zones have no metazone id. TS oracle:
   * timeZoneFromUTC(1736899200000, 'Etc/GMT+1')
   *   -> {"abbr":"-01","dst":0,"offset":-3600000,"zoneid":"Etc/GMT+1"}
   */
  @Test
  public void testTimeZoneFromUTCFixedOffset() {
    ZoneInfo info = EN.Calendars.timeZoneFromUTC(1736899200000L, "Etc/GMT+1");
    assertEquals(info.timeZoneId, "Etc/GMT+1");
    assertEquals(info.metaZoneId, "");
    assertZoneInfo(info, "Etc/GMT+1", "", "-01", -3600000, false);
  }

  /**
   * An unknown zone id returns null for both methods, mirroring the TS
   * undefined result.
   */
  @Test
  public void testUnknownZone() {
    assertNull(EN.Calendars.timeZoneFromUTC(1762074000000L, "Bogus/Zone"));
    assertNull(EN.Calendars.timeZoneFromWall(1762047000000L, "Bogus/Zone"));
  }

  /**
   * A null zone id is equivalent to an unknown zone id and returns null.
   */
  @Test
  public void testNullZoneId() {
    assertNull(EN.Calendars.timeZoneFromUTC(1762074000000L, null));
    assertNull(EN.Calendars.timeZoneFromWall(1762047000000L, null));
  }

}
