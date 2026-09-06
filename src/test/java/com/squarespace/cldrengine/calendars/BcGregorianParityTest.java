package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.ISO8601Date;

/**
 * Regression tests pinning BC Gregorian (proleptic) date parity with the TS
 * oracle (cldr-engine @ v1.15.0).
 *
 * The epoch-millis inputs are the proleptic-Gregorian civil dates computed
 * with the Fliegel-Van Flandern algorithm. The engine itself drifts +2..+9
 * days from the true proleptic Gregorian for BC dates — a TS quirk that
 * Java mirrors byte-for-byte; do NOT "improve" it (parity is the goal).
 *
 * Expected values verified against the TS oracle:
 *   CLDRFramework.get('en').Calendars.toGregorianDate(new Date(ms), 'Etc/UTC')
 * and toISO8601Date (same probe) — identical output on all six cases.
 *
 * Note on the original open-item probe: it compared JAVA's era year
 * (year(), e.g. 1001) against TS's extended-year string (-1000), which made
 * the two engines look divergent. They never were; era year vs extended year
 * are different (related) fields, and both engines agree on every field.
 */
public class BcGregorianParityTest {

  private static final String UTC = "Etc/UTC";

  private static final CLDR EN = CLDR.get("en");

  /**
   * ms, extendedYear, year (era), era, month, dayOfMonth, toDateString.
   * Source: TS v1.15.0 oracle probe (2026-09-07), dates back-computed with
   * Fliegel-Van Flandern from the expected fields.
   */
  private static final long[][] CASES = {
      // ms,                  extYear, year, era, month, dom
      { -93713414400000L,     -1000,   1001, 0,   5,     14 },
      { -77940576000000L,     -500,    501,  0,   3,     6 },
      { -65291443200000L,     -99,     100,  0,   1,     2 },
      { -63730800000000L,     -50,     51,   0,   6,     17 },
      { -62225193600000L,     -2,      3,    0,   3,     3 },
      { -62167305600000L,     0,       1,    0,   1,     2 },
  };

  private static final String[] DATE_STRINGS = {
      "-1000-05-14",
      "-0500-03-06",
      "-0099-01-02",
      "-0050-06-17",
      "-0002-03-03",
      "0000-01-02",
  };

  private static GregorianDate gregorian(long ms) {
    return EN.Calendars.toGregorianDate(ms, UTC);
  }

  private static ISO8601Date iso8601(long ms) {
    return EN.Calendars.toISO8601Date(ms, UTC);
  }

  @Test
  public void testBCGregorianFields() {
    for (int i = 0; i < CASES.length; i++) {
      long[] c = CASES[i];
      long ms = c[0];
      GregorianDate g = gregorian(ms);
      assertEquals(g.extendedYear(), c[1], "extendedYear for ms=" + ms);
      assertEquals(g.year(), c[2], "year (era) for ms=" + ms);
      assertEquals(g.era(), c[3], "era for ms=" + ms);
      assertEquals(g.month(), c[4], "month for ms=" + ms);
      assertEquals(g.dayOfMonth(), c[5], "dayOfMonth for ms=" + ms);
      assertEquals(g.toDateString(), DATE_STRINGS[i], "toDateString for ms=" + ms);
    }
  }

  @Test
  public void testBCISO8601Fields() {
    for (int i = 0; i < CASES.length; i++) {
      long[] c = CASES[i];
      long ms = c[0];
      ISO8601Date d = iso8601(ms);
      assertEquals(d.extendedYear(), c[1], "ISO extendedYear for ms=" + ms);
      assertEquals(d.year(), c[2], "ISO year (era) for ms=" + ms);
      assertEquals(d.era(), c[3], "ISO era for ms=" + ms);
      assertEquals(d.month(), c[4], "ISO month for ms=" + ms);
      assertEquals(d.dayOfMonth(), c[5], "ISO dayOfMonth for ms=" + ms);
      assertEquals(d.toDateString(), DATE_STRINGS[i], "ISO toDateString for ms=" + ms);
    }
  }
}
