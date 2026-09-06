package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Tests for CalendarDate.set / static fromFields / startOf / endOf /
 * fields() and the julianDay() sign fix.
 *
 * Every expected value was derived from the @phensley/* 1.15.0 oracle
 * (TS repo at tag v1.15.0) via node, using GregorianDate/BuddhistDate/
 * PersianDate/JapaneseDate/ISO8601Date.fromFields directly. The oracle
 * script is off/oracle-007.js (run from codegen/).
 *
 * Note: CalendarDateFields.year is the EXTENDED year for every calendar
 * (mirrors TS fields().year = extendedYear()).
 */
public class CalendarDateSetTest {

  private static final String UTC = "Etc/UTC";
  private static final String NEW_YORK = "America/New_York";
  private static final String TOKYO = "Asia/Tokyo";

  // ===================================================================
  // set() partial-field merge: change one field, others preserved
  // ===================================================================

  @Test
  public void testSetPartialFieldMerge() {
    // gregorian: change day only
    GregorianDate g = GregorianDate.fromFields(fields(2021, 7, 15, 12, 34, 56, 789, UTC), 1, 1);
    GregorianDate g2 = g.set(fields(null, null, 20, null, null, null, null, null));
    assertEquals(g2.toString(), "Gregorian 2021-07-20 12:34:56.789 Etc/UTC");
    assertEquals(g2.type(), CalendarType.GREGORY);
    assertEquals(g2.unixEpoch(), 1626784496789L);

    // original date is unchanged
    assertEquals(g.toString(), "Gregorian 2021-07-15 12:34:56.789 Etc/UTC");

    // buddhist: change day only (set is inherited from GregorianDate per TS)
    BuddhistDate b = BuddhistDate.fromFields(fields(2564, 7, 15, 12, 34, 56, 789, UTC), 1, 1);
    GregorianDate b2 = b.set(fields(null, null, 20, null, null, null, null, null));
    assertEquals(b2.toString(), "Buddhist 2564-07-20 12:34:56.789 Etc/UTC");
    assertEquals(b2.unixEpoch(), 18762237296789L);
    assertTrue(b2 instanceof BuddhistDate);

    // persian: change day only
    PersianDate p = PersianDate.fromFields(fields(1400, 7, 15, 12, 34, 56, 789, UTC), 1, 1);
    PersianDate p2 = p.set(fields(null, null, 20, null, null, null, null, null));
    assertEquals(p2.toString(), "Persian 1400-07-20 12:34:56.789 Etc/UTC");
    assertEquals(p2.unixEpoch(), 1634042096789L);
    assertTrue(p2 instanceof PersianDate);

    // japanese: change day only (year is the extended year); set returns
    // GregorianDate statically like TS, the runtime type is JapaneseDate
    JapaneseDate j = JapaneseDate.fromFields(fields(2021, 7, 15, 12, 34, 56, 789, UTC), 1, 1);
    GregorianDate j2 = j.set(fields(null, null, 20, null, null, null, null, null));
    assertEquals(j2.toString(), "Japanese 2021-07-20 12:34:56.789 Etc/UTC");
    assertEquals(j2.unixEpoch(), 1626784496789L);
    assertTrue(j2 instanceof JapaneseDate);

    // iso8601: change day only; set returns GregorianDate statically like TS
    ISO8601Date i = ISO8601Date.fromFields(fields(2021, 7, 15, 12, 34, 56, 789, UTC), 1, 1);
    GregorianDate i2 = i.set(fields(null, null, 20, null, null, null, null, null));
    assertEquals(i2.toString(), "ISO8601 2021-07-20 12:34:56.789 Etc/UTC");
    assertEquals(i2.unixEpoch(), 1626784496789L);
    assertTrue(i2 instanceof ISO8601Date);

    // persian: change month only, day/time preserved
    PersianDate p3 = p.set(fields(null, 2, null, null, null, null, null, null));
    assertEquals(p3.toString(), "Persian 1400-02-15 12:34:56.789 Etc/UTC");
    assertEquals(p3.unixEpoch(), 1620218096789L);

    // gregorian: change hour only
    GregorianDate g3 = g.set(fields(null, null, null, 6, null, null, null, null));
    assertEquals(g3.toString(), "Gregorian 2021-07-15 06:34:56.789 Etc/UTC");
    assertEquals(g3.unixEpoch(), 1626330896789L);

    // _timeToMs clamps out-of-range time fields (TS clamp + |0)
    assertEquals(g.set(fields(null, null, null, 47, null, null, null, null)).toString(),
        "Gregorian 2021-07-15 23:34:56.789 Etc/UTC");
    assertEquals(g.set(fields(null, null, null, -10, null, null, null, null)).toString(),
        "Gregorian 2021-07-15 00:34:56.789 Etc/UTC");
    assertEquals(g.set(fields(null, null, null, null, null, null, 2000, null)).toString(),
        "Gregorian 2021-07-15 12:34:56.999 Etc/UTC");
  }

  // ===================================================================
  // static fromFields() defaults per calendar
  // ===================================================================

  @Test
  public void testFromFieldsDefaults() {
    // gregorian ZEROS: year 1970-01-01 00:00:00
    GregorianDate g = GregorianDate.fromFields(fields(null, 5, 20, null, null, null, null, UTC), 1, 1);
    assertEquals(g.toString(), "Gregorian 1970-05-20 00:00:00.000 Etc/UTC");
    assertEquals(g.unixEpoch(), 12009600000L);
    assertEquals(g.year(), 1970L);

    // buddhist ZEROS: year 1 (extended), time fields zero
    BuddhistDate b = BuddhistDate.fromFields(fields(null, 5, 20, 3, 4, 5, 6, UTC), 1, 1);
    assertEquals(b.toString(), "Buddhist 0001-05-20 03:04:05.006 Etc/UTC");
    assertEquals(b.unixEpoch(), -62123748954994L);
    // buddhist calendar year = extended - BUDDHIST_ERA_START == 1 + 543
    assertEquals(b.year(), 544L);
    assertEquals(b.extendedYear(), 1L);

    // persian defaults are 1-1-1 (not the gregorian ZEROS)
    PersianDate p = PersianDate.fromFields(fields(null, 5, 20, null, null, null, null, UTC), 1, 1);
    assertEquals(p.toString(), "Persian 0001-05-20 00:00:00.000 Etc/UTC");
    assertEquals(p.unixEpoch(), -42519600000000L);
    assertEquals(p.year(), 1L);

    // japanese/iso8601 have NO zeros-merge: unset year clamps to 0 in
    // ymdToJD (this IS the 1.15.0 behavior, do not "fix")
    JapaneseDate j = JapaneseDate.fromFields(fields(null, 5, 20, null, null, null, null, UTC), 1, 1);
    assertEquals(j.toString(), "Japanese 0000-05-20 00:00:00.000 Etc/UTC");
    assertEquals(j.unixEpoch(), -62155296000000L);

    ISO8601Date i = ISO8601Date.fromFields(fields(null, 5, 20, null, null, null, null, UTC), 1, 1);
    assertEquals(i.toString(), "ISO8601 0000-05-20 00:00:00.000 Etc/UTC");
    assertEquals(i.unixEpoch(), -62155296000000L);

    // no zoneId: a freshly-constructed date defaults to UTC ("Etc/UTC")
    assertEquals(GregorianDate.fromFields(fields(null, 5, 20, null, null, null, null, null), 1, 1)
        .toString(), "Gregorian 1970-05-20 00:00:00.000 Etc/UTC");
    assertEquals(PersianDate.fromFields(fields(null, 5, 20, null, null, null, null, null), 1, 1)
        .toString(), "Persian 0001-05-20 00:00:00.000 Etc/UTC");
  }

  // ===================================================================
  // startOf / endOf
  // ===================================================================

  @Test
  public void testStartOf() {
    GregorianDate d = GregorianDate.fromFields(fields(2024, 6, 15, 10, 30, 0, 0, UTC), 1, 1);

    assertEquals(d.startOf(CalendarDateModFields.YEAR).toString(), "Gregorian 2024-01-01 00:00:00.000 Etc/UTC");
    assertEquals(d.startOf(CalendarDateModFields.MONTH).toString(), "Gregorian 2024-06-01 00:00:00.000 Etc/UTC");
    assertEquals(d.startOf(CalendarDateModFields.DAY).toString(), "Gregorian 2024-06-15 00:00:00.000 Etc/UTC");
    assertEquals(d.startOf(CalendarDateModFields.HOUR).toString(), "Gregorian 2024-06-15 10:00:00.000 Etc/UTC");
    assertEquals(d.startOf(CalendarDateModFields.MINUTE).toString(), "Gregorian 2024-06-15 10:30:00.000 Etc/UTC");
    assertEquals(d.startOf(CalendarDateModFields.SECOND).toString(), "Gregorian 2024-06-15 10:30:00.000 Etc/UTC");
  }

  @Test
  public void testEndOf() {
    GregorianDate d = GregorianDate.fromFields(fields(2024, 6, 15, 10, 30, 0, 0, UTC), 1, 1);

    // endOf('year'): month 13 / day 32 clamp to Dec 31
    assertEquals(d.endOf(CalendarDateModFields.YEAR).toString(), "Gregorian 2024-12-31 23:59:59.999 Etc/UTC");
    assertEquals(d.endOf(CalendarDateModFields.YEAR).unixEpoch(), 1735689599999L);
    assertEquals(d.endOf(CalendarDateModFields.MONTH).toString(), "Gregorian 2024-06-30 23:59:59.999 Etc/UTC");
    assertEquals(d.endOf(CalendarDateModFields.DAY).toString(), "Gregorian 2024-06-15 23:59:59.999 Etc/UTC");
    assertEquals(d.endOf(CalendarDateModFields.HOUR).toString(), "Gregorian 2024-06-15 10:59:59.999 Etc/UTC");
    assertEquals(d.endOf(CalendarDateModFields.MINUTE).toString(), "Gregorian 2024-06-15 10:30:59.999 Etc/UTC");
    assertEquals(d.endOf(CalendarDateModFields.SECOND).toString(), "Gregorian 2024-06-15 10:30:00.999 Etc/UTC");

    // endOf('year') across a zone with a non-zero offset: local wall Dec 31 23:59:59.999
    GregorianDate ny = GregorianDate.fromFields(fields(2024, 6, 15, 0, 0, 0, 0, NEW_YORK), 1, 1);
    assertEquals(ny.endOf(CalendarDateModFields.YEAR).toString(), "Gregorian 2024-12-31 23:59:59.999 America/New_York");
    assertEquals(ny.endOf(CalendarDateModFields.YEAR).unixEpoch(), 1735707599999L);

    // endOf('month') on Jan 31
    GregorianDate jan = GregorianDate.fromFields(fields(2024, 1, 31, 0, 0, 0, 0, UTC), 1, 1);
    assertEquals(jan.endOf(CalendarDateModFields.MONTH).toString(), "Gregorian 2024-01-31 23:59:59.999 Etc/UTC");
    assertEquals(jan.endOf(CalendarDateModFields.MONTH).unixEpoch(), 1706745599999L);

    // 12-month calendars clamp month 13 to 12 (persian Esfand has 29 days in 1400)
    assertEquals(BuddhistDate.fromFields(fields(2567, 6, 15, 0, 0, 0, 0, UTC), 1, 1)
        .endOf(CalendarDateModFields.YEAR).toString(), "Buddhist 2567-12-31 23:59:59.999 Etc/UTC");
    assertEquals(PersianDate.fromFields(fields(1400, 6, 15, 0, 0, 0, 0, UTC), 1, 1)
        .endOf(CalendarDateModFields.YEAR).toString(), "Persian 1400-12-29 23:59:59.999 Etc/UTC");
    assertEquals(JapaneseDate.fromFields(fields(2021, 6, 15, 0, 0, 0, 0, UTC), 1, 1)
        .endOf(CalendarDateModFields.YEAR).toString(), "Japanese 2021-12-31 23:59:59.999 Etc/UTC");
    assertEquals(ISO8601Date.fromFields(fields(2021, 6, 15, 0, 0, 0, 0, UTC), 1, 1)
        .endOf(CalendarDateModFields.YEAR).toString(), "ISO8601 2021-12-31 23:59:59.999 Etc/UTC");
  }

  /**
   * America/New_York across the 2025-03-09 spring-forward gap: the wall
   * time 02:30 does not exist; fromWall resolves it to 03:30 EDT. startOf/
   * endOf('day') bracket the gap day using the pre-transition EST offset.
   */
  @Test
  public void testSpringGapNewYork() {
    // construction of wall 02:30 lands on 03:30 EDT (fromWall gap resolution)
    GregorianDate gap = GregorianDate.fromFields(fields(2025, 3, 9, 2, 30, 0, 0, NEW_YORK), 1, 1);
    assertEquals(gap.toString(), "Gregorian 2025-03-09 03:30:00.000 America/New_York");
    assertEquals(gap.unixEpoch(), 1741505400000L);
    assertEquals(gap.timeZoneAbbr(), "EDT");
    assertEquals(gap.isDaylightSavings(), true);

    assertEquals(gap.startOf(CalendarDateModFields.DAY).toString(), "Gregorian 2025-03-09 00:00:00.000 America/New_York");
    assertEquals(gap.startOf(CalendarDateModFields.DAY).unixEpoch(), 1741496400000L);
    assertEquals(gap.endOf(CalendarDateModFields.DAY).toString(), "Gregorian 2025-03-09 23:59:59.999 America/New_York");
    assertEquals(gap.endOf(CalendarDateModFields.DAY).unixEpoch(), 1741579199999L);

    // set hour/minute to wall 02:30 on the gap day -> fromWall -> 03:30 EDT
    GregorianDate pre = GregorianDate.fromFields(fields(2025, 3, 9, 0, 0, 0, 0, NEW_YORK), 1, 1);
    assertEquals(pre.toString(), "Gregorian 2025-03-09 00:00:00.000 America/New_York");
    GregorianDate set = pre.set(fields(null, null, null, 2, 30, null, null, null));
    assertEquals(set.toString(), "Gregorian 2025-03-09 03:30:00.000 America/New_York");
    assertEquals(set.unixEpoch(), 1741505400000L);

    // unambiguous wall 01:30 is unchanged
    GregorianDate earlier = pre.set(fields(null, null, null, 1, 30, null, null, null));
    assertEquals(earlier.toString(), "Gregorian 2025-03-09 01:30:00.000 America/New_York");
    assertEquals(earlier.unixEpoch(), 1741501800000L);
  }

  // ===================================================================
  // set() with zoneId override: same wall fields, different instant
  // ===================================================================

  @Test
  public void testSetZoneOverride() {
    GregorianDate base = GregorianDate.fromFields(fields(2021, 1, 1, 12, 0, 0, 0, UTC), 1, 1);
    assertEquals(base.toString(), "Gregorian 2021-01-01 12:00:00.000 Etc/UTC");
    assertEquals(base.unixEpoch(), 1609502400000L);

    // same wall time, New York (-5h) -> instant is 5h later (12:00 EST == 17:00 UTC)
    GregorianDate ny = base.set(fields(null, null, null, null, null, null, null, NEW_YORK));
    assertEquals(ny.toString(), "Gregorian 2021-01-01 12:00:00.000 America/New_York");
    assertEquals(ny.unixEpoch(), 1609520400000L);

    // same wall time, Tokyo (+9h) -> instant is 9h earlier (12:00 JST == 03:00 UTC)
    GregorianDate tokyo = base.set(fields(null, null, null, null, null, null, null, TOKYO));
    assertEquals(tokyo.toString(), "Gregorian 2021-01-01 12:00:00.000 Asia/Tokyo");
    assertEquals(tokyo.unixEpoch(), 1609470000000L);

    // all three are the same wall fields, different instants:
    // wall 12:00 NY == UTC 17:00 (5h later), wall 12:00 Tokyo == UTC 03:00 (9h earlier)
    assertEquals(ny.unixEpoch() - base.unixEpoch(), 18000000L);
    assertEquals(base.unixEpoch() - tokyo.unixEpoch(), 32400000L);
  }

  // ===================================================================
  // julianDay() sign fix: TS is (MILLIS_IN_DAY - offset), Java was +
  // ===================================================================

  @Test
  public void testJulianDay() {
    // 2020-01-01T00:00:00 EST (offset -05:00, utc 05:00)
    GregorianDate g = GregorianDate.fromFields(fields(2020, 1, 1, 0, 0, 0, 0, NEW_YORK), 1, 1);
    assertEquals(g.toString(), "Gregorian 2020-01-01 00:00:00.000 America/New_York");
    assertEquals(g.unixEpoch(), 1577854800000L);
    assertEquals(g.julianDay(), 2458849.7083333335, 1e-9);
    assertEquals(g.timeZoneOffset(), -18000000);

    // same instant expressed in UTC: midnight ET == 05:00 UTC
    GregorianDate gu = GregorianDate.fromFields(fields(2020, 1, 1, 0, 0, 0, 0, UTC), 1, 1);
    assertEquals(gu.julianDay(), 2458849.5, 1e-9);

    // other calendars use the same julianDay
    assertEquals(BuddhistDate.fromFields(fields(2563, 1, 1, 0, 0, 0, 0, UTC), 1, 1).julianDay(), 2657176.5, 1e-9);
    assertEquals(PersianDate.fromFields(fields(1400, 1, 1, 0, 0, 0, 0, UTC), 1, 1).julianDay(), 2459294.5, 1e-9);
    assertEquals(JapaneseDate.fromFields(fields(2021, 1, 1, 0, 0, 0, 0, NEW_YORK), 1, 1).julianDay(), 2459215.7083333335, 1e-9);
  }

  // ===================================================================
  // fields(): full field snapshot
  // ===================================================================

  @Test
  public void testFields() {
    GregorianDate g = GregorianDate.fromFields(fields(2021, 7, 15, 12, 34, 56, 789, UTC), 1, 1);
    CalendarDateFields f = g.fields();
    assertEquals(f.year.get().doubleValue(), 2021.0);
    assertEquals(f.month.get().doubleValue(), 7.0);
    assertEquals(f.day.get().doubleValue(), 15.0);
    assertEquals(f.hour.get().doubleValue(), 12.0);
    assertEquals(f.minute.get().doubleValue(), 34.0);
    assertEquals(f.second.get().doubleValue(), 56.0);
    assertEquals(f.millis.get().doubleValue(), 789.0);
    assertEquals(f.zoneId.get(), "Etc/UTC");

    // fields().year is the extended year for every calendar
    BuddhistDate b = BuddhistDate.fromFields(fields(2564, 7, 15, 12, 34, 56, 789, UTC), 1, 1);
    assertEquals(b.fields().year.get().doubleValue(), 2564.0);

    // withZone keeps the calendar type (julianDay parity of the fix)
    GregorianDate ny = g.withZone(NEW_YORK);
    assertEquals(ny.timeZoneId(), NEW_YORK);
    assertEquals(ny.unixEpoch(), g.unixEpoch());
  }

  private static CalendarDateFields fields(
      Number year, Number month, Number day,
      Number hour, Number minute, Number second, Number millis, String zoneId) {
    CalendarDateFields f = CalendarDateFields.build();
    if (year != null) {
      f.year(year.doubleValue());
    }
    if (month != null) {
      f.month(month.doubleValue());
    }
    if (day != null) {
      f.day(day.doubleValue());
    }
    if (hour != null) {
      f.hour(hour.doubleValue());
    }
    if (minute != null) {
      f.minute(minute.doubleValue());
    }
    if (second != null) {
      f.second(second.doubleValue());
    }
    if (millis != null) {
      f.millis(millis.doubleValue());
    }
    if (zoneId != null) {
      f.zoneId(zoneId);
    }
    return f;
  }
}
