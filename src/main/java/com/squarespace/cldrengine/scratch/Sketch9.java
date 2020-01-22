package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalAdjustOptions;
import com.squarespace.cldrengine.api.RoundingModeType;

public class Sketch9 {

  public static void main(String[] args) {
    String id = "en";
    CLDR cldr = CLDR.get(id);
    DecimalAdjustOptions options = DecimalAdjustOptions.build().maximumFractionDigits(4)
        .round(RoundingModeType.HALF_EVEN);
    Decimal n = new Decimal("1.23456789");
    System.out.println(n.toString());
    Decimal r = cldr.Numbers.adjustDecimal(n, options);
    System.out.println(r.toString());
  }
}
