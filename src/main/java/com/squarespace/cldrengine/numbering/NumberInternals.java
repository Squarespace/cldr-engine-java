package com.squarespace.cldrengine.numbering;

import com.squarespace.cldrengine.internal.CurrenciesSchema;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.NumbersSchema;
import com.squarespace.cldrengine.parsing.NumberPattern;
import com.squarespace.cldrengine.parsing.NumberPatternParser;
import com.squarespace.cldrengine.utils.Cache;

public class NumberInternals {

  private final Internals internals;
  private final CurrenciesSchema currencies;
  private final NumbersSchema numbers;
  private final Cache<NumberPattern[]> numberPatternCache;

  public NumberInternals(Internals internals) {
    this.internals = internals;
    this.currencies = internals.schema.Currencies;
    this.numbers = internals.schema.Numbers;
    this.numberPatternCache = new Cache<>(NumberPatternParser::parse, 256);
  }

  public NumberPattern getNumberPattern(String raw, boolean negative) {
    return numberPatternCache.get(raw)[negative ? 1 : 0];
  }
}
