package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import java.time.Instant;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Guards the tzdb 2026 rule changes, and that the metazone table stays aligned with the order
 * of the zone list (a tzdb release can add zones and shift every later index).
 */
public class TimeZone2026RulesTest {

  private static final int HOUR = 60 * 60 * 1000;

  private static ZoneInfo at(String zoneId, String instant) {
    return TimeZoneData.zoneInfoFromUTC(zoneId, Instant.parse(instant).toEpochMilli());
  }

  @DataProvider
  public Object[][] offsets() {
    return new Object[][] {
      // British Columbia moved to permanent UTC-7 on 2026-03-09 (modelled from 2026-11-01).
      { "America/Vancouver", "2026-02-15T20:00:00Z", -8 },
      { "America/Vancouver", "2026-07-15T20:00:00Z", -7 },
      { "America/Vancouver", "2026-12-15T20:00:00Z", -7 },
      { "America/Vancouver", "2027-01-15T20:00:00Z", -7 },
      { "America/Vancouver", "2027-07-15T20:00:00Z", -7 },
      { "Canada/Pacific", "2027-01-15T20:00:00Z", -7 },
      // Alberta and Manitoba
      { "America/Edmonton", "2026-12-15T20:00:00Z", -6 },
      { "America/Winnipeg", "2026-12-15T20:00:00Z", -5 },
      // Zones that still observe DST are unchanged.
      { "America/Los_Angeles", "2026-12-15T20:00:00Z", -8 },
      { "America/Los_Angeles", "2027-07-15T20:00:00Z", -7 },
      { "America/Toronto", "2026-12-15T20:00:00Z", -5 },
    };
  }

  @Test(dataProvider = "offsets")
  public void testOffsets(String zoneId, String instant, int hours) {
    assertEquals(at(zoneId, instant).offset, hours * HOUR, zoneId + " @ " + instant);
  }

  @Test
  public void testBritishColumbiaIsStandardTimeAtUtcMinus7() {
    ZoneInfo info = at("America/Vancouver", "2026-12-15T20:00:00Z");
    assertEquals(info.abbr, "MST");
    assertEquals(info.dst, false);
    assertEquals(info.metaZoneId, "America_Pacific");
  }

  @DataProvider
  public Object[][] metazones() {
    return new Object[][] {
      { "America/New_York", "America_Eastern" },
      { "America/Los_Angeles", "America_Pacific" },
      { "Asia/Tokyo", "Japan" },
      { "Europe/Paris", "Europe_Central" },
      { "Australia/Sydney", "Australia_Eastern" },
      { "Pacific/Auckland", "New_Zealand" },
    };
  }

  @Test(dataProvider = "metazones")
  public void testMetazonesAlignedWithZoneList(String zoneId, String metazone) {
    assertEquals(at(zoneId, "2026-12-15T20:00:00Z").metaZoneId, metazone, zoneId);
  }
}
