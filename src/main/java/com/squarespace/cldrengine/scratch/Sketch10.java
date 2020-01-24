package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CurrencyFormatOptions;
import com.squarespace.cldrengine.api.CurrencyFormatStyleType;
import com.squarespace.cldrengine.api.CurrencyType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;

public class Sketch10 {

  public static void main(String[] args) {
    String id = "en";
    CLDR cldr = CLDR.get(id);
    Decimal n = new Decimal("1249.99");
    n.scientific(1);
    DecimalFormatOptions options = DecimalFormatOptions.build()
        .style(DecimalFormatStyleType.SCIENTIFIC)
        .minimumSignificantDigits(5);
//        .minimumFractionDigits(1);

    String s = cldr.Numbers.formatDecimal(n, options);
//    System.out.println(s);

    CurrencyType[] codes = new CurrencyType[] {
      CurrencyType.ARS,
      CurrencyType.BRL,
      CurrencyType.COP,
      CurrencyType.IDR,
      CurrencyType.INR,
      CurrencyType.JPY,
      CurrencyType.ZAR
    };

    for (CurrencyType code : codes) {
      System.out.println("\n");
      System.out.println(cldr.Numbers.getCurrencyDisplayName(code, null));
      System.out.println(cldr.Numbers.getCurrencyPluralName(n, code, null));
      System.out.println(cldr.Numbers.getCurrencySymbol(code, null));

      CurrencyFormatOptions copts = CurrencyFormatOptions.build()
          .group(true)
          .style(CurrencyFormatStyleType.CODE);
      s = cldr.Numbers.formatCurrency(n, code, copts);
//      System.out.println(s);

      copts.style(CurrencyFormatStyleType.NAME);
      s = cldr.Numbers.formatCurrency(n, code, copts);
//      System.out.println(s);

      copts.style(CurrencyFormatStyleType.SHORT);
      s = cldr.Numbers.formatCurrency(n, code, copts);
//      System.out.println(s);

      copts.style(CurrencyFormatStyleType.ACCOUNTING);
      s = cldr.Numbers.formatCurrency(n, code, copts);
//      System.out.println(s);

      copts.style(CurrencyFormatStyleType.SYMBOL);
      s = cldr.Numbers.formatCurrency(n, code, copts);
      System.out.println(s);

    }
//    List<Part> parts = cldr.Numbers.formatDecimalToParts(n, options);
//    System.out.println(parts)
  }
}
