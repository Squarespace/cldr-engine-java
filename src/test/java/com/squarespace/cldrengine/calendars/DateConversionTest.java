package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.BuddhistDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.ISO8601Date;
import com.squarespace.cldrengine.api.JapaneseDate;
import com.squarespace.cldrengine.api.PersianDate;

public class DateConversionTest {

  // Sat, Feb 15, 2020 11:49:56 PM UTC
  private static final long FEB_15_2020 = 1581810596000L;

  @Test
  public void testDateConversions() {
    CLDR cldr = CLDR.get("en");
    Date base;

    base = new Date(FEB_15_2020);
    GregorianDate gregorian = cldr.Calendars.toGregorianDate(base, "UTC");
    assertEquals(gregorian.toString(), "Gregorian 2020-02-15 23:49:56.000 Etc/UTC");

    BuddhistDate buddhist = cldr.Calendars.toBuddhistDate(base, "UTC");
    assertEquals(buddhist.toString(), "Buddhist 2020-02-15 23:49:56.000 Etc/UTC");

    buddhist = cldr.Calendars.toBuddhistDate(gregorian);
    assertEquals(buddhist.toString(), "Buddhist 2020-02-15 23:49:56.000 Etc/UTC");

    PersianDate persian = cldr.Calendars.toPersianDate(base, "UTC");
    assertEquals(persian.toString(), "Persian 1398-11-26 23:49:56.000 Etc/UTC");

    persian = cldr.Calendars.toPersianDate(gregorian);
    assertEquals(persian.toString(), "Persian 1398-11-26 23:49:56.000 Etc/UTC");

    ISO8601Date iso8601 = cldr.Calendars.toISO8601Date(base, "UTC");
    assertEquals(iso8601.toString(), "ISO8601 2020-02-15 23:49:56.000 Etc/UTC");

    iso8601 = cldr.Calendars.toISO8601Date(gregorian);
    assertEquals(iso8601.toString(), "ISO8601 2020-02-15 23:49:56.000 Etc/UTC");

    JapaneseDate japanese = cldr.Calendars.toJapaneseDate(base, "UTC");
    assertEquals(japanese.toString(), "Japanese 2020-02-15 23:49:56.000 Etc/UTC");

    japanese = cldr.Calendars.toJapaneseDate(gregorian);
    assertEquals(japanese.toString(), "Japanese 2020-02-15 23:49:56.000 Etc/UTC");

    // convert back to gregorian
    gregorian = cldr.Calendars.toGregorianDate(buddhist);
    assertEquals(gregorian.toString(), "Gregorian 2020-02-15 23:49:56.000 Etc/UTC");
  }
}
