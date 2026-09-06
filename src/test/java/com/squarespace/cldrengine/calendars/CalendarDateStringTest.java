package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.BuddhistDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.PersianDate;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimeStringOptions;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/isostring.test.ts
 * (all of it) and the 'string methods' test in
 * __tests__/systems/calendars/gregorian.test.ts (tag v1.15.0), plus
 * toDateTimeString cases derived from the published @phensley/cldr-core 1.15.0
 * package (codegen/node_modules) and asJavaDate (TS asJSDate) equivalence.
 */
public class CalendarDateStringTest {

  private static final String NEW_YORK = "America/New_York";
  private static final String TOKYO = "Asia/Tokyo";
  private static final String PARIS = "Europe/Paris";
  private static final String UTC = "Etc/UTC";

  private static GregorianDate gregorian(long e, String z) {
    return GregorianDate.fromUnixEpoch(e, z, DayOfWeek.SUNDAY, 1);
  }

  private static PersianDate persian(long e, String z) {
    return PersianDate.fromUnixEpoch(e, z, 1, 1);
  }

  private static BuddhistDate buddhist(long e, String z) {
    return BuddhistDate.fromUnixEpoch(e, z, 1, 1);
  }

  @Test
  public void testPersianISOString() {
    // This test method constructs only PersianDate instances so it exercises
    // the CalendarDate static-block guard that forces GregorianDate's class
    // initialization (installing CalendarDate._gregorian) regardless of test
    // ordering. The ISO strings must still render Gregorian fields.
    PersianDate d = persian(1586089698456L, NEW_YORK);
    assertEquals(d.toISOString(), "2020-04-05T12:28:18.456Z");
    assertEquals(d.toLocalISOString(), "2020-04-05T08:28:18.456-04:00");

    d = persian(1586089698456L, TOKYO);
    assertEquals(d.toISOString(), "2020-04-05T12:28:18.456Z");
    assertEquals(d.toLocalISOString(), "2020-04-05T21:28:18.456+09:00");
  }

  @Test
  public void testBuddhistISOString() {
    BuddhistDate d = buddhist(1586089698456L, NEW_YORK);
    assertEquals(d.toISOString(), "2020-04-05T12:28:18.456Z");
    assertEquals(d.toLocalISOString(), "2020-04-05T08:28:18.456-04:00");

    d = buddhist(1586089698456L, TOKYO);
    assertEquals(d.toISOString(), "2020-04-05T12:28:18.456Z");
    assertEquals(d.toLocalISOString(), "2020-04-05T21:28:18.456+09:00");
  }

  @Test
  public void testGregorianISOString() {
    GregorianDate d = gregorian(1586089698456L, NEW_YORK);
    assertEquals(d.toISOString(), "2020-04-05T12:28:18.456Z");
    assertEquals(d.toLocalISOString(), "2020-04-05T08:28:18.456-04:00");

    d = gregorian(1586089698456L, TOKYO);
    assertEquals(d.toISOString(), "2020-04-05T12:28:18.456Z");
    assertEquals(d.toLocalISOString(), "2020-04-05T21:28:18.456+09:00");

    d = gregorian(-62198755200000L, UTC);
    assertEquals(d.toISOString(), "-0001-01-03T00:00:00.000Z");
    assertEquals(d.toLocalISOString(), "-0001-01-03T00:00:00.000+00:00");

    d = gregorian(-62198755200000L, NEW_YORK);
    assertEquals(d.toISOString(), "-0001-01-03T00:00:00.000Z");
    assertEquals(d.toLocalISOString(), "-0001-01-02T19:03:58.000-04:56");
  }

  @Test
  public void testStringMethods() {
    // gregorian.test.ts 'string methods' (tag v1.15.0)
    GregorianDate d;
    long n;

    // Tuesday, July 8, 2025 11:31:21.234 AM GMT-04:00 DST
    n = 1751988681234L;

    d = gregorian(n, NEW_YORK);
    assertEquals(d.toString(), "Gregorian 2025-07-08 11:31:21.234 America/New_York");
    assertEquals(d.toDateString(), "2025-07-08");
    assertEquals(d.toTimeString(TimeStringOptions.build()), "11:31:21.234");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneId(true)), "11:31:21.234 America/New_York");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneId(false)), "11:31:21.234");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneOffset(true)), "11:31:21.234-04:00");

    GregorianDate q = d.add(TimePeriod.build().millis(-234));
    assertEquals(q.toTimeString(TimeStringOptions.build().optionalMilliseconds(true)), "11:31:21");
    assertEquals(q.toTimeString(TimeStringOptions.build().optionalMilliseconds(false)), "11:31:21.000");

    d = gregorian(n, PARIS);
    assertEquals(d.toString(), "Gregorian 2025-07-08 17:31:21.234 Europe/Paris");
    assertEquals(d.toDateString(), "2025-07-08");
    assertEquals(d.toTimeString(TimeStringOptions.build()), "17:31:21.234");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneId(true)), "17:31:21.234 Europe/Paris");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneId(false)), "17:31:21.234");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneOffset(true)), "17:31:21.234+02:00");
  }

  @Test
  public void testStringMethodsApril2018() {
    // gregorian.test.ts 'gregorian date' string block (tag v1.15.0)
    // Wednesday, April 11, 2018 11:59:59.123 PM UTC
    long n = 1523491199123L;

    // 7:59 PM NY time
    GregorianDate d = gregorian(n, NEW_YORK);
    assertEquals(d.toString(), "Gregorian 2018-04-11 19:59:59.123 America/New_York");
    assertEquals(d.toDateString(), "2018-04-11");
    assertEquals(d.toTimeString(TimeStringOptions.build()), "19:59:59.123");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneId(true)), "19:59:59.123 America/New_York");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneId(false)), "19:59:59.123");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneOffset(true)), "19:59:59.123-04:00");
    assertEquals(d.toTimeString(TimeStringOptions.build().includeZoneOffset(false)), "19:59:59.123");
  }

  @Test
  public void testToDateTimeString() {
    // No dedicated TS test exists for toDateTimeString; these cases were
    // derived from the published @phensley/cldr-core 1.15.0 package via node
    // one-liners (codegen/node_modules).
    GregorianDate d = gregorian(1751988681234L, NEW_YORK);
    assertEquals(d.toDateTimeString(TimeStringOptions.build()), "2025-07-08 11:31:21.234");
    assertEquals(d.toDateTimeString(TimeStringOptions.build().includeZoneId(true)),
        "2025-07-08 11:31:21.234 America/New_York");
    assertEquals(d.toDateTimeString(TimeStringOptions.build().includeZoneOffset(true)),
        "2025-07-08 11:31:21.234-04:00");

    d = gregorian(1751988681234L, TOKYO);
    assertEquals(d.toDateTimeString(TimeStringOptions.build().includeZoneOffset(true)),
        "2025-07-09 00:31:21.234+09:00");

    // Zero-millis optionalMilliseconds case: 2020-12-31 12:30:00 UTC
    d = gregorian(1609417800000L, UTC);
    assertEquals(d.toDateTimeString(TimeStringOptions.build()), "2020-12-31 12:30:00.000");
    assertEquals(d.toDateTimeString(TimeStringOptions.build().optionalMilliseconds(true)),
        "2020-12-31 12:30:00");
    assertEquals(d.toDateTimeString(TimeStringOptions.build().optionalMilliseconds(true).includeZoneOffset(true)),
        "2020-12-31 12:30:00+00:00");
  }

  @Test
  public void testAsJavaDate() {
    // TS asJSDate(): new Date(this.toLocalISOString()) — the ISO string parses
    // back to the epoch instant. Java: new Date(unixEpoch()).getTime() must
    // equal unixEpoch().
    GregorianDate d = gregorian(1751988681234L, NEW_YORK);
    assertEquals(d.asJavaDate().getTime(), d.unixEpoch());
    assertEquals(d.unixEpoch(), 1751988681234L);
  }

}
