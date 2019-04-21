package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class BuddhistDateTest {

  public static final String NEW_YORK = "America/New_York";

  @Test
  public void testBasic() {
    BuddhistDate d;

    // Wednesday, April 11, 2018 11:59:59.123 PM UTC
    long n = 1523491199123L;

    d = make(n, NEW_YORK);

    assertEquals(d.toString(), "Buddhist");
  }

  private static BuddhistDate make(long epoch, String zoneId) {
    return BuddhistDate.fromUnixEpoch(epoch, zoneId, DayOfWeek.SUNDAY, 1);
  }
}
