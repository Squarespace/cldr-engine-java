package com.squarespace.cldrengine.units;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.util.Arrays;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.Quantity;
import com.squarespace.cldrengine.api.UnitFormatOptions;
import com.squarespace.cldrengine.api.UnitFormatStyleType;
import com.squarespace.cldrengine.api.UnitLength;
import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.decimal.DecimalConstants;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.UnitInfo;
import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.numbers.NumberRenderer;
import com.squarespace.cldrengine.utils.Pair;

public class UnitInternals {

  private final Internals internals;

  public UnitInternals(Internals internals) {
    this.internals = internals;
  }

  public String getDisplayName(Bundle bundle, UnitType unit, UnitLength length) {
    return this.getUnitInfo(length).displayName.get(bundle, unit);
  }

  public <T> T format(Bundle bundle, NumberRenderer<T> renderer, Quantity q,
      UnitFormatOptions options, NumberParams params) {

    Decimal n = q.value.or(DecimalConstants.ZERO);

    DecimalFormatOptions opts = convert(options);
    Pair<T, PluralType> pair = this.internals.numbers.formatDecimal(bundle, renderer, n, opts, params);
    T num = pair._1;
    if (!q.unit.ok()) {
      return num;
    }
    PluralType plural = pair._2;

    // Compute plural category for the value '1'
    PluralType singular = bundle.plurals().cardinal(DecimalConstants.ONE);

    // For default and "per" compound pattern, the {0} will use
    // the plural category and {1} will be singular. Examples:
    //   1 meter per second
    //  10 meters per second
    //
    // For the 'times' compound pattern, the {0} will be singular,
    // and the {1} will use the plural category. Examples:
    //   1 newton-meter
    //  10 newton-meters

    PluralType plural0 = q.times.ok() ? singular : plural;
    PluralType plural1 = q.times.ok() ? plural : singular;

    UnitInfo info = this.getUnitInfo(options.length.or(UnitLength.LONG));
    String pattern = info.unitPattern.get(bundle, plural0, q.unit.get());
    if (isEmpty(pattern)) {
      // Fallback to other. Some locales don't break out a pattern per category
      // when the patterns are identical
      pattern = info.unitPattern.get(bundle, PluralType.OTHER, q.unit.get());
    }

    // Format argument '{0}' here. If no 'per' unit is defined, we
    // return it. Otherwise we join it with the denominator unit below.
    T zero = renderer.wrap(this.internals.general, pattern, Arrays.asList(num));
    if (q.per.ok()) {
      // Check if the 'per' unit has a perUnitPattern defined and use it.
      String perPattern = info.perUnitPattern.get(bundle, q.per.get());
      if (!isEmpty(perPattern)) {
        return renderer.wrap(this.internals.general, perPattern, Arrays.asList(zero));
      }
    }

    // If per or times are specified, use use the corresponding compound pattern.
    // See notes here:
    // https://www.unicode.org/reports/tr35/tr35-general.html#perUnitPatterns
    String compound = q.per.ok() ? info.perPattern.get(bundle)
         : q.times.ok() ? info.timesPattern.get(bundle) : "";
    UnitType perunit = q.per.or(q.times.get());
    if (perunit != null) {
      // Fetch the denominator's unit pattern, strip off the '{0}'
      // and any surrounding whitespace.
      String denom = info.unitPattern.get(bundle, plural1, perunit);
      denom = denom.replace("\\s*\\{0\\}\\s*", "");
      T one = renderer.make("per", denom);

      // Wrap the numerator and denominator together
      return renderer.wrap(this.internals.general, compound, Arrays.asList(zero, one));
    }

    return zero;
  }

  protected UnitInfo getUnitInfo(UnitLength length) {
    switch (length) {
      case NARROW:
        return this.internals.schema.Units.narrow;
      case SHORT:
        return this.internals.schema.Units.short_;
      default:
        return this.internals.schema.Units.long_;
    }
  }

  protected DecimalFormatOptions convert(UnitFormatOptions opts) {
    DecimalFormatStyleType style;
    switch (opts.style.or(UnitFormatStyleType.DECIMAL)) {
      case SHORT:
        style = DecimalFormatStyleType.SHORT;
        break;
      case LONG:
        style = DecimalFormatStyleType.LONG;
        break;
      case SCIENTIFIC:
        style = DecimalFormatStyleType.SCIENTIFIC;
        break;
      case DECIMAL:
      default:
        style = DecimalFormatStyleType.DECIMAL;
        break;
    }
    return DecimalFormatOptions.build()
        .style(style)
        .negativeZero(false)
        .group(opts.group)
        .divisor(opts.divisor)
        .minimumIntegerDigits(opts.minimumIntegerDigits)
        .maximumFractionDigits(opts.maximumFractionDigits)
        .minimumFractionDigits(opts.minimumFractionDigits)
        .maximumSignificantDigits(opts.maximumSignificantDigits)
        .minimumSignificantDigits(opts.minimumSignificantDigits);
  }
}
