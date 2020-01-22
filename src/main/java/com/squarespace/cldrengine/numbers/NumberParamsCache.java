package com.squarespace.cldrengine.numbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CurrencySpacingPattern;
import com.squarespace.cldrengine.api.CurrencySpacingPos;
import com.squarespace.cldrengine.api.NumberSymbolType;
import com.squarespace.cldrengine.api.NumberSystemCategory;
import com.squarespace.cldrengine.api.NumberSystemName;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.NumberExternalData;
import com.squarespace.cldrengine.internal.NumberSystemInfo;
import com.squarespace.cldrengine.internal.NumbersSchema;
import com.squarespace.cldrengine.parsing.NumberPattern;
import com.squarespace.cldrengine.utils.Cache;

public class NumberParamsCache {

  private static final Map<String, String[]> DECIMAL_NUMBERING_DIGITS = new HashMap<>();

  static {
    JsonObject root = JsonParser.parseString(NumberExternalData.DECIMALNUMBERINGDIGITS).getAsJsonObject();
    for (String name : root.keySet()) {
      List<String> digits = new ArrayList<>();
      JsonArray raw = root.getAsJsonArray(name);
      if (raw.size() == 10) {
        for (int i = 0; i < raw.size(); i++) {
          digits.add(raw.get(i).getAsString());
        }
      } else {
        String base = raw.get(0).getAsString();
        digits.add(base);
        int code = base.codePointAt(0);
        for (int i = 1; i < 10; i++) {
          StringBuilder buf = new StringBuilder();
          buf.appendCodePoint(code + i);
          digits.add(buf.toString());
        }
      }
      DECIMAL_NUMBERING_DIGITS.put(name, digits.stream().toArray(String[]::new));
    }
  }

  private final Bundle bundle;
  private final Internals internals;
  private final NumbersSchema numbers;
  private NumberingSystem latnSystem;
  private NumberSystemInfo latnSystemInfo;

  private Cache<NumberParams> storage;

  public NumberParamsCache(Bundle bundle, Internals internals) {
    this.bundle = bundle;
    this.internals = internals;
    this.numbers = internals.schema.Numbers;
    this.latnSystemInfo = this.numbers.numberSystem.get("latn");
    this.latnSystem = this.buildNumberSystem("latn");
    this.storage = new Cache<>(this::build, 20);
  }

  public NumberParams getNumberParams(String numberSystem, String defaultSystem) {
    // Default numbering system for a locale unless explicitly overridden
    // https://www.unicode.org/reports/tr35/tr35-33/tr35-numbers.html#defaultNumberingSystem
    if (defaultSystem == null) {
      defaultSystem = "default";
    }
    if (numberSystem == null) {
      numberSystem = bundle.numberSystem();
      if (numberSystem == null) {
        numberSystem = defaultSystem;
      }
    }

    NumberSystemName realName = this.select(numberSystem);

    // Handle invalid number systems by returning the specified default
    // TODO: include algorithmic number system check
    if (!DECIMAL_NUMBERING_DIGITS.containsKey(realName.value())) {
      realName = this.select(defaultSystem);

      // TODO: temporary double-check to default for zh finance until we
      // have rbnf implemented.
      if (!DECIMAL_NUMBERING_DIGITS.containsKey(realName.value())) {
        realName = this.select("default"); // always exists
      }
    }
    return this.storage.get(realName.value());
  }

  protected NumberSystemName select(String numberSystem) {
    switch (numberSystem) {
      case "default":
      case "native":
      case "finance":
      case "traditional":
        String raw = this.numbers.numberSystems.get(bundle, NumberSystemCategory.fromString(numberSystem));
        return NumberSystemName.fromString(raw);
      default:
        return NumberSystemName.fromString(numberSystem);
    }
  }

  protected NumberParams build(String name) {
    NumberingSystem system = name.equals("latn") ? this.latnSystem : this.buildNumberSystem(name);
    NumberSystemInfo info = this.numbers.numberSystem.get(name);
    if (info == null) {
      info = this.latnSystemInfo;
    }
    Map<CurrencySpacingPos, Map<CurrencySpacingPattern, String>> currencySpacing =
        info.currencyFormats.spacing.exists(bundle)
            ? info.currencyFormats.spacing.mapping(bundle)
            : latnSystemInfo.currencyFormats.spacing.mapping(bundle);

    return new NumberParams(
        NumberSystemName.fromString(name),
        system,
        latnSystem,
        DECIMAL_NUMBERING_DIGITS.get(name),
        DECIMAL_NUMBERING_DIGITS.get("latn"),
        system.symbols,
        system.minimumGroupingDigits,
        system.primaryGroupingSize,
        system.secondaryGroupingSize,
        currencySpacing);
  }


  protected NumberingSystem buildNumberSystem(String name) {
    Map<String, NumberSystemInfo> system = this.numbers.numberSystem;
    NumberSystemInfo info = system.get(name);
    if (info == null) {
      info = this.latnSystemInfo;
    }
    Map<NumberSymbolType, String> symbols = info.symbols.exists(bundle)
        ? info.symbols.mapping(bundle)
        : latnSystemInfo.symbols.mapping(bundle);
    String standardRaw = info.decimalFormats.standard.get(bundle);
    if (standardRaw == null) {
      latnSystemInfo.decimalFormats.standard.get(bundle);
    }
    NumberPattern standard = this.internals.numbers.getNumberPattern(standardRaw, false);
    int minimumGroupingDigits = Integer.parseInt(this.numbers.minimumGroupingDigits.get(bundle), 10);
    return new DecimalNumberingSystem(
        name,
        DECIMAL_NUMBERING_DIGITS.get(name),
        symbols,
        minimumGroupingDigits,
        standard.priGroup,
        standard.secGroup);
  };
}
