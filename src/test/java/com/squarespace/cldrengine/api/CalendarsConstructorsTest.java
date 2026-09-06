package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

/**
 * Port of the TS api/calendars.ts new/now date constructors
 * (packages/cldr-core/src/api/calendars.ts:124-215, tag v1.15.0).
 */
public class CalendarsConstructorsTest {

  private static final String NEWYORK = "America/New_York";
  private static final String UTC = "Etc/UTC";

  private static final CLDR EN = CLDR.get("en");

  @Test
  public void testNewGregorianDate() {
    // TS: newGregorianDate({ year: 2020, month: 2, day: 18, hour: 16, minute: 16,
    // second: 31, zoneId: 'America/New_York' })
    CalendarDateFields fields = CalendarDateFields.build()
        .year(2020).month(2).day(18).hour(16).minute(16).second(31)
        .zoneId(NEWYORK);
    GregorianDate date = EN.Calendars.newGregorianDate(fields);

    // 2020-02-18 16:16:31 America/New_York == epoch 1582060591000
    GregorianDate expected = EN.Calendars.toGregorianDate(1582060591000L, NEWYORK);

    // Equal on all fields (year, month, day, hour, minute, second, millis, zoneId)
    assertEquals(date.fields(), expected.fields());

    // Expected field values (verified against TS v1.15.0 oracle)
    assertEquals(date.year(), 2020);
    assertEquals(date.month(), 2);
    assertEquals(date.dayOfMonth(), 18);
    assertEquals(date.hourOfDay(), 16);
    assertEquals(date.minute(), 16);
    assertEquals(date.second(), 31);
    assertEquals(date.milliseconds(), 0);
    assertEquals(date.timeZoneId(), NEWYORK);
  }

  @Test
  public void testNowGregorian() {
    long before = System.currentTimeMillis();
    GregorianDate date = EN.Calendars.nowGregorian("UTC");
    long after = System.currentTimeMillis();

    // The instant must be in [now - 2s, now + 2s].
    assertTrue(date.unixEpoch() >= before - 2000, "epoch too early: " + date.unixEpoch());
    assertTrue(date.unixEpoch() <= after + 2000, "epoch too late: " + date.unixEpoch());

    // NOTE: the IANA data links 'UTC' -> 'Etc/UTC', so timeZoneId() reports
    // 'Etc/UTC' — byte-identical to the TS v1.15.0 oracle
    // (CLDR('en').Calendars.nowGregorian('UTC').timeZoneId() === 'Etc/UTC').
    assertEquals(date.timeZoneId(), UTC);
  }

  @Test
  public void testNewDateCalendarTypes() {
    assertEquals(EN.Calendars.newBuddhistDate(CalendarDateFields.build().year(2020)).type(),
        CalendarType.BUDDHIST);
    assertEquals(EN.Calendars.newGregorianDate(CalendarDateFields.build().year(2020)).type(),
        CalendarType.GREGORY);
    assertEquals(EN.Calendars.newISO8601Date(CalendarDateFields.build().year(2020)).type(),
        CalendarType.ISO8601);
    assertEquals(EN.Calendars.newJapaneseDate(CalendarDateFields.build().year(2020)).type(),
        CalendarType.JAPANESE);
    assertEquals(EN.Calendars.newPersianDate(CalendarDateFields.build().year(2020)).type(),
        CalendarType.PERSIAN);
  }

  @Test
  public void testNowCalendarTypes() {
    assertEquals(EN.Calendars.nowBuddhist(UTC).type(), CalendarType.BUDDHIST);
    assertEquals(EN.Calendars.nowGregorian(UTC).type(), CalendarType.GREGORY);
    assertEquals(EN.Calendars.nowISO8601(UTC).type(), CalendarType.ISO8601);
    assertEquals(EN.Calendars.nowJapanese(UTC).type(), CalendarType.JAPANESE);
    assertEquals(EN.Calendars.nowPersian(UTC).type(), CalendarType.PERSIAN);

    // now(zoneId) is an alias of nowGregorian(zoneId)
    assertEquals(EN.Calendars.now("UTC").type(), CalendarType.GREGORY);
  }

  @Test
  public void testNowNullZoneId() {
    // TS: zoneId || '' — a null zone id must not fall back to the JVM default
    // zone. The empty zone id is invalid, so the date resolves to the default
    // UTC zone ('Etc/UTC'), exactly as the TS oracle does.
    BuddhistDate b = EN.Calendars.nowBuddhist(null);
    GregorianDate g = EN.Calendars.nowGregorian(null);
    ISO8601Date iso = EN.Calendars.nowISO8601(null);
    JapaneseDate j = EN.Calendars.nowJapanese(null);
    PersianDate p = EN.Calendars.nowPersian(null);

    long now = System.currentTimeMillis();
    for (CalendarDate d : new CalendarDate[] { b, g, iso, j, p }) {
      assertTrue(Math.abs(d.unixEpoch() - now) < 2000, "epoch out of range: " + d.unixEpoch());
    }
    assertEquals(g.timeZoneId(), UTC);
  }

}
