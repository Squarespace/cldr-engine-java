package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CurrencyFormatOptions;
import com.squarespace.cldrengine.api.CurrencyType;
import com.squarespace.cldrengine.api.Decimal;

public class Sketch16 {

  public static void main(String[] args) {
    String id = "pt-PT";
    CLDR cldr = CLDR.get(id);

    String s = "";

    Decimal n = new Decimal("2");
    CurrencyFormatOptions opts = CurrencyFormatOptions.build();

    s = cldr.Numbers.formatCurrency(n, CurrencyType.PTE, opts);
    System.out.println(s);
//    Decimal n = new Decimal("3.141592653589793238462643383280");
//    CurrencyFormatOptions opts = CurrencyFormatOptions.build()
//      .style(CurrencyFormatStyleType.SHORT)
//      .cash(false);

//    s = cldr.Numbers.formatCurrency(n, CurrencyType.CAD, opts);
//    System.out.println(s);

//    Decimal n = new Decimal("1");
//    DecimalFormatOptions opts = DecimalFormatOptions.build()
//        .style(DecimalFormatStyleType.SHORT);
  }
}
