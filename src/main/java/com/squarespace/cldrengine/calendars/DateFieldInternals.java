package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.util.Map;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.RelativeTimeFieldFormatOptions;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;
import com.squarespace.cldrengine.decimal.DecimalConstants;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.RelativeTimeFields;
import com.squarespace.cldrengine.internal.RelativeTimes;
import com.squarespace.cldrengine.internal.Vector2Arrow;
import com.squarespace.cldrengine.numbers.NumberParams;

public class DateFieldInternals {

  private final Internals internals;
  private final RelativeTimes relativeTimes;

  public DateFieldInternals(Internals internals) {
    this.internals = internals;
    this.relativeTimes = internals.schema.DateFields.relativeTimes;
  }

  public String formatRelativeTimeField(Bundle bundle, Decimal n, RelativeTimeFieldType field,
      RelativeTimeFieldFormatOptions options, NumberParams params, Map<ContextTransformFieldType, String> transform) {

    DateFieldWidthType width = options.width.or(DateFieldWidthType.WIDE);
    RelativeTimeFields format = selectFormat(width);
    boolean group = options.group.or(true);
    n = this.internals.numbers.adjustDecimal(n, options);
    boolean negative = n.isNegative();
    if (negative) {
      n = n.negate();
    }

    boolean iszero = n.isZero();
    String res = null;

    boolean alwaysNow = options.alwaysNow.or(false);
    boolean numericOnly = options.numericOnly.or(false);
    if (iszero) {
      if (alwaysNow || !numericOnly) {
        res = format.current.get(bundle, field);
      }

    } else if (!numericOnly) {
      switch (field) {
        case HOUR:
        case MINUTE:
        case SECOND:
          break;
        default:
          if (n.compare(DecimalConstants.TWO) == 0) {
            String p = negative ? format.previous2.get(bundle, field) : format.next2.get(bundle, field);
            if (!isEmpty(p)) {
              res = p;
            }
            // Fall through
          } else if (n.compare(DecimalConstants.ONE) == 0) {
            res = negative ? format.previous.get(bundle, field) : format.next.get(bundle, field);
          }
          break;
      }
    }

    // If we output anything above, return it.
    if (res != null) {
      if (options.context.ok()) {
        res = this.internals.general.contextTransform(res, transform, options.context.get(), ContextTransformFieldType.RELATIVE);
      }
      return res;
    }

    // Format a pluralized future / past.
    PluralType plural = bundle.plurals().cardinal(n);
    Vector2Arrow<PluralType, RelativeTimeFieldType> arrow = negative ? format.past : format.future;
    String raw = arrow.get(bundle, plural, field);
    if (options.context.ok()) {
      raw = this.internals.general.contextTransform(raw, transform, options.context.get(), ContextTransformFieldType.RELATIVE);
    }
    String num = params.system.formatString(n, group, 1);
    return this.internals.general.formatWrapper(raw, num);
  }

  protected RelativeTimeFields selectFormat(DateFieldWidthType width) {
    if (width != null) {
      switch (width) {
        case SHORT:
          return this.relativeTimes.short_;
        case NARROW:
          return this.relativeTimes.narrow;
        default:
          break;
      }
    }
    return this.relativeTimes.wide;
  }
}
