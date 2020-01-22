package com.squarespace.cldrengine.numbers;

import static com.squarespace.cldrengine.utils.TypeUtils.defaulter;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalAdjustOptions;
import com.squarespace.cldrengine.api.RoundingModeType;
import com.squarespace.cldrengine.internal.CurrenciesSchema;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.NumbersSchema;
import com.squarespace.cldrengine.parsing.NumberPattern;
import com.squarespace.cldrengine.parsing.NumberPatternParser;
import com.squarespace.cldrengine.utils.Cache;

public class NumberInternals {

  private static final DecimalAdjustOptions ADJUST_OPTIONS = DecimalAdjustOptions.build()
      .minimumIntegerDigits(0)
      .round(RoundingModeType.HALF_EVEN);

    private static final NumberPattern ADJUST_PATTERN = NumberPatternParser.parse("0")[0];

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

  public Decimal adjustDecimal(Decimal num, DecimalAdjustOptions options) {
    options = defaulter(options, DecimalAdjustOptions::build)
        .merge(ADJUST_OPTIONS);
    NumberContext ctx = new NumberContext(options, options.round.get(), false, false);
    ctx.setPattern(ADJUST_PATTERN, false);
    return ctx.adjust(num);
  }

  public NumberPattern getNumberPattern(String raw, boolean negative) {
    return numberPatternCache.get(raw)[negative ? 1 : 0];
  }

}
