package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;
import com.squarespace.cldrengine.api.TimePeriodField;

/**
 * Mirror of @phensley/cldr-core __tests__/systems/calendars/integers.test.ts
 * (tag v1.15.0). The TS invariant is "math operations result in integer
 * calendar fields" (add/subtract with fractional inputs never leak a
 * fractional part into the epoch). In Java unixEpoch() is a long, so the
 * floor check is trivially true; the meaningful port is asserting that an
 * add/subtract with a fractional field value produces the exact same epoch
 * as the same operation with the field truncated to an integer.
 */
public class DateIntegersTest {

  private static final TimePeriodField[] FIELDS = new TimePeriodField[] {
    TimePeriodField.YEAR,
    TimePeriodField.MONTH,
    TimePeriodField.WEEK,
    TimePeriodField.DAY,
    TimePeriodField.HOUR,
    TimePeriodField.MINUTE,
    TimePeriodField.SECOND,
    TimePeriodField.MILLIS
  };

  private static final double[] NUMS = new double[] { 0.1, 0.11111, 0.2, 0.3, 0.5, 0.7, 1, 1.1, 1.3333333 };

  @Test
  public void testDateFieldIntegers() {
    GregorianDate date = GregorianDate.fromUnixEpoch(0, "UTC", 1, 1);

    for (double n : NUMS) {
      for (TimePeriodField f : FIELDS) {
        long u = date.add(period(f, n)).unixEpoch();
        long ut = date.add(period(f, trunc(n))).unixEpoch();
        assertEquals(u, ut, "add field " + f + " value " + n);

        long v = date.subtract(period(f, n)).unixEpoch();
        long vt = date.subtract(period(f, trunc(n))).unixEpoch();
        assertEquals(v, vt, "subtract field " + f + " value " + n);
      }
    }
  }

  private static TimePeriod period(TimePeriodField field, double value) {
    TimePeriod p = TimePeriod.build();
    switch (field) {
      case YEAR:
        p.year(value);
        break;
      case MONTH:
        p.month(value);
        break;
      case WEEK:
        p.week(value);
        break;
      case DAY:
        p.day(value);
        break;
      case HOUR:
        p.hour(value);
        break;
      case MINUTE:
        p.minute(value);
        break;
      case SECOND:
        p.second(value);
        break;
      default:
        p.millis(value);
        break;
    }
    return p;
  }

  // TS Math.trunc
  private static double trunc(double value) {
    return value < 0 ? Math.ceil(value) : Math.floor(value);
  }

}
