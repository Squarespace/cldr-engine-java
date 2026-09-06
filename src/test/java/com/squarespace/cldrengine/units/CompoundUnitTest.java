package com.squarespace.cldrengine.units;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Quantity;
import com.squarespace.cldrengine.api.UnitFormatOptions;
import com.squarespace.cldrengine.api.UnitLength;
import com.squarespace.cldrengine.api.UnitType;

/**
 * Compound unit formatting ("times" and "per" quantities).
 *
 * Expected values are oracle-verified against cldr-engine (TS) v1.15.0,
 * packages/cldr-core/__tests__/api/units/units.test.ts.
 */
public class CompoundUnitTest {

  @Test
  public void testTimes() {
    CLDR cldr = CLDR.get("en");

    assertEquals(fmt(cldr, "1", UnitType.NEWTON, UnitType.METER), "1 newton-meter");
    assertEquals(fmt(cldr, "123", UnitType.NEWTON, UnitType.METER), "123 newton-meters");
    assertEquals(fmt(cldr, "1", UnitType.FOOT, UnitType.POUND), "1 foot-pound");
    assertEquals(fmt(cldr, "123", UnitType.FOOT, UnitType.POUND), "123 foot-pounds");
    assertEquals(fmt(cldr, "1", UnitType.METER, UnitType.KILOGRAM), "1 meter-kilogram");
    assertEquals(fmt(cldr, "123", UnitType.METER, UnitType.KILOGRAM), "123 meter-kilograms");
  }

  @Test
  public void testPer() {
    CLDR cldr = CLDR.get("en");

    assertEquals(fmt(cldr, "12345.6789", UnitType.KILOGRAM, UnitType.SECOND, "per"), "12,345.679 kilograms per second");
    assertEquals(fmt(cldr, "17.9887", UnitType.TERABIT, UnitType.MINUTE, "per"), "17.989 terabits per minute");
    assertEquals(fmt(cldr, "30.7899", UnitType.KILOGRAM, UnitType.LUX, "per"), "30.79 kilograms per lux");
  }

  @Test
  public void testPerNarrow() {
    CLDR cldr = CLDR.get("en");
    Quantity qty = Quantity.build()
        .value(new Decimal("17.9887"))
        .unit(UnitType.TERABIT)
        .per(UnitType.MINUTE);
    String s = cldr.Units.formatQuantity(qty,
        UnitFormatOptions.build().length(UnitLength.NARROW));
    assertEquals(s, "17.989Tb/min");
  }

  private static String fmt(CLDR cldr, String value, UnitType num, UnitType den) {
    return fmt(cldr, value, num, den, "times");
  }

  private static String fmt(CLDR cldr, String value, UnitType num, UnitType den, String op) {
    Quantity qty = Quantity.build()
        .value(new Decimal(value))
        .unit(num);
    if (op.equals("times")) {
      qty.times(den);
    } else {
      qty.per(den);
    }
    return cldr.Units.formatQuantity(qty);
  }
}
