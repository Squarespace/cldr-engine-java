package com.squarespace.cldrengine.numbers;

import static com.squarespace.cldrengine.options.Option.option;

import com.squarespace.cldrengine.decimal.RoundingModeType;
import com.squarespace.cldrengine.options.Option;

public class DecimalAdjustOptions {

  public final Option<RoundingModeType> round = option();
  public final Option<Integer> minimumIntegerDigits = option();
  public final Option<Integer> maximumFractionDigits = option();
  public final Option<Integer> minimumFractionDigits = option();
  public final Option<Integer> maximumSignificantDigits = option();
  public final Option<Integer> minimumSignificantDigits = option();

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private RoundingModeType round;
    private Integer minimumIntegerDigits;
    private Integer maximumFractionDigits;
    private Integer minimumFractionDigits;
    private Integer maximumSignificantDigits;
    private Integer minimumSignificantDigits;

    public Builder round(RoundingModeType round) {
      this.round = round;
      return this;
    }

    public Builder minimumIntegerDigits(Integer digits) {
      this.minimumIntegerDigits = digits;
      return this;
    }

    public Builder maximumFractionDigits(Integer digits) {
      this.maximumFractionDigits = digits;
      return this;
    }

    public Builder minimumFractionDigits(Integer digits) {
      this.minimumFractionDigits = digits;
      return this;
    }

    public Builder maximumSignificantDigits(Integer digits) {
      this.maximumSignificantDigits = digits;
      return this;
    }

    public Builder minimumSignificantDigits(Integer digits) {
      this.minimumSignificantDigits = digits;
      return this;
    }

    public DecimalAdjustOptions build() {
      DecimalAdjustOptions d = new DecimalAdjustOptions();
      d.round.set(round);
      d.minimumIntegerDigits.set(minimumIntegerDigits);
      d.maximumFractionDigits.set(maximumFractionDigits);
      d.minimumFractionDigits.set(minimumFractionDigits);
      d.maximumSignificantDigits.set(maximumSignificantDigits);
      d.minimumSignificantDigits.set(minimumSignificantDigits);
      return d;
    }
  }
}
