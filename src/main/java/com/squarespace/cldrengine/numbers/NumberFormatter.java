package com.squarespace.cldrengine.numbers;


import static com.squarespace.cldrengine.utils.StringUtils.firstChar;
import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;
import static com.squarespace.cldrengine.utils.StringUtils.lastChar;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.squarespace.cldrengine.api.CurrencySpacingPattern;
import com.squarespace.cldrengine.api.CurrencySpacingPos;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.NumberSymbolType;
import com.squarespace.cldrengine.decimal.DecimalFormatter;
import com.squarespace.cldrengine.general.GeneralInternals;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.parsing.NumberPattern;
import com.squarespace.cldrengine.parsing.WrapperPattern;

public abstract class NumberFormatter<R> implements NumberRenderer<R> {

  private final NumberParams params;

  public NumberFormatter(NumberParams params) {
    this.params = params;
  }

  public abstract AbstractValue<R> value();

  public abstract DecimalFormatter<R> formatter(String decimal, String group);

  public R render(Decimal n, NumberPattern pattern, String currencySymbol,
      String percentSymbol, String decimalSymbol, int minInt, boolean grouping,
      Integer exponent) {

    Map<NumberSymbolType, String> symbols = params.symbols;
    boolean currency = !isEmpty(currencySymbol);
    String decimal = "";
    String currencyDecimal = symbols.get(NumberSymbolType.CURRENCYDECIMAL);
    if (!isEmpty(decimalSymbol)) {
      decimal = decimalSymbol;
    } else if (currency && !isEmpty(currencyDecimal)) {
      decimal = currencyDecimal;
    } else {
      decimal = symbols.get(NumberSymbolType.DECIMAL);
    }

    String group = "";
    String currencyGroup = symbols.get(NumberSymbolType.CURRENCYGROUP);
    if (grouping) {
      group = symbols.get(NumberSymbolType.GROUP);
      if (!isEmpty(currencyGroup)) {
        group = currencyGroup;
      }
    }

    int priGroup = pattern.priGroup;
    int secGroup = pattern.secGroup;
    if (priGroup <= 0) {
      priGroup = this.params.primaryGroupingSize;
    }
    if (secGroup <= 0) {
      secGroup = this.params.secondaryGroupingSize;
    }

    DecimalFormatter<R> formatter = this.formatter(decimal, group);
    n.format(
        formatter,
        decimal,
        group,
        minInt,
        this.params.minimumGroupingDigits,
        priGroup,
        secGroup,
        true, // zeroScale
        this.params.digits);

    R formatted = formatter.render();
    AbstractValue<R> res = this.value();

    boolean haveNumber = false;
    boolean currencyBefore = false;
    int currencyIdx = -1;
    for (Object node : pattern.nodes) {
      if (node instanceof String) {
        res.add("literal", (String) node);
      } else {
        NumberPattern.Field field = (NumberPattern.Field) node;
        switch (field) {
          case CURRENCY: {
            // Save the offset to the segment before or after the currency symbol
            currencyBefore = !haveNumber;
            int i = res.length();
            res.add("currency", currencySymbol);
            int j = res.length();
            currencyIdx = currencyBefore ? j : i - 1;
            break;
          }

          case MINUS:
            res.add("minus", symbols.get(NumberSymbolType.MINUSSIGN));
            break;

          case PLUS:
            res.add("plus", symbols.get(NumberSymbolType.PLUSSIGN));
            break;

          case NUMBER:
            res.append(formatted);
            haveNumber = true;
            break;

          case PERCENT:
            res.add("percent", percentSymbol);
            break;

          case EXPONENT:
            // Don't emit the exponent if undefined or zero
            if (exponent != null) {
              res.add("exponent", symbols.get(NumberSymbolType.EXPONENTIAL));
              if (exponent < 0) {
                res.add("minus", symbols.get(NumberSymbolType.MINUSSIGN));
              } else {
                res.add("plus", symbols.get(NumberSymbolType.PLUSSIGN));
              }
              String exp = DecimalNumberingSystem.fastFormatDecimal(exponent.toString(), this.params.digits, 1);
              res.add("integer", exp);
              break;
            }
        }

      }
    }

    // Adjust spacing between currency symbol based on surrounding characters.
    if (currencyIdx != -1) {
      Map<CurrencySpacingPos, Map<CurrencySpacingPattern, String>> spacing = this.params.currencySpacing;
      String curr = res.get(currencyIdx);
      if (currencyBefore) {
        Map<CurrencySpacingPattern, String> pos = spacing.get(CurrencySpacingPos.AFTER);
        if (insertBetween(pos, lastChar(currencySymbol), firstChar(curr))) {
          res.insert(currencyIdx, "spacer", pos.get(CurrencySpacingPattern.INSERTBETWEEN));
        }
      } else {
        Map<CurrencySpacingPattern, String> pos = spacing.get(CurrencySpacingPos.BEFORE);
        if (insertBetween(pos, firstChar(currencySymbol), lastChar(curr))) {
          res.insert(currencyIdx + 1, "spacer", pos.get(CurrencySpacingPattern.INSERTBETWEEN));
        }
      }
    }

    return res.render();
  }

  private static final Pattern RE_SYMBOL = Pattern.compile("^[\\p{Sm}\\p{Sc}\\p{Sk}\\p{So}]");
  private static final Pattern RE_DIGIT = Pattern.compile("^[\\p{Nd}]");

  protected boolean insertBetween(Map<CurrencySpacingPattern, String> spacing, String currency, String surrounding) {
    String currencyMatch = spacing.get(CurrencySpacingPattern.CURRENCYMATCH);
    String surroundingMatch = spacing.get(CurrencySpacingPattern.SURROUNDINGMATCH);
    return testMatch(currencyMatch, currency) && testMatch(surroundingMatch, surrounding);
  }

  protected boolean testMatch(String spacing, String value) {
    switch (spacing) {
      case "[:digit:]":
        return RE_DIGIT.matcher(value).matches();
      case "[:^S:]":
        return !RE_SYMBOL.matcher(value).matches();
    }
    return false;
  }


  public R empty() {
    return this.value().render();
  }

  public R make(String type, String value) {
    AbstractValue<R> v = this.value();
    v.add(type, value);
    return v.render();
  }

  public R wrap(GeneralInternals internal, String raw, List<R> args) {
    AbstractValue<R> res = this.value();
    WrapperPattern pattern = internal.parseWrapper(raw);
    for (Object node : pattern.nodes) {
      if (node instanceof String) {
        res.add("literal", (String)node);
      } else {
        int n = (int)node;
        if (n < args.size()) {
          R v = args.get(n);
          if (v != null) {
            res.append(v);
          }
        }
      }
    }
    return res.render();
  }

}
