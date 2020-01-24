package com.squarespace.cldrengine.units;

import java.util.Arrays;
import java.util.List;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.Quantity;
import com.squarespace.cldrengine.api.UnitFormatOptions;
import com.squarespace.cldrengine.api.UnitFormatStyleType;
import com.squarespace.cldrengine.api.UnitLength;
import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.api.Units;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.Meta;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.numbers.NumberRenderer;

public class UnitsImpl implements Units {

  private static final UnitFormatOptions DEFAULT_OPTIONS = UnitFormatOptions.build()
      .length(UnitLength.LONG)
      .style(UnitFormatStyleType.DECIMAL);

  private final Bundle bundle;
  private final Internals internal;
  private final PrivateApi privateApi;

  public UnitsImpl(Bundle bundle, Internals internal, PrivateApi privateApi) {
    this.bundle = bundle;
    this.internal = internal;
    this.privateApi = privateApi;
  }

  @Override
  public List<UnitType> availableUnits() {
    return Arrays.asList(Meta.KEY_UNIT_ID.keys());
  }

  @Override
  public String getUnitDisplayName(UnitType unit, UnitLength length) {
    if (length == null) {
      length = UnitLength.LONG;
    }
    return this.internal.units.getDisplayName(bundle, unit, length);
  }

  @Override
  public String formatQuantity(Quantity qty, UnitFormatOptions options) {
    options = (options == null ? DEFAULT_OPTIONS : options).merge(DEFAULT_OPTIONS);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), null);
    NumberRenderer<String> renderer = this.internal.numbers.stringRenderer(params);
    return this.internal.units.format(bundle, renderer, qty, options, params);
  }

  @Override
  public List<Part> formatQuantityToParts(Quantity qty, UnitFormatOptions options) {
    options = (options == null ? DEFAULT_OPTIONS : options).merge(DEFAULT_OPTIONS);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), null);
    NumberRenderer<List<Part>> renderer = this.internal.numbers.partsRenderer(params);
    return this.internal.units.format(bundle, renderer, qty, options, params);
  }
}
