package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;

/**
 * Port of the TS api/calendars.ts misc methods
 * (packages/cldr-core/src/api/calendars.ts, tag v1.15.0):
 * fieldOfVisualDifference, formatDateRawToParts, formatDateWrapper(ToParts)
 * and timePeriodToQuantity.
 *
 * Expected values were generated with the published 1.15.0 engine via
 * CLDRFramework (packages/cldr at tag v1.15.0).
 */
public class CalendarsMiscTest {

  private static final CLDR EN = CLDR.get("en");

  @Test
  public void testFieldOfVisualDifference() {
    // TS doc example: 2019-03-31 vs 2019-04-01 differ visually in the month
    // field even though they are 1 day apart.
    GregorianDate mar31 = EN.Calendars.newGregorianDate(CalendarDateFields.build().year(2019).month(3).day(31));
    GregorianDate apr1 = EN.Calendars.newGregorianDate(CalendarDateFields.build().year(2019).month(4).day(1));
    assertEquals(EN.Calendars.fieldOfVisualDifference(mar31, apr1), DateTimePatternFieldType.MONTH);
    assertNull(EN.Calendars.fieldOfVisualDifference(mar31, mar31));

    // The order of arguments does not matter.
    assertEquals(EN.Calendars.fieldOfVisualDifference(apr1, mar31), DateTimePatternFieldType.MONTH);
  }

  @Test
  public void testFormatDateWrapper() {
    // TS v1.15.0 oracle: c.Calendars.formatDateWrapper('March 31, 2019', '3:00 PM',
    //   { width: 'medium', atTime: true }) === 'March 31, 2019, 3:00 PM'
    String s = EN.Calendars.formatDateWrapper("March 31, 2019", "3:00 PM",
        DateWrapperFormatOptions.build().width(FormatWidthType.MEDIUM).atTime(true));
    assertEquals(s, "March 31, 2019, 3:00 PM");

    // Oracle: { width: 'long', atTime: true } === 'March 31, 2019 at 3:00 PM'
    s = EN.Calendars.formatDateWrapper("March 31, 2019", "3:00 PM",
        DateWrapperFormatOptions.build().width(FormatWidthType.LONG).atTime(true));
    assertEquals(s, "March 31, 2019 at 3:00 PM");

    // Oracle: no options === 'March 31, 2019, 3:00 PM' (width defaults to
    // medium, atTime defaults to true). Options are optional.
    assertEquals(EN.Calendars.formatDateWrapper("March 31, 2019", "3:00 PM", null),
        "March 31, 2019, 3:00 PM");
  }

  @Test
  public void testFormatDateRawToParts() {
    GregorianDate date = EN.Calendars.newGregorianDate(
        CalendarDateFields.build().year(2019).month(5).day(6));
    DateRawFormatOptions opts = DateRawFormatOptions.build().pattern("yyyy-MM-dd");

    String raw = EN.Calendars.formatDateRaw(date, opts);
    assertEquals(raw, "2019-05-06");

    // The parts concatenate to the raw string output.
    List<Part> parts = EN.Calendars.formatDateRawToParts(date, opts);
    StringBuilder buf = new StringBuilder();
    for (Part p : parts) {
      buf.append(p.value);
    }
    assertEquals(buf.toString(), raw);

    // Exact parts from the TS v1.15.0 oracle.
    assertEquals(parts, Arrays.asList(
        Part.part("year", "2019"),
        Part.part("literal", "-"),
        Part.part("month", "05"),
        Part.part("literal", "-"),
        Part.part("day", "06")));
  }

  @Test
  public void testFormatDateWrapperToParts() {
    // Build date and time parts via the raw formatter, then join them with the
    // medium wrapper. Oracle parts: date parts, literal ", ", time parts.
    GregorianDate date = EN.Calendars.newGregorianDate(
        CalendarDateFields.build().year(2019).month(5).day(6));
    List<Part> dateParts = EN.Calendars.formatDateRawToParts(date,
        DateRawFormatOptions.build().pattern("yyyy-MM-dd"));
    List<Part> timeParts = EN.Calendars.formatDateRawToParts(date,
        DateRawFormatOptions.build().pattern("HH:mm"));

    List<Part> parts = EN.Calendars.formatDateWrapperToParts(dateParts, timeParts,
        DateWrapperFormatOptions.build().width(FormatWidthType.MEDIUM).atTime(true));
    assertEquals(parts, Arrays.asList(
        Part.part("year", "2019"),
        Part.part("literal", "-"),
        Part.part("month", "05"),
        Part.part("literal", "-"),
        Part.part("day", "06"),
        Part.part("literal", ", "),
        Part.part("hour", "00"),
        Part.part("literal", ":"),
        Part.part("minute", "00")));
  }

  @Test
  public void testTimePeriodToQuantity() {
    // Oracle: timePeriodToQuantity({ year: 1, day: 2 })
    //   -> [{ unit: 'year', value: 1 }, { unit: 'day', value: 2 }]
    List<Quantity> q = EN.Calendars.timePeriodToQuantity(TimePeriod.build().year(1).day(2));
    assertEquals(q, Arrays.asList(
        Quantity.build().value(new Decimal(1.0)).unit(UnitType.YEAR),
        Quantity.build().value(new Decimal(2.0)).unit(UnitType.DAY)));

    // Oracle: timePeriodToQuantity({ millis: 1500 })
    //   -> [{ unit: 'millisecond', value: 1500 }]
    q = EN.Calendars.timePeriodToQuantity(TimePeriod.build().millis(1500));
    assertEquals(q, Arrays.asList(
        Quantity.build().value(new Decimal(1500.0)).unit(UnitType.MILLISECOND)));

    // Oracle: timePeriodToQuantity({}) -> []
    assertEquals(EN.Calendars.timePeriodToQuantity(TimePeriod.build()), Arrays.asList());
  }

}
