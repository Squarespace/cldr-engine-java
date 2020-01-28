package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;
import com.squarespace.cldrengine.api.RoundingModeType;

public class Sketch15 {

  public static void main(String[] args) {
    load();
  }

  private static void load() {
    String id = "es-419";
    CLDR cldr = CLDR.get(id);

    String s = "";

    DecimalFormatOptions opts = DecimalFormatOptions.build()
        .style(DecimalFormatStyleType.SHORT)
        .divisor(1000)
        .minimumFractionDigits(0);
//    s = cldr.Numbers.formatDecimal(DecimalConstants.PI, opts);
//    System.out.println(s);

//    1.111 DecimalFormatOptions( round=up minimumIntegerDigits=5
    // maximumFractionDigits=0
    // minimumFractionDigits=5
    // style=LONG
    // negativeZero=false divisor=10000 )
//    1 mil   |   1

    opts = DecimalFormatOptions.build()
        .style(DecimalFormatStyleType.LONG)
        .divisor(10000)
        .round(RoundingModeType.UP)
        .minimumIntegerDigits(5)
        .maximumFractionDigits(0)
        .minimumFractionDigits(1)
        .negativeZero(true);
    for (int j = 0; j < 10; j++) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 1000000; i++) {
        s = cldr.Numbers.formatDecimal(new Decimal("2"), opts);
      }
      long elapsed = System.currentTimeMillis() - start;
      System.out.println(s);
      System.out.println(elapsed);
    }

  }
}
