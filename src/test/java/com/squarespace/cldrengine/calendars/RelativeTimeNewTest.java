package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_11_2000;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.UTC;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.dateTimeString;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.gregorian;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateDiffOptions;
import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.Pair;
import com.squarespace.cldrengine.api.RelativeTimeFormatOptions;
import com.squarespace.cldrengine.api.RoundingModeType;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;

/**
 * Mirror of the v1.15.0 relativeTime / formatRelativeTime tests:
 *
 *   - __tests__/systems/calendars/relativetime.test.ts (55 lines)
 *   - __tests__/api/calendars/relativetime.test.ts (170 lines)
 *
 * Case tables copied verbatim. The current (old) RelativeTimeTest exercises
 * the pre-1.13 absolute-value model and is intentionally left untouched; this
 * class is the 1.15.0 signed/difference-based coverage.
 *
 * The signed asymmetry examples (2024-03-03 vs 2024-01-30:
 * until = 1 month 3 days / -1 month -4 days, since = 1 month 4 days /
 * -1 month -3 days) are reviewer-verified oracle values, captured from the
 * published @phensley/cldr 1.15.0.
 */
public class RelativeTimeNewTest {

  // Sun, March 11, 2018 7:00:25 AM UTC (api test start date)
  private static final long MARCH_11_2018_070025_UTC = 1520751625000L;

  private static final ContextType CONTEXT = ContextType.BEGIN_SENTENCE;

  // ------------------------------------------------------------------
  // __tests__/systems/calendars/relativetime.test.ts
  // ------------------------------------------------------------------

  @Test
  public void testBasicDifference() {
    Pair<TimePeriodField, Double> r;
    CalendarDate end;
    CalendarDate start = gregorian(MAR_11_2000, UTC);
    assertEquals(dateTimeString(start, false, false), "2000-03-11 08:00:25.000");

    end = start.add(TimePeriod.build().year(1)); // truncated to 1
    assertEquals(dateTimeString(end, false, false), "2001-03-11 08:00:25.000");

    r = start.relativeTime(end, TimePeriodField.YEAR, null);
    assertRelTime(r, TimePeriodField.YEAR, 1);
    r = start.relativeTime(end, TimePeriodField.MONTH, null);
    assertRelTime(r, TimePeriodField.MONTH, 12);
    r = start.relativeTime(end, TimePeriodField.DAY, null);
    assertRelTime(r, TimePeriodField.DAY, 365);

    end = start.add(TimePeriod.build().month(6));

    r = start.relativeTime(end, TimePeriodField.YEAR, null);
    assertRelTime(r, TimePeriodField.YEAR, 0);
    r = start.relativeTime(end, TimePeriodField.YEAR, DateDiffOptions.build().rollupFractional(true));
    assertRelTime(r, TimePeriodField.YEAR, 0.5);
    r = start.relativeTime(end, TimePeriodField.MONTH, null);
    assertRelTime(r, TimePeriodField.MONTH, 6);
    r = start.relativeTime(end, TimePeriodField.DAY, null);
    assertRelTime(r, TimePeriodField.DAY, 184);

    // March 11 to September 11 is 184 days, which is 1/2 year.
    end = start.add(TimePeriod.build().day(184));

    r = start.relativeTime(end, TimePeriodField.YEAR, DateDiffOptions.build().rollupFractional(true));
    assertRelTime(r, TimePeriodField.YEAR, 0.5);
    r = start.relativeTime(end, TimePeriodField.MONTH, null);
    assertRelTime(r, TimePeriodField.MONTH, 6);
    r = start.relativeTime(end, TimePeriodField.DAY, null);
    assertRelTime(r, TimePeriodField.DAY, 184);
  }

  @Test
  public void testSign() {
    Pair<TimePeriodField, Double> r;
    CalendarDate end;
    CalendarDate start = gregorian(MAR_11_2000, UTC);
    assertEquals(dateTimeString(start, false, false), "2000-03-11 08:00:25.000");

    end = start.add(TimePeriod.build().day(-5));
    r = start.relativeTime(end, null, null);
    assertRelTime(r, TimePeriodField.DAY, -5);
  }

  /**
   * The auto-select never picks WEEK: the all-fields difference only
   * computes a week value when weeks are requested (includeWeeks), so the
   * first non-zero field is DAY for any day-span. WEEK appears only when
   * allowWeeks/includeWeeks is set.
   */
  @Test
  public void testWeekAutoSelectProhibition() {
    CalendarDate start = gregorian(MAR_11_2000, UTC);

    assertRelTime(start.relativeTime(start.add(TimePeriod.build().day(27)), null, null),
        TimePeriodField.DAY, 27);
    assertRelTime(start.relativeTime(start.add(TimePeriod.build().day(28)), null, null),
        TimePeriodField.DAY, 28);
    assertRelTime(start.relativeTime(start.add(TimePeriod.build().day(1)), null, null),
        TimePeriodField.DAY, 1);

    // Allowing weeks turns 27/28 day spans into weeks.
    assertRelTime(
        start.relativeTime(start.add(TimePeriod.build().day(28)), null,
            DateDiffOptions.build().includeWeeks(true)),
        TimePeriodField.WEEK, 4);
    assertRelTime(
        start.relativeTime(start.add(TimePeriod.build().day(27)), null,
            DateDiffOptions.build().includeWeeks(true)),
        TimePeriodField.WEEK, 3);
    assertRelTime(
        start.relativeTime(start.add(TimePeriod.build().week(1)), null,
            DateDiffOptions.build().includeWeeks(true)),
        TimePeriodField.WEEK, 1);

    // Explicit week field rolls the fractional day span.
    assertRelTime(
        start.relativeTime(start.add(TimePeriod.build().day(27)), TimePeriodField.WEEK,
            DateDiffOptions.build().rollupFractional(true)),
        TimePeriodField.WEEK, 3.857142857142857);
  }

  /**
   * The signed asymmetry of the difference algorithm (Temporal-compatible):
   * 2024-01-30 vs 2024-03-03. Durations are not symmetrical because of
   * end-of-month alignment with months of different lengths.
   *
   * until = 1 month 3 days / -1 month -4 days
   * since = 1 month 4 days / -1 month -3 days
   */
  @Test
  public void testSignedAsymmetry() {
    // 2024-01-30 00:00 UTC and 2024-03-03 00:00 UTC
    GregorianDate jan30 = gregorian(1706572800000L, UTC);
    GregorianDate mar03 = gregorian(1709424000000L, UTC);
    DateDiffOptions options = DateDiffOptions.build().fields(java.util.Arrays.asList(
        TimePeriodField.MONTH, TimePeriodField.DAY));

    assertMonthDay(jan30.until(mar03, options), 1, 3);
    assertMonthDay(mar03.until(jan30, options), -1, -4);
    assertMonthDay(mar03.since(jan30, options), 1, 4);
    assertMonthDay(jan30.since(mar03, options), -1, -3);
  }

  // ------------------------------------------------------------------
  // __tests__/api/calendars/relativetime.test.ts
  // ------------------------------------------------------------------

  @Test
  public void testFormatRelativeTime() {
    CLDR api = CLDR.get("en");
    String s;
    CalendarDate end;

    CalendarDate start = api.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, UTC);

    s = format(api, start, start, null);
    assertEquals(s, "now");

    s = format(api, start, start, opts(CONTEXT));
    assertEquals(s, "Now");

    end = start.add(TimePeriod.build().millis(250));

    s = format(api, start, end, null);
    assertEquals(s, "now");

    s = format(api, start, end, opts(CONTEXT));
    assertEquals(s, "Now");

    end = start.add(TimePeriod.build().millis(750));
    s = format(api, start, end, opts(CONTEXT));
    assertEquals(s, "In 1 second");

    end = start.add(TimePeriod.build().millis(1250));
    s = format(api, start, end, opts(CONTEXT));
    assertEquals(s, "In 1 second");

    end = start.add(TimePeriod.build().second(30));
    s = format(api, start, end, null);
    assertEquals(s, "in 30 seconds");

    s = format(api, start, end, opts(CONTEXT));
    assertEquals(s, "In 30 seconds");

    end = start.add(TimePeriod.build().minute(-32));
    s = format(api, start, end, opts(CONTEXT));
    assertEquals(s, "32 minutes ago");

    end = start.add(TimePeriod.build().day(-27));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.WEEK));
    assertEquals(s, "4 weeks ago");

    end = start.add(TimePeriod.build().day(-27));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.WEEK).maximumFractionDigits(1));
    assertEquals(s, "3.9 weeks ago");

    end = start.add(TimePeriod.build().week(1));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.WEEK));
    assertEquals(s, "Next week");

    end = start.add(TimePeriod.build().week(2));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.WEEK));
    assertEquals(s, "In 2 weeks");

    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.WEEK).dayOfWeek(true));
    assertEquals(s, "In 2 Sundays");

    s = format(api, start, end, opts(CONTEXT).allowWeeks(true));
    assertEquals(s, "In 2 weeks");

    s = format(api, start, end, opts(CONTEXT).allowWeeks(true).dayOfWeek(true));
    assertEquals(s, "In 2 Sundays");

    end = start.add(TimePeriod.build().month(6));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.MONTH));
    assertEquals(s, "In 6 months");

    end = start.add(TimePeriod.build().month(6));
    s = format(api, start, end, opts(CONTEXT).width(DateFieldWidthType.SHORT));
    assertEquals(s, "In 6 mo.");

    end = start.add(TimePeriod.build().month(6));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.DAY));
    assertEquals(s, "In 184 days");

    end = start.add(TimePeriod.build().month(3000));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.MONTH));
    assertEquals(s, "In 3,000 months");

    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.MONTH).group(false));
    assertEquals(s, "In 3000 months");

    end = start.add(TimePeriod.build().year(1).month(2));
    s = format(api, start, end, opts(CONTEXT).round(RoundingModeType.FLOOR));
    assertEquals(s, "Next year");

    end = start.add(TimePeriod.build().year(1).month(2));
    s = format(api, start, end, opts(CONTEXT).width(DateFieldWidthType.SHORT).round(RoundingModeType.FLOOR));
    assertEquals(s, "Next yr.");

    end = start.add(TimePeriod.build().year(1).month(2));
    s = format(api, start, end, opts(CONTEXT).width(DateFieldWidthType.NARROW).round(RoundingModeType.FLOOR));
    assertEquals(s, "Next yr.");
  }

  @Test
  public void testSpecificField() {
    CLDR api = CLDR.get("en");
    String s;
    CalendarDate end;

    CalendarDate start = api.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, UTC);

    end = start.add(TimePeriod.build().year(1).month(6));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.YEAR));
    assertEquals(s, "In 2 years");

    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.YEAR).minimumFractionDigits(1));
    assertEquals(s, "In 1.5 years");

    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.MONTH));
    assertEquals(s, "In 18 months");

    end = start.add(TimePeriod.build().month(6));
    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.DAY));
    assertEquals(s, "In 184 days");

    end = start.add(TimePeriod.build().day(90).hour(12).minute(30));

    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.DAY));
    assertEquals(s, "In 91 days");

    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.DAY).maximumFractionDigits(1));
    assertEquals(s, "In 90.5 days");
  }

  @Test
  public void testNumericOnly() {
    CLDR api = CLDR.get("en");
    String s;
    CalendarDate end;

    CalendarDate start = api.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, UTC);

    end = start.add(TimePeriod.build());
    s = format(api, start, end, opts(CONTEXT));
    assertEquals(s, "Now");

    s = format(api, start, end, opts(CONTEXT).field(TimePeriodField.DAY));
    assertEquals(s, "Today");

    s = format(api, start, end, opts(CONTEXT).numericOnly(true));
    assertEquals(s, "In 0 seconds");

    s = format(api, start, end, opts(CONTEXT).numericOnly(true).field(TimePeriodField.HOUR));
    assertEquals(s, "In 0 hours");

    s = format(api, start, end, opts(CONTEXT).numericOnly(true).alwaysNow(true));
    assertEquals(s, "Now");

    end = start.add(TimePeriod.build().year(1));
    s = format(api, start, end, opts(CONTEXT));
    assertEquals(s, "Next year");

    s = format(api, start, end, opts(CONTEXT).numericOnly(true));
    assertEquals(s, "In 1 year");
  }

  // ------------------------------------------------------------------
  // helpers
  // ------------------------------------------------------------------

  private static String format(CLDR api, CalendarDate start, CalendarDate end, RelativeTimeFormatOptions opts) {
    return api.Calendars.formatRelativeTime(start, end, opts);
  }

  private static RelativeTimeFormatOptions opts(ContextType context) {
    return RelativeTimeFormatOptions.build().context(context);
  }

  /**
   * Assert a month/day-only until/since result with zero time fields.
   */
  private static void assertMonthDay(com.squarespace.cldrengine.api.TimePeriod p, int month, int day) {
    assertEquals(p.month, (double) month);
    assertEquals(p.day, (double) day);
    assertEquals(p.hour, 0.0);
    assertEquals(p.minute, 0.0);
    assertEquals(p.second, 0.0);
    assertEquals(p.millis, 0.0);
  }

  private static void assertRelTime(Pair<TimePeriodField, Double> res, TimePeriodField field, double value) {
    assertEquals(res._1, field);
    assertEquals(res._2.doubleValue(), value);
  }

}
