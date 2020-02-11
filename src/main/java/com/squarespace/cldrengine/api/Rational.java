package com.squarespace.cldrengine.api;

import com.squarespace.cldrengine.decimal.DecimalConstants;

/**
 * Arbitrary precision rational type.
 */
public class Rational {

  private final Decimal numer;
  private final Decimal denom;

  public Rational(String repr) {
    int i = repr.indexOf('/');
    if (i == -1) {
      this.numer = new Decimal(repr);
      this.denom = DecimalConstants.ONE;
    } else {
      this.numer = parse(repr.substring(0, i).trim());
      this.denom = parse(repr.substring(i + 1).trim());
    }
  }

  public Rational(Decimal numer, Decimal denom) {
    this.numer = numer;
    this.denom = denom;
  }

  public Decimal numerator() {
    return this.numer;
  }

  public Decimal denominator() {
    return this.denom;
  }

  public int compare(Rational arg, MathContext context) {
    Decimal a = this.numer.multiply(arg.denom, context);
    Decimal b = arg.numer.multiply(this.denom, context);
    return a.compare(b);
  }

  public Rational divide(Rational n, MathContext context) {
    return new Rational(
        this.numer.multiply(n.denom, context),
        this.denom.multiply(n.numer, context));
  }

  public Rational multiply(Rational n, MathContext context) {
    return new Rational(
        this.numer.multiply(n.numer, context),
        this.denom.multiply(n.denom, context));
  }

  public Rational inverse() {
    return new Rational(this.denom, this.numer);
  }

  public Decimal toDecimal(MathContext context) {
    return this.numer.divide(this.denom, context);
  }

  @Override
  public String toString() {
    return this.numer.toString() + " / " + this.denom.toString();
  }


  private Decimal parse(String r) {
    switch (r) {
      case "e":
        return DecimalConstants.E;
      case "pi":
        return DecimalConstants.PI;
      default:
        return new Decimal(r);
    }
  }
}
