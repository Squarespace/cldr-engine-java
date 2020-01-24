package com.squarespace.cldrengine.general;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.CharacterOrderType;
import com.squarespace.cldrengine.api.General;
import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.api.LineOrderType;
import com.squarespace.cldrengine.api.MeasurementCategory;
import com.squarespace.cldrengine.api.MeasurementSystem;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.locale.LanguageTagParser;

/**
 * Top-level namespace to expose info about the current locale and bundle,
 * and attach helper methods for dealing with locales.
 */
public class GeneralImpl implements General {

  private final Bundle bundle;
  private final CLocale locale;
  private final GeneralInternals general;

  public GeneralImpl(Bundle bundle, CLocale locale, Internals internals) {
    this.bundle = bundle;
    this.locale = locale;
    this.general = internals.general;
  }

  public CharacterOrderType characterOrder() {
    return this.general.characterOrder(bundle);
  }

  public LineOrderType lineOrder() {
    return this.general.lineOrder(bundle);
  }

  public Bundle bundle() {
    return this.bundle;
  }

  public CLocale locale() {
    return this.locale;
  }

  public CLocale resolveLocale(String tag) {
    return CLDR.resolveLocale(tag);
  }

  public LanguageTag parseLanguageTag(String tag) {
    return LanguageTagParser.parse(tag);
  }

  public MeasurementSystem measurementSystem() {
    return measurementSystem(null);
  }

  public MeasurementSystem measurementSystem(MeasurementCategory category) {
    String region = bundle.region();
    if (category != null) {
      switch (category) {
        case TEMPERATURE:
          switch (region) {
            case "BS":
            case "BZ":
            case "PR":
            case "PW":
              return MeasurementSystem.US;
            default:
              return MeasurementSystem.METRIC;
          }
      }
    }
    switch (region) {
      case "GB":
        return MeasurementSystem.UK;
      case "LR":
      case "MM":
      case "US":
        return MeasurementSystem.US;
      default:
        return MeasurementSystem.METRIC;
    }
  }
}
