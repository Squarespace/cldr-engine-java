package com.squarespace.cldrengine.numbering;

import com.squarespace.cldrengine.decimal.Decimal;
import com.squarespace.cldrengine.decimal.StringDecimalFormatter;

public class DecimalNumberingSystem extends NumberingSystem {

  private final String[] digits;

  public DecimalNumberingSystem(String name, String[] digits, NumberSymbols symbols,
      int minimumGroupingDigits, int primaryGroupingSize, int secondaryGroupingSize) {
    super(name, symbols, minimumGroupingDigits, primaryGroupingSize, secondaryGroupingSize);
    this.digits = digits;
  }

  @Override
  public String formatString(long n, boolean groupDigits, int minInt) {
    return formatString(Decimal.coerce(n), groupDigits, minInt);
  }

  @Override
  public String formatString(Decimal n, boolean groupDigits, int minInt) {
    if (!groupDigits && n.isInteger()) {
      return fastFormatDecimal(n.toString(), minInt);
    }
    return formatDecimal(n, groupDigits, minInt);
  }

  protected String formatDecimal(Decimal n, boolean groupDigits, int minInt) {
    String group = groupDigits ? this.symbols.getOrDefault("group", "") : "";
    String decimal = this.symbols.getOrDefault("decimal", "");
    StringDecimalFormatter fmt = new StringDecimalFormatter();
    n.format(fmt, decimal, group, minInt, this.minimumGroupingDigits,
        this.primaryGroupingSize, this.secondaryGroupingSize, true, this.digits);
    return fmt.render();
  }

  private String fastFormatDecimal(String n, int minInt) {
    StringBuilder buf = new StringBuilder();
    int len = n.length();
    for (int i = 0; i < len; i++) {
      char c = n.charAt(i);
      switch (c) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          buf.append(this.digits[c - '0']);
          break;
      }
    }
    int diff = minInt - buf.length();
    if (diff > 0) {
      StringBuilder pfx = new StringBuilder();
      while (diff-- > 0) {
        pfx.append(this.digits[0]);
      }
      pfx.append(buf);
      return pfx.toString();
    }
    return buf.toString();
  }

}
