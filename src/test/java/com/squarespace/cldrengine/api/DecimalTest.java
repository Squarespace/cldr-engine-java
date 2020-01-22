package com.squarespace.cldrengine.api;

import static com.squarespace.cldrengine.api.RoundingModeType.HALF_EVEN;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class DecimalTest {

  @Test
  public void testShiftLeft() {
    assertEquals(d("1.234").shiftleft(1).toString(), "12.340");
  }

  @Test
  public void testShiftRight() {
    assertEquals(d("155.578").shiftright(1, HALF_EVEN).toString(), "155.58");
  }

  private static Decimal d(String s) {
    return new Decimal(s);
  }
}
