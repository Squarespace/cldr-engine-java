package com.squarespace.cldrengine.numbers;

import java.util.List;

import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.decimal.DecimalFormatter;
import com.squarespace.cldrengine.decimal.PartsDecimalFormatter;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.PartsValue;

public class PartsNumberFormatter extends NumberFormatter<List<Part>> {

  public PartsNumberFormatter(NumberParams params) {
    super(params);
  }

  public AbstractValue<List<Part>> value() {
    return new PartsValue();
  }

  public DecimalFormatter<List<Part>> formatter(String decimal, String group) {
    return new PartsDecimalFormatter(decimal, group);
  }
}
