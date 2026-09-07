package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.JapaneseDate;

public class JapaneseDateTest {

  @Test
  public void testEininEraBoundary() {
    JapaneseDate d;

    // The era table lists Einin as starting 1293-08-05 (was 1293-08-55, an
    // impossible day). Pre-1582 dates internalize via the proleptic Julian
    // calendar (as in ICU), so the Einin boundary falls at Julian 1293-08-05 =
    // Gregorian 1293-08-12. Values verified against ICU4J (era 140/141).

    // 1293-08-04 → Shoo (started 1288-04-28), era-year 6
    d = make(-21345379200000L, "UTC");
    assertEquals(d.era(), 140);
    assertEquals(d.year(), 6);
    assertEquals(d.extendedYear(), 1293);

    // 1293-08-11 → last day of Shoo
    d = make(-21344774400000L, "UTC");
    assertEquals(d.era(), 140);
    assertEquals(d.year(), 6);

    // 1293-08-12 → first day of Einin. With the 1293-08-55 typo (August has 31
    // days) every date in August 1293 resolved to Shoo instead.
    d = make(-21344688000000L, "UTC");
    assertEquals(d.era(), 141);
    assertEquals(d.year(), 1);
    assertEquals(d.extendedYear(), 1293);

    // 1294-01-09 → Einin era-year 2
    d = make(-21331728000000L, "UTC");
    assertEquals(d.era(), 141);
    assertEquals(d.year(), 2);
  }

  private static JapaneseDate make(long epoch, String zoneId) {
    return JapaneseDate.fromUnixEpoch(epoch, zoneId, DayOfWeek.SUNDAY, 1);
  }
}
