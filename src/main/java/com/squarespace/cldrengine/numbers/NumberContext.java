package com.squarespace.cldrengine.numbers;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalAdjustOptions;
import com.squarespace.cldrengine.api.NumberFormatOptions;
import com.squarespace.cldrengine.api.Option;
import com.squarespace.cldrengine.api.RoundingModeType;
import com.squarespace.cldrengine.parsing.NumberPattern;

public class NumberContext {

  public final DecimalAdjustOptions options;
  public final RoundingModeType roundingMode;
  public boolean useSignificant;
  public int minInt = -1;
  public int maxFrac = -1;
  public int minFrac = -1;
  public int maxSig = -1;
  public int minSig = -1;
  public int currencyDigits = -1;

  public NumberContext(DecimalAdjustOptions options, RoundingModeType roundingMode,
      boolean compact, boolean scientific) {
    this(options, roundingMode, compact, scientific, -1);
  }

  public NumberContext(DecimalAdjustOptions options, RoundingModeType roundingMode,
      boolean compact, boolean scientific, int currencyDigits) {
    this.options = options;
    this.roundingMode = roundingMode;
    this.currencyDigits = currencyDigits;

    DecimalAdjustOptions o = options;
    // Determine if we should use default or significant digit modes. If we're in compact mode
    // we will use significant digits unless any fraction option is set. Otherwise we use
    // significant digits if any significant digit option is set.
    boolean optFrac = o.minimumFractionDigits.ok() || o.maximumFractionDigits.ok();
    boolean optSig = o.minimumSignificantDigits.ok() || o.maximumSignificantDigits.ok();
    this.useSignificant = (scientific && !optFrac) || (compact && !optFrac) || optSig;
  }

  /**
   * Set a pattern. The 'scientific' flag indicates the pattern uses significant
   * digits, which we will copy from the pattern's min/max fractions.
   */
  public void setPattern(NumberPattern pattern, boolean scientific) {
    this._setPattern(pattern, scientific, -1, -1, -1);
  }

  /**
   * Set a compact pattern.
   */
  public void setCompact(NumberPattern pattern, int integerDigits, int divisor, int maxFracDigits) {
    int maxSigDigits = Math.max(pattern.minInt, integerDigits);
    if (integerDigits == 1) {
      maxSigDigits++;
    }
    this._setPattern(pattern, false, maxSigDigits, 1, maxFracDigits);
  }

  /**
   * Adjust the scale of the number using the resolved parameters.
   */
  public Decimal adjust(Decimal n) {
    return adjust(n, false);
  }

  /**
   * Adjust the scale of the number using the resolved parameters.
   */
  public Decimal adjust(Decimal n, boolean scientific) {

    // TODO: consider moving this logic into Decimal since it could be useful
    // to adjust a number using several options in a single pass. Could be
    // more efficient, making fewer copies.

    if (this.useSignificant && scientific) {
      if (this.minSig <= 0) {
        this.minSig = 1;
      }
      if (this.maxSig <= 0) {
        this.maxSig = 1;
      }
    }

    if (this.useSignificant && this.minSig > 0 && this.maxSig > 0) {
      if (n.precision() > this.maxSig) {
        // Scale the number to have at most the maximum significant digits.
        int scale = this.maxSig - n.precision() + n.scale();
        n = n.setScale(scale, this.roundingMode);
      }

      // Ensure that one less digit is emitted if the number is exactly zero.
      n = n.stripTrailingZeros();
      boolean zero = n.isZero();
      int precision = n.precision();
      if (zero && n.scale() == 1) {
        precision--;
      }

      // scale the number to have at least the minimum significant digits
      if (precision < this.minSig) {
        int scale = this.minSig - precision + n.scale();
        n = n.setScale(scale, this.roundingMode);
      }

    } else {
      // Precise control over number of integer and decimal digits to include, e.g. when
      // formatting exact currency values.
      int scale = Math.max(this.minFrac, Math.min(n.scale(), this.maxFrac));

      n = n.setScale(scale, this.roundingMode);
      n = n.stripTrailingZeros();

      // Ensure minimum fraction digits is met.
      if (n.scale() < this.minFrac) {
        n = n.setScale(this.minFrac, this.roundingMode);
      }
    }

    return n;
  }

  /**
   * Set context parameters from options, pattern and significant digit arguments.
   */
  private void _setPattern(NumberPattern pattern, boolean scientific, int maxSigDigits,
      int minSigDigits, int maxFracDigits) {

    DecimalAdjustOptions o = this.options;

    this.minInt = o.minimumIntegerDigits.or(pattern.minInt);
    this.minFrac = this.currencyDigits == -1 ? pattern.minFrac : this.currencyDigits;
    this.maxFrac = this.currencyDigits == -1 ? pattern.maxFrac : this.currencyDigits;

    Integer minFrac = o.minimumFractionDigits.get();
    Integer maxFrac = o.maximumFractionDigits.get();
    if (minFrac == null && maxFrac == null && maxFracDigits > -1) {
      maxFrac = maxFracDigits;
    }

    if (maxFrac != null && maxFrac > -1) {
      this.maxFrac = maxFrac;
    }

    if (minFrac != null && minFrac > -1) {
      this.minFrac = minFrac != null && (maxFrac != null && maxFrac > -1)
          ? (maxFrac < minFrac ? maxFrac : minFrac) : minFrac;
      if (this.minFrac > this.maxFrac) {
        this.maxFrac = this.minFrac;
      }
    }

    if (maxFrac != null && maxFrac > -1) {
      if (this.maxFrac < this.minFrac || this.minFrac == -1) {
        this.minFrac = this.maxFrac;
      }
    }

    if (this.useSignificant || scientific) {
      Option<Integer> optMinSig = o.minimumSignificantDigits;
      Option<Integer> optMaxSig = o.maximumSignificantDigits;

      int minSig = scientific ? optMinSig.or(pattern.minFrac) : optMinSig.or(minSigDigits);
      int maxSig = scientific ? optMaxSig.or(pattern.maxFrac) : optMaxSig.or(maxSigDigits);

      if (minSig != -1 && minSig > maxSig) {
        maxSig = minSig;
      }

      this.minSig = minSig == -1 ? maxSig : minSig;
      this.maxSig = maxSig == -1 ? minSig : maxSig;
    } else {
      this.minSig = -1;
      this.maxSig = -1;
    }
  }
}
