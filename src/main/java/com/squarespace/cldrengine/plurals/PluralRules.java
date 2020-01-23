package com.squarespace.cldrengine.plurals;

import java.util.List;

import com.squarespace.cldrengine.api.Decimal;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PluralRules {

  private final List<Expr> expressions;
  private final List<Rule> cardinals;
  private final List<Rule> ordinals;

  public NumberOperands operands(Decimal d) {
    return new NumberOperands(d);
  }


}
