package com.squarespace.cldrengine.decimal;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.MathContext;

public class DecimalBasicTest {

  @Test
  public void testBasic() {
    assertEquals(dec("1.35").add(dec("7.213")).toString(), "8.563");
  }

  @Test
  public void testCompare() {
    assertEquals(dec("3").compare(dec("3")), 0);
    assertEquals(dec("2").compare(dec("3")), -1);
    assertEquals(dec("4").compare(dec("3")), 1);

    // 14 digits, evenly divisible by radix
    Decimal a = dec("100000000000000");
    Decimal b = dec("100000000000001");
    Decimal c = new Decimal(a);

    assertEquals(a.compare(b), -1);
    assertEquals(b.compare(a), 1);
    assertEquals(a.compare(c), 0);

    Decimal d = dec("900000000000000");
    assertEquals(a.compare(d), -1);
    assertEquals(d.compare(a), 1);

    // 15 digits
    a = dec("1e15");
    b = dec("1000000000000001");
    c = new Decimal(a);

    assertEquals(a.compare(b), -1);
    assertEquals(b.compare(a), 1);
    assertEquals(a.compare(c), 0);

    d = dec("9e15");
    assertEquals(a.compare(d), -1);
    assertEquals(d.compare(a), 1);
  }

  @Test
  public void testCompare2() {
    assertEquals(dec("100000.2345666666e-7").compare(dec("100000.23457e-7")), -1);
    assertEquals(dec("100000.2345666666e-7").compare(dec("100000.23447e-7")), 1);
    assertEquals(dec("100000000.2345666666e-7").compare(dec("100020000.23447e-7")), -1);
    assertEquals(dec("10000000000.2345666666e-7").compare(dec("10000000000.23457e-7")), -1);
    assertEquals(dec("10000000000.234566666666e-6").compare(dec("10000000000.23457e-6")), -1);
  }

  @Test
  public void testIncrement() {
    assertEquals(dec("1.34").increment().toString(), "2.34");
  }

  @Test
  public void testDivide() {
    Decimal a = dec("1207008");
    Decimal b = dec("647386433361211");
    MathContext c = MathContext.build();

    assertEquals(a.divide(b, c.scale(5)), dec("0.00000"));
    assertEquals(a.divide(b, c.scale(8)), dec("0.00000000"));
    assertEquals(a.divide(b, c.scale(9)), dec("0.000000002"));
    assertEquals(a.divide(b, c.scale(10)), dec("0.0000000019"));
    assertEquals(a.divide(b, c.scale(11)), dec("0.00000000186"));
    assertEquals(a.divide(b, c.scale(12)), dec("0.000000001864"));
  }

  private static Decimal dec(String s) {
    return Decimal.coerce(s);
  }

}
