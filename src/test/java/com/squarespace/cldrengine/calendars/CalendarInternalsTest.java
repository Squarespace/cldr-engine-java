package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarType;
import com.squarespace.cldrengine.api.DateIntervalFormatOptions;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.MiscData;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.internal.SchemaConfig;
import com.squarespace.cldrengine.numbers.NumberParams;

/**
 * Mirrors the TypeScript internals tests
 * (packages/cldr-core/__tests__/internals/internals.test.ts, 'select calendar')
 * and pins the 1.15.0 CalendarManager RULE 1c behavior
 * (packages/cldr-core/src/internals/calendars/manager.ts).
 */
public class CalendarInternalsTest {

  private static final Internals INTERNALS = new Internals(new SchemaConfig(), MiscData.VERSION, false);

  private static Bundle bundle(String locale) {
    return CLDR.get(locale).General.bundle();
  }

  /**
   * Mirrors TS internals.test.ts 'select calendar': a plain bundle for
   * und-TH has no calendar system, so the region calendar preferences are
   * consulted. The TH preferences are [buddhist, gregory], hence Buddhist.
   */
  @Test
  public void testSelectCalendarThaiDefaultsToBuddhist() {
    Bundle th = bundle("th");
    assertEquals(th.calendarSystem(), "");
    assertEquals(th.region(), "TH");
    assertEquals(INTERNALS.calendars.selectCalendar(th, null), CalendarType.BUDDHIST);
  }

  /**
   * An explicit calendar override always wins, regardless of the bundle.
   */
  @Test
  public void testSelectCalendarCastOverride() {
    Bundle th = bundle("th");
    assertEquals(INTERNALS.calendars.selectCalendar(th, CalendarType.JAPANESE), CalendarType.JAPANESE);
  }

  /**
   * A region with no calendar preferences falls through to the World (001)
   * first preference, resolved through supportedCalendar ('gregory').
   * The en bundle resolves to region US, which is absent from the
   * preference data.
   */
  @Test
  public void testSelectCalendarWorldFallback() {
    Bundle en = bundle("en");
    assertEquals(INTERNALS.calendars.selectCalendar(en, null), CalendarType.GREGORY);
  }

  /**
   * RULE 1c: a compound skeleton with date and time fields, where the two
   * interval endpoints are equivalent at minute granularity (the field of
   * visual difference is SECOND), is formatted as standalone date + time.
   *
   * Pins the unconditional lookups: both the date and the split time
   * skeleton are resolved via matchAvailablePattern regardless of the
   * isDate/isTime flags (the CLDR data always has standalone patterns
   * available for each, and matchAvailablePattern returns null when no
   * pattern matches).
   */
  @Test
  public void testRule1cResolvesBothDateAndTime() {
    Bundle en = bundle("en");
    CalendarManager manager = new CalendarManager(en, INTERNALS);
    NumberParams params = new PrivateApi(en, INTERNALS).getNumberParams(null, "default");

    // 2000-01-01 10:00:00 UTC -> 2000-01-01 10:00:01 UTC: differ only in seconds.
    GregorianDate start = GregorianDate.fromUnixEpoch(946720800000L, "UTC", 1, 1);
    GregorianDate end = GregorianDate.fromUnixEpoch(946720801000L, "UTC", 1, 1);

    DateIntervalFormatOptions options = DateIntervalFormatOptions.build().skeleton("yMdhm");
    DateIntervalFormatRequest req =
        manager.getDateIntervalFormatRequest(CalendarType.GREGORY, start, end, options, params);

    assertNotNull(req.date, "RULE 1c must resolve the standalone date pattern");
    assertNotNull(req.time, "RULE 1c must resolve the standalone time pattern");
  }
}
