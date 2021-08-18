package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.plurals.NumberOperands;

public interface PluralRules {

  NumberOperands operands(Decimal d);

  PluralType cardinal(Decimal n);
  PluralType cardinal(Decimal n, int c);

  PluralType ordinal(Decimal n);
}
