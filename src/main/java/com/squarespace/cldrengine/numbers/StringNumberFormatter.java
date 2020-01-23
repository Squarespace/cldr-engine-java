package com.squarespace.cldrengine.numbers;

import com.squarespace.cldrengine.decimal.DecimalFormatter;
import com.squarespace.cldrengine.decimal.StringDecimalFormatter;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.StringValue;

public class StringNumberFormatter extends NumberFormatter<String> {

  public StringNumberFormatter(NumberParams params) {
    super(params);
  }

  public AbstractValue<String> value() {
    return new StringValue();
  }

  public DecimalFormatter<String> formatter(String _decimal, String group) {
    return new StringDecimalFormatter();
  }
}
