package com.squarespace.cldrengine.numbers;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;
import static com.squarespace.cldrengine.utils.TypeUtils.defaulter;

import java.util.List;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalAdjustOptions;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.RoundingModeType;
import com.squarespace.cldrengine.internal.CurrenciesSchema;
import com.squarespace.cldrengine.internal.DecimalFormats;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.NumberSystemInfo;
import com.squarespace.cldrengine.internal.NumbersSchema;
import com.squarespace.cldrengine.parsing.NumberPattern;
import com.squarespace.cldrengine.parsing.NumberPatternParser;
import com.squarespace.cldrengine.utils.Cache;
import com.squarespace.cldrengine.utils.Pair;
import com.squarespace.cldrengine.utils.StringUtils;

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

  public NumberRenderer<String> stringRenderer(NumberParams params) {
    return new StringNumberFormatter(params);
  }

  public NumberRenderer<List<Part>> partsRenderer(NumberParams params) {
    return new PartsNumberFormatter(params);
  }

  public NumberPattern getNumberPattern(String raw, boolean negative) {
    return numberPatternCache.get(raw)[negative ? 1 : 0];
  }

  public <T> Pair<T, PluralType> formatDecimal(Bundle bundle, NumberRenderer<T> renderer, Decimal n,
      DecimalFormatOptions options, NumberParams params) {

    // TODO: abstract away pattern selection defaulting
    DecimalFormatStyleType style = options.style.or(DecimalFormatStyleType.DECIMAL);
    T result;
    PluralType plural = PluralType.OTHER;
    RoundingModeType round = options.round.or(RoundingModeType.HALF_EVEN);

    NumberSystemInfo latnInfo = this.numbers.numberSystem.get("latn");
    NumberSystemInfo info = this.numbers.numberSystem.get(params.numberSystemName);
    if (info == null) {
      info = latnInfo;
    }

    DecimalFormats decimalFormats = info.decimalFormats;
    DecimalFormats latnDecimalFormats = latnInfo.decimalFormats;
    String standardRaw = decimalFormats.standard.get(bundle);
    if (isEmpty(standardRaw)) {
      standardRaw = latnDecimalFormats.standard.get(bundle);
    }

    // TODO:
    // const plurals = bundle.plurals();

    return Pair.of(renderer.empty(), plural);
  }
}
