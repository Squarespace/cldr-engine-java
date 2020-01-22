package com.squarespace.cldrengine.decimal;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.Decimal;

public class DecimalBasicTest {

  @Test
  public void testBasic() {
    assertEquals(decimal("1.35").add(decimal("7.213")).toString(), "8.563");
  }

  @Test
  public void testCompare() {
    assertEquals(decimal("3").compare(decimal("3")), 0);
    assertEquals(decimal("2").compare(decimal("3")), -1);
    assertEquals(decimal("4").compare(decimal("3")), 1);
  }

  @Test
  public void testIncrement() {
    assertEquals(decimal("1.34").increment().toString(), "2.34");
  }

  private static Decimal decimal(String s) {
    return Decimal.coerce(s);
  }

}
