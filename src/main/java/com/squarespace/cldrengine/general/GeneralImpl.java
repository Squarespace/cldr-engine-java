package com.squarespace.cldrengine.general;

import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.CharacterOrderType;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DisplayNameOptions;
import com.squarespace.cldrengine.api.General;
import com.squarespace.cldrengine.api.LanguageIdType;
import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.api.LineOrderType;
import com.squarespace.cldrengine.api.ListPatternType;
import com.squarespace.cldrengine.api.MeasurementCategory;
import com.squarespace.cldrengine.api.MeasurementSystem;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.RegionIdType;
import com.squarespace.cldrengine.api.ScriptIdType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.NamesSchema;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.internal.Vector2Arrow;
import com.squarespace.cldrengine.locale.LanguageTagParser;
import com.squarespace.cldrengine.utils.StringUtils;

/**
 * Top-level namespace to expose info about the current locale and bundle,
 * and attach helper methods for dealing with locales.
 */
public class GeneralImpl implements General {

  private static final DisplayNameOptions DEFAULT_NAME_OPTIONS = DisplayNameOptions.build()
      .context(ContextType.BEGIN_SENTENCE);

  private final Bundle bundle;
  private final CLocale locale;
  private final GeneralInternals general;
  private final NamesSchema names;
  private final Map<ContextTransformFieldType, String> transform;

  public GeneralImpl(Bundle bundle, CLocale locale, Internals internals, PrivateApi privateApi) {
    this.bundle = bundle;
    this.locale = locale;
    this.general = internals.general;
    this.names = internals.schema.Names;
    this.transform = privateApi.getContextTransformInfo();
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

  public String formatList(List<String> items, ListPatternType type) {
    return this.general.formatList(bundle, items, type);
  }

  public List<Part> formatListToParts(List<String> items, ListPatternType type) {
    return this.general.formatListToParts(bundle, items, type);
  }

  public String getLanguageDisplayName(String code, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options)
        .mergeIf(DEFAULT_NAME_OPTIONS);
    String s = this.names.languages.displayName.get(bundle, LanguageIdType.fromString(code));
    return this.general.contextTransform(s, this.transform, options.context.get(),
        ContextTransformFieldType.LANGUAGES);
  }

  public String getScriptDisplayName(String code, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options)
        .mergeIf(DEFAULT_NAME_OPTIONS);
    String s = this.names.scripts.displayName.get(bundle, ScriptIdType.fromString(code));
    return this.general.contextTransform(s, this.transform, options.context.get(),
        ContextTransformFieldType.SCRIPT);
  }

  public String getRegionDisplayName(String code, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options)
        .mergeIf(DEFAULT_NAME_OPTIONS);
    Vector2Arrow<AltType, RegionIdType> impl = this.names.regions.displayName;
    RegionIdType region = RegionIdType.fromString(code);
    String name = impl.get(bundle, options.type.or(AltType.NONE), region);
    if (StringUtils.isEmpty(name)) {
      name = impl.get(bundle, AltType.NONE, region);
    }
    return name;
  }
}
