package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.calendars.DiffTestUtils.MAR_11_2000;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.NEW_YORK;
import static com.squarespace.cldrengine.calendars.DiffTestUtils.gregorian;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/datecomp.test.ts
 * (tag v1.15.0). Case tables copied verbatim.
 */
public class DateCompareTest {

  @Test
  public void testComparison() {
    GregorianDate end;
    GregorianDate start = gregorian(MAR_11_2000, NEW_YORK);

    end = start.add(TimePeriod.build().year(1));
    assertEquals(start.compare(end), -1);

    end = start.add(TimePeriod.build().millis(1));
    assertEquals(start.compare(end), -1);

    end = start.add(TimePeriod.build());
    assertEquals(start.compare(end), 0);

    end = start.add(TimePeriod.build().year(0).millis(0));
    assertEquals(start.compare(end), 0);

    end = start.add(TimePeriod.build().millis(-1));
    assertEquals(start.compare(end), 1);

    end = start.add(TimePeriod.build().year(-1));
    assertEquals(start.compare(end), 1);
  }

}
