package com.squarespace.cldrengine.numbers;

import java.util.List;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.general.GeneralInternals;
import com.squarespace.cldrengine.parsing.NumberPattern;

public interface NumberRenderer<R> {

  R empty();
  R make(String type, String value);
  R render(Decimal n, NumberPattern pattern, String currencySybol, String percentSymbol,
      String decimalSymbol, int minInt, Boolean grouping, Integer exponent);
  R wrap(GeneralInternals internal, String raw, List<R> args);

}
