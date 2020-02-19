package com.squarespace.cldrengine.plurals;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Quantity;
import com.squarespace.cldrengine.api.UnitFormatOptions;
import com.squarespace.cldrengine.api.UnitFormatStyleType;
import com.squarespace.cldrengine.api.UnitLength;
import com.squarespace.cldrengine.api.UnitType;

public class UnitsTest {

  @Test
  public void testOptions() {
    CLDR cldr = CLDR.get("en");
    String s;

    Quantity qty = Quantity.build()
        .value(new Decimal("12.56")).unit(UnitType.LIGHT_YEAR);

    s = cldr.Units.formatQuantity(qty, opts());
    assertEquals(s, "12.56 light years");

    s = cldr.Units.formatQuantity(qty, opts().length(UnitLength.LONG));
    assertEquals(s, "12.56 light years");

    s = cldr.Units.formatQuantity(qty, opts().length(UnitLength.SHORT));
    assertEquals(s, "12.56 ly");

    s = cldr.Units.formatQuantity(qty, opts().length(UnitLength.NARROW));
    assertEquals(s, "12.56ly");

    qty.value(new Decimal("12345.6"));
    s = cldr.Units.formatQuantity(qty, opts().style(UnitFormatStyleType.LONG));
    assertEquals(s, "12 thousand light years");
  }

  private UnitFormatOptions opts() {
    return UnitFormatOptions.build();
  }
}
