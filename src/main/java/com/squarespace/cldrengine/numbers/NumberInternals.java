package com.squarespace.cldrengine.numbers;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;
import static com.squarespace.cldrengine.utils.TypeUtils.defaulter;

import java.util.List;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalAdjustOptions;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;
import com.squarespace.cldrengine.api.NumberSymbolType;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.RoundingModeType;
import com.squarespace.cldrengine.internal.CurrenciesSchema;
import com.squarespace.cldrengine.internal.DecimalFormats;
import com.squarespace.cldrengine.internal.DigitsArrow;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.NumberSystemInfo;
import com.squarespace.cldrengine.internal.NumbersSchema;
import com.squarespace.cldrengine.parsing.NumberPattern;
import com.squarespace.cldrengine.parsing.NumberPatternParser;
import com.squarespace.cldrengine.plurals.PluralRules;
import com.squarespace.cldrengine.utils.Cache;
import com.squarespace.cldrengine.utils.Pair;

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
    NumberSystemInfo info = this.numbers.numberSystem.get(params.numberSystemName.value());
    if (info == null) {
      info = latnInfo;
    }

    DecimalFormats decimalFormats = info.decimalFormats;
    DecimalFormats latnDecimalFormats = latnInfo.decimalFormats;
    String standardRaw = decimalFormats.standard.get(bundle);
    if (isEmpty(standardRaw)) {
      standardRaw = latnDecimalFormats.standard.get(bundle);
    }

    PluralRules plurals = bundle.plurals();

    switch (style) {
      case LONG:
      case SHORT: {
        boolean isShort = style == DecimalFormatStyleType.SHORT;
        boolean useLatn = decimalFormats.short_.get(bundle, PluralType.OTHER, 4)._2 != 0;
        DigitsArrow<PluralType> patternImpl = isShort
            ? (useLatn ? latnInfo.decimalFormats.short_ : decimalFormats.short_)
            : (useLatn ? latnInfo.decimalFormats.long_ : decimalFormats.long_);
        NumberContext ctx = new NumberContext(options, round, true, false);

        // Adjust the number using the compact pattern and divisor.
        Pair<Decimal, Integer> pair;
        if (options.divisor.ok()) {
          pair = this.setupCompactDivisor(bundle, n, ctx, standardRaw, options.divisor.get(), patternImpl);
        } else {
          pair = this.setupCompact(bundle, n, ctx, standardRaw, patternImpl);
        }
        Decimal q2 = pair._1;
        int ndigits = pair._2;

        q2 = negzero(q2, options.negativeZero.or(false));

        // Compute the plural category for the final q2.
        plural = plurals.cardinal(q2);

        // Select the final pluralized compact pattern based on the integer
        // digits of n and the plural category of the rounded / shifted number q2.
        String raw = patternImpl.get(bundle, plural, ndigits)._1;
        if (isEmpty(raw)) {
          raw = standardRaw;
        }

        // Re-select pattern as number may have changed sign due to rounding.
        NumberPattern pattern = this.getNumberPattern(raw, q2.isNegative());
        result = renderer.render(q2, pattern, "", "", "", ctx.minInt, options.group.get(), null);
        break;
      }

      case PERCENT:
      case PERCENT_SCALED:
      case PERMILLE:
      case PERMILLE_SCALED: {
        // Get percent pattern
        String raw = info.percentFormat.get(bundle);
        if (raw == null) {
          raw = latnInfo.percentFormat.get(bundle);
        }
        NumberPattern pattern = this.getNumberPattern(raw, n.isNegative());

        // Scale the number to a percent or permille form as needed.
        if (style == DecimalFormatStyleType.PERCENT) {
          n = n.movePoint(2);
        } else if (style == DecimalFormatStyleType.PERMILLE) {
          n = n.movePoint(3);
        }

        // Select percent or permille symbol.
        String symbol = (style == DecimalFormatStyleType.PERCENT || style == DecimalFormatStyleType.PERCENT_SCALED) ?
            params.symbols.get(NumberSymbolType.PERCENTSIGN) : params.symbols.get(NumberSymbolType.PERMILLE);

        // Adjust number using pattern and options, then render.
        NumberContext ctx = new NumberContext(options, round, false, false, -1);
        ctx.setPattern(pattern, false);
        n = ctx.adjust(n);
        n = negzero(n, options.negativeZero.or(false));
        plural = plurals.cardinal(n);

        // Re-select pattern as number may have changed sign due to rounding
        pattern = this.getNumberPattern(raw, n.isNegative());
        result = renderer.render(n, pattern, "", symbol, "", ctx.minInt, options.group.get(), null);
        break;
      }

      case DECIMAL: {
        // Get decimal pattern
        NumberPattern pattern = this.getNumberPattern(standardRaw, n.isNegative());

        // Adjust number using pattern and options, then render.
        NumberContext ctx = new NumberContext(options, round, false, false, -1);
        ctx.setPattern(pattern, false);
        n = ctx.adjust(n);
        n = negzero(n, options.negativeZero.or(false));
        plural = plurals.cardinal(n);

        // Re-select pattern as number may have changed sign due to rounding.
        pattern = this.getNumberPattern(standardRaw, n.isNegative());
        result = renderer.render(n, pattern, "", "", "", ctx.minInt, options.group.get(), null);
        break;
      }

      case SCIENTIFIC: {
        NumberContext ctx = new NumberContext(options, round, false, true, -1);
        String format = info.scientificFormat.get(bundle);
        if (isEmpty(format)) {
          format = latnInfo.scientificFormat.get(bundle);
        }
        NumberPattern pattern = this.getNumberPattern(format, n.isNegative());

        ctx.setPattern(pattern, true);
        n = ctx.adjust(n, true);
        // output negative zero by default for scientific
        n = negzero(n, options.negativeZero.or(true) != false);

        pattern = this.getNumberPattern(format, n.isNegative());

        // Split number into coefficient and exponent
        Decimal.Scientific sci = n.scientific(ctx.minInt == 0 ? 1 : ctx.minInt);

        Decimal adjcoeff = ctx.adjust(sci.coefficient, true);
        result = renderer.render(adjcoeff, pattern, "", "", "", 1, false, sci.exponent);
        break;
      }


      default:
        result = renderer.empty();
        break;
    }

    return Pair.of(result, plural);
  }

  protected Decimal negzero(Decimal n, boolean show) {
    return !show && n.isZero() && n.isNegative() ? n.abs() : n;
  }

  protected Pair<Decimal, Integer> setupCompact(Bundle bundle, Decimal n, NumberContext ctx,
      String standardRaw, DigitsArrow<PluralType> patternImpl) {

    // Select the correct divisor based on the number of integer digits in n.
    boolean negative = n.isNegative();
    int ndigits = n.integerDigits();

    // Select the initial compact pattern based on the integer digits of n.
    // The plural category doesn't matter until the final pattern is selected.
    Pair<String, Integer> pair = patternImpl.get(bundle, PluralType.OTHER, ndigits);
    String raw = pair._1;
    int ndivisor = pair._2;
    NumberPattern pattern = this.getCompactPattern(raw, standardRaw, negative);
    int fracDigits = ctx.useSignificant ? -1 : 0;

    // Move the decimal point of n, producing q1. we always strip trailing
    // zeros on compact patterns.
    Decimal q1 = n;
    if (ndivisor > 0) {
      q1 = q1.movePoint(-ndivisor);
    }

    // Adjust q1 using the compact pattern's parameters, to produce q2.
    int q1digits = q1.integerDigits();
    ctx.setCompact(pattern, q1digits, ndivisor, fracDigits);

    Decimal q2 = ctx.adjust(q1);
    int q2digits = q2.integerDigits();
    negative = q2.isNegative();

    // Check if the number rounded up, adding another integer digit.
    if (q2digits > q1digits) {
      // Select a new divisor and pattern.
      ndigits++;

      pair = patternImpl.get(bundle, PluralType.OTHER, ndigits);
      raw = pair._1;
      int divisor = pair._2;
      pattern = this.getCompactPattern(raw, standardRaw, negative);

      // If divisor changed we need to divide and adjust again. We don't divide,
      // we just move the decimal point, since our Decimal type uses a radix that
      // is a power of 10. Otherwise q2 is ready for formatting.
      if (divisor > ndivisor) {
        // We shift right before we move the decimal point. This triggers rounding
        // of the number at its correct scale. Otherwise we would end up with
        // 999,999 becoming 0.999999 and half-even rounding truncating the
        // number to '0M' instead of '1M'.
        q1 = n.shiftright(divisor, RoundingModeType.HALF_EVEN);
        q1 = q1.movePoint(-divisor);
        ctx.setCompact(pattern, q1.integerDigits(), divisor, fracDigits);
        q2 = ctx.adjust(q1);
      }
    }

    return Pair.of(q2, ndigits);
  }

  protected Pair<Decimal, Integer> setupCompactDivisor(Bundle bundle, Decimal n, NumberContext ctx,
      String standardRaw, int divisor, DigitsArrow<PluralType> patternImpl) {
    boolean negative = n.isNegative();
    int ndigits = (int)Math.log10(divisor) + 1;

    // Select compact patterns based on number of digits in divisor
    Pair<String, Integer> pair = patternImpl.get(bundle, PluralType.OTHER, ndigits);
    String raw = pair._1;
    int ndivisor = pair._2;

    if (ndivisor > 0) {
      n = n.movePoint(-ndivisor);
    }

    NumberPattern pattern = this.getCompactPattern(raw, standardRaw, negative);
    int fracDigits = ctx.useSignificant ? -1 : 0;
    boolean noMinInt = ctx.minInt == -1;
    ctx.setCompact(pattern, n.integerDigits(), ndivisor, fracDigits);
    // Hack to avoid extra leading '0' for certain divisor cases
    if (noMinInt) {
      ctx.minInt = 1;
    }
    return Pair.of(ctx.adjust(n), ndigits);
  }

  protected NumberPattern getCompactPattern(String raw, String standardRaw, boolean negative) {
    if (!isEmpty(raw)) {
      return this.getNumberPattern(raw, negative);
    }
    // Adjust standard pattern to have same fraction settings as compact
    NumberPattern pattern = new NumberPattern(this.getNumberPattern(standardRaw, negative));
    pattern.minFrac = 0;
    pattern.maxFrac = 0;
    return pattern;
  }
}
