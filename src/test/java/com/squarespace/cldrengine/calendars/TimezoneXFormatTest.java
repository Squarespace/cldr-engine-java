package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateRawFormatOptions;

/**
 * Mirror of the upstream timezone_x zero-offset Z tests:
 *
 *   - __tests__/api/calendars/calendars.test.ts 'timezone iso8601 basic format'
 *   - __tests__/api/calendars/timezones.test.ts 'iso8601 X/x offset formats (TR35)'
 *
 * ISO 8601 local time difference format (X family) per TR35. Expected cells
 * verified against ICU4J: 'Z' is the UTC indicator and is used only when the
 * offset is zero and the specifier is X* (uppercase) — for EVERY width 1..5.
 * Lowercase x* renders the numeric offset ('+00', '+0000', ...), never 'Z'.
 * Colon appears for XXX (extended) and XXXXX only; XXXX stays basic, and
 * width 1 (X) appends minutes whenever they are non-zero.
 */
public class TimezoneXFormatTest {

  // Sun, March 11, 2018 7:00:25 AM UTC (api test start date)
  private static final long MARCH_11_2018_070025_UTC = 1520751625000L;

  private static final String NEW_YORK = "America/New_York";
  private static final String LONDON = "Europe/London";

  private static final CLDR EN = CLDR.get("en");

  @Test
  public void testTimezoneIso8601BasicFormat() {
    CalendarDate d;
    String s;

    d = EN.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, NEW_YORK);

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("x"));
    assertEquals(s, "-04");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("X"));
    assertEquals(s, "-04");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xx"));
    assertEquals(s, "-0400");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XX"));
    assertEquals(s, "-0400");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xxx"));
    assertEquals(s, "-04:00");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XXX"));
    assertEquals(s, "-04:00");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xxxx"));
    assertEquals(s, "-0400");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XXXX"));
    assertEquals(s, "-0400");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xxxxx"));
    assertEquals(s, "-04:00");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XXXXX"));
    assertEquals(s, "-04:00");

    d = EN.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, LONDON);

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("x"));
    assertEquals(s, "+00");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("X"));
    assertEquals(s, "Z");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xx"));
    assertEquals(s, "+0000");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XX"));
    assertEquals(s, "Z");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xxx"));
    assertEquals(s, "+00:00");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XXX"));
    assertEquals(s, "Z");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xxxx"));
    assertEquals(s, "+0000");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XXXX"));
    assertEquals(s, "Z");

    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("xxxxx"));
    assertEquals(s, "+00:00");
    s = EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern("XXXXX"));
    assertEquals(s, "Z");
  }

  @Test
  public void testIso8601XxOffsetFormatsTr35() {
    // Zero offset (UTC).
    assertEquals(fmt("UTC", "X"), "Z");
    assertEquals(fmt("UTC", "XX"), "Z");
    assertEquals(fmt("UTC", "XXX"), "Z");
    assertEquals(fmt("UTC", "XXXX"), "Z");
    assertEquals(fmt("UTC", "XXXXX"), "Z");
    assertEquals(fmt("UTC", "x"), "+00");
    assertEquals(fmt("UTC", "xx"), "+0000");
    assertEquals(fmt("UTC", "xxx"), "+00:00");
    assertEquals(fmt("UTC", "xxxx"), "+0000");
    assertEquals(fmt("UTC", "xxxxx"), "+00:00");

    // Fractional offset +05:30 (Asia/Kolkata has no DST, so the offset holds
    // for any date).
    String tzid = "Asia/Kolkata";
    assertTrue(EN.Calendars.timeZoneIds().contains(tzid));
    assertEquals(fmt(tzid, "X"), "+0530");
    assertEquals(fmt(tzid, "XX"), "+0530");
    assertEquals(fmt(tzid, "XXX"), "+05:30");
    assertEquals(fmt(tzid, "XXXX"), "+0530");
    assertEquals(fmt(tzid, "XXXXX"), "+05:30");
    assertEquals(fmt(tzid, "x"), "+0530");
    assertEquals(fmt(tzid, "xx"), "+0530");
    assertEquals(fmt(tzid, "xxx"), "+05:30");
    assertEquals(fmt(tzid, "xxxx"), "+0530");
    assertEquals(fmt(tzid, "xxxxx"), "+05:30");
  }

  // test date: 2019-04-03 03:45:55 UTC (1554263155000)
  private static String fmt(String zoneId, String pattern) {
    CalendarDate d = EN.Calendars.toGregorianDate(1554263155000L, zoneId);
    return EN.Calendars.formatDateRaw(d, DateRawFormatOptions.build().pattern(pattern));
  }
}
