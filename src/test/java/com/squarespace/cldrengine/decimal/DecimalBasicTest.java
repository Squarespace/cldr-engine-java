package com.squarespace.cldrengine.decimal;

import static com.squarespace.cldrengine.decimal.Decimal.coerce;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DecimalBasicTest {

  @Test
  public void testBasic() {
    Decimal a = coerce("1.35");
    Decimal b = coerce("7.213");
    Assert.assertEquals(a.add(b).toString(), "8.563");
  }

}
