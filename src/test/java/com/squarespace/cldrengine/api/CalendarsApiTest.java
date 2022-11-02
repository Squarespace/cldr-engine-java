package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

public class CalendarsApiTest {

  private final static CLDR EN = CLDR.get("en");
  private final static CLDR ES = CLDR.get("es");

  private final static String UTC = "Etc/UTC";
  private final static String UNK = "Factory";
  private final static String INVALID = "Invalid";
  private final static String NEWYORK = "America/New_York";
  private final static String ADDIS = "Africa/Addis_Ababa";
  private final static String NAIROBI = "Africa/Nairobi";

  @Test
  public void testTimezoneInfo() {
    TimeZoneInfo tz;

    tz = EN.Calendars.timeZoneInfo(NEWYORK);
    assertEquals(tz.id(), NEWYORK);
    assertEquals(tz.city().name(), "New York");
    assertEquals(tz.metazone(), "America_Eastern");
    assertEquals(tz.latitude(), 40.714167);
    assertEquals(tz.longitude(), -74.006389);
    assertEquals(tz.countries(), Arrays.asList("US"));
    assertEquals(tz.stdoffset(), -18000000);
    assertEquals(tz.names().long_().generic(), "Eastern Time");
    assertEquals(tz.names().long_().standard(), "Eastern Standard Time");
    assertEquals(tz.names().long_().daylight(), "Eastern Daylight Time");

    tz = EN.Calendars.timeZoneInfo(ADDIS);
    assertEquals(tz.id(), NAIROBI);
    assertEquals(tz.city().name(), "Nairobi");
    assertEquals(tz.metazone(), "Africa_Eastern");
    assertEquals(tz.names().long_().generic(), "");
    assertEquals(tz.names().long_().standard(), "East Africa Time");
    assertEquals(tz.names().long_().daylight(), "");

    tz = EN.Calendars.timeZoneInfo(UTC);
    assertEquals(tz.id(), UTC);
    assertEquals(tz.city().name(), "Unknown City");
    assertEquals(tz.metazone(), "GMT");
    assertEquals(tz.latitude(), 0);
    assertEquals(tz.longitude(), 0);
    assertEquals(tz.countries(), Arrays.asList());
    assertEquals(tz.stdoffset(), 0);

    tz = EN.Calendars.timeZoneInfo(UNK);
    assertEquals(tz.id(), UNK);
    assertEquals(tz.city().name(), "Unknown City");

    tz = EN.Calendars.timeZoneInfo(INVALID);
    assertEquals(tz.id(), UNK);
    assertEquals(tz.city().name(), "Unknown City");

    tz = ES.Calendars.timeZoneInfo(NEWYORK);
    assertEquals(tz.id(), NEWYORK);
    assertEquals(tz.city().name(), "Nueva York");

    tz = ES.Calendars.timeZoneInfo(UTC);
    assertEquals(tz.id(), UTC);
    assertEquals(tz.city().name(), "Ciudad desconocida");

    tz = ES.Calendars.timeZoneInfo(UNK);
    assertEquals(tz.id(), UNK);
    assertEquals(tz.city().name(), "Ciudad desconocida");
  }

}