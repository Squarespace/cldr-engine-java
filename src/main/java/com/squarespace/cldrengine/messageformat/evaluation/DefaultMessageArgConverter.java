package com.squarespace.cldrengine.messageformat.evaluation;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.MessageArgConverter;
import com.squarespace.cldrengine.decimal.DecimalConstants;

/**
 * Convert arguments to the required values.
 */
public class DefaultMessageArgConverter implements MessageArgConverter {

  public String asString(Object arg) {
    if (arg == null) {
      return "";
    }
    if (arg instanceof String) {
      return (String)arg;
    }
    return arg.toString();
  }

  public Decimal asDecimal(Object arg) {
    if (arg == null) {
      return DecimalConstants.ZERO;
    }
    if (arg instanceof Decimal) {
      return (Decimal)arg;
    } else if (arg instanceof Boolean) {
      return ((boolean)arg) ? DecimalConstants.ONE : DecimalConstants.ZERO;
    } else if (arg instanceof Number) {
      return new Decimal(((Number)arg).toString());
    }
    return new Decimal(arg.toString());
  }

}
