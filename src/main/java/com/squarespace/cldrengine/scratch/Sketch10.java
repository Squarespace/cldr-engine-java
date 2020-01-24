package com.squarespace.cldrengine.scratch;

import java.util.List;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CurrencyFormatOptions;
import com.squarespace.cldrengine.api.CurrencyFormatStyleType;
import com.squarespace.cldrengine.api.CurrencyType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;
import com.squarespace.cldrengine.api.Part;

public class Sketch10 {

  public static void main(String[] args) {
    String id = "en";
    CLDR cldr = CLDR.get(id);
    Decimal n = new Decimal("-7812345.4444444");
    n.scientific(1);
    DecimalFormatOptions options = DecimalFormatOptions.build()
        .style(DecimalFormatStyleType.SCIENTIFIC)
        .minimumSignificantDigits(5);
//        .minimumFractionDigits(1);

    String s = cldr.Numbers.formatDecimal(n, options);
    System.out.println(s);

    CurrencyFormatOptions copts = CurrencyFormatOptions.build()
        .group(true)
        .style(CurrencyFormatStyleType.CODE);
    s = cldr.Numbers.formatCurrency(n, CurrencyType.USD, copts);
    System.out.println(s);

    copts.style(CurrencyFormatStyleType.NAME);
    s = cldr.Numbers.formatCurrency(n, CurrencyType.USD, copts);
    System.out.println(s);

    copts.style(CurrencyFormatStyleType.SHORT);
    s = cldr.Numbers.formatCurrency(n, CurrencyType.USD, copts);
    System.out.println(s);

    copts.style(CurrencyFormatStyleType.ACCOUNTING);
    s = cldr.Numbers.formatCurrency(n, CurrencyType.USD, copts);
    System.out.println(s);

    copts.style(CurrencyFormatStyleType.SYMBOL);
    s = cldr.Numbers.formatCurrency(n, CurrencyType.USD, copts);
    System.out.println(s);

    List<Part> parts = cldr.Numbers.formatDecimalToParts(n, options);
    System.out.println(parts);
  }
}
