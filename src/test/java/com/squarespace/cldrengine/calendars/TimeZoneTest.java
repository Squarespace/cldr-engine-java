package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class TimeZoneTest {

  private static final String NEW_YORK = "America/New_York";

  @Test
  public void testZoneInfo() {
    long base = 1613746481000L;
    ZoneInfo info;

    info = TimeZoneData.zoneInfoFromUTC(NEW_YORK, base);
    assertEquals(info.abbr, "EST");
    assertEquals(info.dst, false);
    assertEquals(info.metaZoneId, "America_Eastern");
    assertEquals(info.stableId, "America/New_York");
    assertEquals(info.offset, -5 * 60 * 60 * 1000);

    base += (60 * 86400 * 1000L);

    info = TimeZoneData.zoneInfoFromUTC(NEW_YORK, base);
    assertEquals(info.abbr, "EDT");
    assertEquals(info.dst, true);
    assertEquals(info.metaZoneId, "America_Eastern");
    assertEquals(info.stableId, "America/New_York");
    assertEquals(info.offset, -4 * 60 * 60 * 1000);
  }

  @Test
  public void testZoneMeta() {
    ZoneMeta meta;

    meta = TimeZoneData.zoneMeta(NEW_YORK);
    assertEquals(meta.zoneId, NEW_YORK);
    assertEquals(meta.countries, new String[] { "US" });
    assertEquals(meta.latitude, 40.714167);
    assertEquals(meta.longitude, -74.006389);
    assertEquals(meta.stdoffset, -5 * 60 * 60 * 1000);

    meta = TimeZoneData.zoneMeta("Foo/Bar");
    assertEquals(meta, null);
  }
}
