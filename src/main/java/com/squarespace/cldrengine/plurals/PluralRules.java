package com.squarespace.cldrengine.plurals;

import java.util.List;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.PluralType;

import lombok.AllArgsConstructor;

/**
 * Set of all cardinal and ordinal plural rules, and the array of expression fragments the rules reference.
 */
@AllArgsConstructor
public class PluralRules {

  // Notation for categories in compact plural rules
  private static final PluralType[] CATEGORIES = new PluralType[] {
      PluralType.ZERO,
      PluralType.ONE,
      PluralType.TWO,
      PluralType.FEW,
      PluralType.MANY,
      PluralType.OTHER
  };

  private final Rule[] cardinals;
  private final Rule[] ordinals;

  public NumberOperands operands(Decimal d) {
    return new NumberOperands(d);
  }

  public PluralType cardinal(Decimal n) {
    return this.evaluate(new NumberOperands(n), this.cardinals);
  }

  public PluralType ordinal(Decimal n) {
    return this.evaluate(new NumberOperands(n), this.ordinals);
  }

  private PluralType evaluate(NumberOperands operands, Rule[] rules) {
    for (Rule rule : rules) {
      if (this.execute(operands, rule.indices)) {
        return CATEGORIES[rule.index];
      }
    }
    return PluralType.OTHER;
  }

  private boolean execute(NumberOperands operands, int[][] conditions) {
    // Evaluate each condition and OR them together.
    int len = conditions.length;
    for (int i = 0; i < len; i++) {
      int[] cond = conditions[i];

      // Evaluate the inner expressions and AND them together.
      boolean res = true;
      for (int j = 0; j < cond.length; j++) {
        Expr expr = PluralData.EXPRESSIONS.get(cond[j]);
        res = res && this.evaluateExpr(operands, expr);
        if (!res) {
          break;
        }
      }
      if (res) {
        return true;
      }
    }
    return false;
  }

  private boolean evaluateExpr(NumberOperands operands, Expr expr) {
    long n = operands.get(expr.operand);

    // The N = X..Y syntax means N matches an integer from X to Y inclusive
    // Operand 'n' must always be compared as an integer, so if it has any non-zero decimal
    // parts we must set integer = false.
    boolean integer = expr.operand == 'n' ? operands.w == 0 : true;

    if (expr.mod > 0) {
      n = n % expr.mod;
    }

    List<Object> rangelist = expr.rangelist;
    int len = rangelist.size();
    boolean res = false;
    for (int i = 0; i < len; i++) {
      Object elem = rangelist.get(i);
      if (elem instanceof Integer) {
        int v = (int) elem;
        res = res || (integer && n == v);
      } else {
        int[] range = (int[]) elem;
        res = res || (integer && range[0] <= n && n <= range[1]);
      }
    }
    return expr.relop == 1 ? res : !res;
  }

}
