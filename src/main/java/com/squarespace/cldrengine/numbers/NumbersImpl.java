package com.squarespace.cldrengine.numbers;

import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.CurrencyDisplayNameOptions;
import com.squarespace.cldrengine.api.CurrencyFormatOptions;
import com.squarespace.cldrengine.api.CurrencyFractions;
import com.squarespace.cldrengine.api.CurrencySymbolWidthType;
import com.squarespace.cldrengine.api.CurrencyType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalAdjustOptions;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.NumberSymbolType;
import com.squarespace.cldrengine.api.Numbers;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.general.GeneralInternals;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.utils.Pair;

public class NumbersImpl implements Numbers {

  private static final DecimalFormatOptions FORCE_ERRORS = DecimalFormatOptions.build()
      .errors("nan infinity");

  private static final CurrencyDisplayNameOptions DEFAULT_CURRENCY_OPTIONS =
      CurrencyDisplayNameOptions.build().context(ContextType.BEGIN_SENTENCE);

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

  public String formatDecimal(Decimal n, DecimalFormatOptions options) {
    options = (options == null ? DecimalFormatOptions.build() : options);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), null);
    NumberRenderer<String> renderer = this.numbers.stringRenderer(params);
    return this.formatDecimalImpl(renderer, params, n, options);
  }

  public List<Part> formatDecimalToParts(Decimal n, DecimalFormatOptions options) {
    options = (options == null ? DecimalFormatOptions.build() : options);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), null);
    NumberRenderer<List<Part>> renderer = this.numbers.partsRenderer(params);
    return this.formatDecimalImpl(renderer, params, n, options);
  }

  public String formatCurrency(Decimal n, CurrencyType code, CurrencyFormatOptions options) {
    options = (options == null ? CurrencyFormatOptions.build() : options);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), "finance");
    NumberRenderer<String> renderer = this.numbers.stringRenderer(params);
    return this.formatCurrencyImpl(renderer, params, n, code, options);
  }

  public List<Part> formatCurrencyToParts(Decimal n, CurrencyType code, CurrencyFormatOptions options) {
    options = (options == null ? CurrencyFormatOptions.build() : options);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), "finance");
    NumberRenderer<List<Part>> renderer = this.numbers.partsRenderer(params);
    return this.formatCurrencyImpl(renderer, params, n, code, options);
  }

  public String getCurrencySymbol(CurrencyType code, CurrencySymbolWidthType width) {
    return this.numbers.getCurrencySymbol(bundle, code, width);
  }

  public String getCurrencyDisplayName(CurrencyType code, CurrencyDisplayNameOptions options) {
    options = (options == null ? CurrencyDisplayNameOptions.build() : options);
    ContextType context = options.context.or(ContextType.BEGIN_SENTENCE);
    String name = this.numbers.getCurrencyDisplayName(this.bundle, code);
    Map<ContextTransformFieldType, String> transform = this.privateApi.getContextTransformInfo();
    return this.general.contextTransform(name, transform, context, ContextTransformFieldType.CURRENCYNAME);
  }

  public String getCurrencyPluralName(Decimal n, CurrencyType code, CurrencyDisplayNameOptions options) {
    options = (options == null ? CurrencyDisplayNameOptions.build() : options);
    ContextType context = options.context.or(ContextType.BEGIN_SENTENCE);
    // TODO: support adjustment options here
    PluralType plural = this.getPluralCardinal(n, null);
    String name = this.numbers.getCurrencyPluralName(bundle, code, plural);
    Map<ContextTransformFieldType, String> transform = this.privateApi.getContextTransformInfo();
    return this.general.contextTransform(name, transform, context, ContextTransformFieldType.CURRENCYNAME);
  }

  public CurrencyFractions getCurrencyFractions(CurrencyType code) {
    return this.numbers.getCurrencyFractions(code);
  }

  public CurrencyType getCurrencyForRegion(String region) {
    return this.numbers.getCurrencyForRegion(region);
  }

  public PluralType getPluralCardinal(Decimal n, DecimalAdjustOptions options) {
    if (options != null) {
      n = this.adjustDecimal(n, options);
    }
    return this.bundle.plurals().cardinal(n);
  }

  public PluralType getPluralOrdinal(Decimal n, DecimalAdjustOptions options) {
    if (options != null) {
      n = this.adjustDecimal(n, options);
    }
    return this.bundle.plurals().ordinal(n);
  }

  protected <T> T formatDecimalImpl(NumberRenderer<T> renderer, NumberParams params,
      Decimal n, DecimalFormatOptions options) {

    // A NaN or Infinity value will just return the locale's representation
    T v = validate(n, options, renderer, params);
    if (v != null) {
      return v;
    }
    Pair<T, PluralType> result = this.numbers.formatDecimal(bundle, renderer, n, options, params);
    return result._1;
  }

  protected <T> T formatCurrencyImpl(NumberRenderer<T> renderer, NumberParams params,
      Decimal n, CurrencyType code, CurrencyFormatOptions options) {
    validate(n, FORCE_ERRORS, renderer, params);
    return this.numbers.formatCurrency(this.bundle, renderer, n, code, options, params);
  }

  protected <T> T validate(Decimal n, DecimalFormatOptions options, NumberRenderer<T> renderer,
      NumberParams params) {

    String[] errors = null;
    if (options.errors.ok()) {
      errors = options.errors.get().split("[\\s,]+");
    }

    // Check if we have NaN or Infinity
    boolean isnan = n.isNaN();
    boolean isinfinity = n.isInfinity();

    if (errors != null) {
      // Check if we should throw an error on either of these
      if (isnan && arrayContains(errors, "nan")) {
        throw new RuntimeException("Invalid argument: NaN");
      }
      if (isinfinity && arrayContains(errors, "infinity")) {
        throw new RuntimeException("Invalid argument: Infinity");
      }
    }
    if (isnan) {
      return renderer.make("nan", params.symbols.get(NumberSymbolType.NAN));
    } else if (isinfinity) {
      return renderer.make("infinity", params.symbols.get(NumberSymbolType.INFINITY));
    }
    return null;

  }

  protected static <T> boolean arrayContains(T[] arr, T t) {
    for (T elem : arr) {
      if (elem.equals(t)) {
        return true;
      }
    }
    return false;
  }
}
