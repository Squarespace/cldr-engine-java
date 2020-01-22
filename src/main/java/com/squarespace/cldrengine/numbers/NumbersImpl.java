package com.squarespace.cldrengine.numbers;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalAdjustOptions;
import com.squarespace.cldrengine.api.Numbers;
import com.squarespace.cldrengine.general.GeneralInternals;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.PrivateApi;

public class NumbersImpl implements Numbers {

  public final Bundle bundle;
  public final NumberInternals numbers;
  public final GeneralInternals general;
  public final PrivateApi privateApi;

  public NumbersImpl(Bundle bundle, Internals internals, PrivateApi privateApi) {
    this.bundle = bundle;
    this.numbers = internals.numbers;
    this.general = internals.general;
    this.privateApi = privateApi;
  }

  public Decimal adjustDecimal(Decimal num, DecimalAdjustOptions options) {
    return this.numbers.adjustDecimal(num, options);
  }
}
