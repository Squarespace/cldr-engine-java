package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;

public class Sketch10 {

  public static void main(String[] args) {
    String id = "en";
    CLDR cldr = CLDR.get(id);
    Decimal n = new Decimal("5355733");
    DecimalFormatOptions options = DecimalFormatOptions.build()
        .style(DecimalFormatStyleType.LONG)
        .minimumFractionDigits(1);
    String s = cldr.Numbers.formatDecimal(n, options);
    System.out.println(s);
  }
}
