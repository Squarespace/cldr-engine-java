package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.Pair;

/**
 * Tests for TimeZoneData.fromWall, the wall-clock (DST disambiguation)
 * lookup. Expected values are derived from the @phensley/timezone v1.15.0
 * oracle via node, never computed by hand.
 */
public class TimeZoneDataFromWallTest {

  private static final long HOUR = 60 * 60 * 1000;

  /**
   * Wall time during the fall-back DST transition is ambiguous; the
   * earlier occurrence (pre-transition offset) is returned.
   *
   * America/Vancouver 2025-11-02 01:30 AM wall (PDT -07:00) occurs again
   * at 1:30 AM PST (-08:00) after the 2:00 AM fall-back.
   *
   * Oracle: wall=1762047000000 -> utc=1762072200000 (-25200000 = -07:00, PDT)
   */
  @Test
  public void testFallBackResolvesToEarlier() {
    long wall = 1762047000000L;
    Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall("America/Vancouver", wall);
    assertEquals(r._1.longValue(), 1762072200000L);
    assertEquals(r._2.offset, -7 * HOUR);
    assertEquals(r._2.abbr, "PDT");
    assertEquals(r._2.dst, 1);
    // resolved local time is exactly the wall time
    assertEquals(r._1 + r._2.offset, wall);
  }

  /**
   * Wall time inside the spring-forward gap is impossible; the returned
   * UTC/offset pair resolves to the post-jump local time.
   *
   * America/New_York 2025-03-09 02:30 AM wall does not exist (2:00-2:59 AM
   * skipped). The result places the local clock at 3:30 AM EDT.
   *
   * Oracle: wall=1741487400000 -> utc=1741505400000 (-14400000 = -04:00, EDT)
   */
  @Test
  public void testSpringGap() {
    long wall = 1741487400000L;
    Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall("America/New_York", wall);
    assertEquals(r._1.longValue(), 1741505400000L);
    assertEquals(r._2.offset, -4 * HOUR);
    assertEquals(r._2.abbr, "EDT");
    assertEquals(r._2.dst, 1);
    // the resolved local time is the post-jump time, 3:30 AM
    assertEquals(r._1 + r._2.offset, wall + HOUR);
  }

  /**
   * New York 2020 fall-back: 1:30 AM occurs before the boundary (EDT)
   * and again after (EST); the earlier occurrence is returned.
   *
   * Oracle: wall=1604194200000 -> utc=1604208600000 (-04:00, EDT)
   */
  @Test
  public void testNewYorkFallBackEarlierOccurrence() {
    long wall = 1604194200000L;
    Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall("America/New_York", wall);
    assertEquals(r._1.longValue(), 1604208600000L);
    assertEquals(r._2.offset, -4 * HOUR);
    assertEquals(r._2.abbr, "EDT");
    assertEquals(r._2.dst, 1);
  }

  /**
   * New York 2020 spring-forward: 2:00 AM wall maps onto the post-jump
   * 3:00 AM EDT.
   *
   * Oracle: wall=1583632800000 -> utc=1583650800000 (-04:00, EDT)
   */
  @Test
  public void testNewYorkSpringBoundary() {
    long wall = 1583632800000L;
    Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall("America/New_York", wall);
    assertEquals(r._1.longValue(), 1583650800000L);
    assertEquals(r._2.offset, -4 * HOUR);
    assertEquals(r._2.abbr, "EDT");
    assertEquals(r._2.dst, 1);
  }

  /**
   * Berlin 2020 spring-forward: 2:30 AM wall is inside the gap; the result
   * places the clock at 3:30 AM CEST.
   *
   * Oracle: wall=1585449000000 -> utc=1585445400000 (+02:00, CEST)
   */
  @Test
  public void testBerlinSpringGap() {
    long wall = 1585449000000L;
    Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall("Europe/Berlin", wall);
    assertEquals(r._1.longValue(), 1585445400000L);
    assertEquals(r._2.offset, 2 * HOUR);
    assertEquals(r._2.abbr, "CEST");
    assertEquals(r._2.dst, 1);
    assertEquals(r._1 + r._2.offset, wall + HOUR);
  }

  /**
   * Wall time before the first recorded transition uses the initial
   * local mean time record.
   *
   * Oracle (matches TS wall.test.ts): wall=-2776975260000 -> utc=-2776957498000
   * (offset -17762000, LMT, dst=0)
   */
  @Test
  public void testBeforeFirstUntil() {
    long wall = -2776975260000L;
    Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall("America/New_York", wall);
    assertEquals(r._1.longValue(), -2776957498000L);
    assertEquals(r._2.offset, -17762000);
    assertEquals(r._2.abbr, "LMT");
    assertEquals(r._2.dst, 0);
  }

  /**
   * Wall time past the last recorded transition uses the latest record.
   *
   * Oracle: wall=4776975260000 -> utc=4776993260000 (-05:00, EST, dst=0)
   */
  @Test
  public void testAfterLastUntil() {
    long wall = 4776975260000L;
    Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall("America/New_York", wall);
    assertEquals(r._1.longValue(), 4776993260000L);
    assertEquals(r._2.offset, -5 * HOUR);
    assertEquals(r._2.abbr, "EST");
    assertEquals(r._2.dst, 0);
  }

  /**
   * For an unambiguous instant, fromWall(zone, utc + offset) must recover
   * the exact utc. Samples include zones with historical transitions such as
   * America/Los_Angeles during the 1998 spring-forward.
   */
  @Test
  public void testUnambiguousRoundTrip() {
    long[][] samples = {
      // zone utc, offset
      { 891684000000L, -8 * HOUR }, // America/Los_Angeles 1998-04-04T10:00Z (PST)
      { 891856800000L, -7 * HOUR }, // America/Los_Angeles 1998-04-06T10:00Z (PDT)
      { 916403400000L, -8 * HOUR }, // America/Los_Angeles 1999-01-15T12:30Z (PST)
      { 1751371200000L, -7 * HOUR }, // America/Vancouver 2025-07-01T12:00Z (PDT)
      { 1278258300000L, -4 * HOUR }, // America/New_York 2010-07-04T15:45Z (EDT)
      { 1585445400000L, 2 * HOUR }, // Europe/Berlin 2020-03-29T01:30Z (CEST)
      { 0L, 1 * HOUR }, // Europe/London 1970-01-01T00:00Z (BST)
      { 915148800000L, 9 * HOUR }, // Asia/Tokyo 1999-01-01T00:00Z (JST)
      { 946728000000L, 11 * HOUR }, // Australia/Sydney 2000-01-01T12:00Z (AEDT)
      { 1467892800000L, 2 * HOUR }, // Africa/Cairo 2016-07-07T12:00Z (EET)
    };

    String[] zones = {
      "America/Los_Angeles", "America/Los_Angeles", "America/Los_Angeles",
      "America/Vancouver", "America/New_York", "Europe/Berlin",
      "Europe/London", "Asia/Tokyo", "Australia/Sydney", "Africa/Cairo",
    };

    for (int i = 0; i < samples.length; i++) {
      long utc = samples[i][0];
      long offset = samples[i][1];
      String zone = zones[i];

      // verify the sample offset agrees with fromUTC (data sanity check)
      TimeZoneData.TZInfo info = TimeZoneData.fromUTC(zone, utc);
      assertEquals(info.offset, offset, zone);

      // wall = utc + offset resolves back to utc
      long wall = utc + offset;
      Pair<Long, TimeZoneData.TZInfo> r = TimeZoneData.fromWall(zone, wall);
      assertEquals(r._1.longValue(), utc, zone);
      assertEquals(r._2.offset, offset, zone);
    }
  }

  /**
   * Unknown zone ids resolve to null, mirroring the TS undefined result.
   */
  @Test
  public void testUnknownZone() {
    assertNull(TimeZoneData.fromWall("Foo/Bar", 0));
    assertNull(TimeZoneData.fromWall("america/new_york_typo", 0));
  }
}
