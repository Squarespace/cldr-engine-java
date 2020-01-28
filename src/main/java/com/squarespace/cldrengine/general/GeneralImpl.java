package com.squarespace.cldrengine.general;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    options = (options == null ? DEFAULT_NAME_OPTIONS : options).mergeIf(DEFAULT_NAME_OPTIONS);
    AltType type = options.type.or(AltType.NONE);
    LanguageIdType id = LanguageIdType.fromString(code);
    Vector2Arrow<AltType, LanguageIdType> arrow = this.names.languages.displayName;
    String s = "";
    if (id != null) {
      s = this.getVectorAlt(arrow, id, type);
    }
    if (isEmpty(s)) {
      LanguageTag tag = parseLanguageTag(code);
      s = this._getLanguageDisplayName(arrow, tag, type);
    }
    return this.general.contextTransform(s, this.transform, options.context.get(),
        ContextTransformFieldType.LANGUAGES);
  }

  public String getLanguageDisplayName(LanguageTag tag, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options).mergeIf(DEFAULT_NAME_OPTIONS);
    AltType type = options.type.or(AltType.NONE);
    Vector2Arrow<AltType, LanguageIdType> arrow = this.names.languages.displayName;
    String s = this._getLanguageDisplayName(arrow, tag, type);
    return this.general.contextTransform(s, this.transform, options.context.get(),
        ContextTransformFieldType.LANGUAGES);
  }

  protected String _getLanguageDisplayName(Vector2Arrow<AltType, LanguageIdType> arrow, LanguageTag tag, AltType type) {
    String s = "";

    // Try language + region
    if (tag.hasLanguage() && tag.hasRegion()) {
      LanguageIdType id = _languageRegion(tag);
      if (id != null) {
        s = this.getVectorAlt(arrow, id, type);
      }
    }

    // Try language + script
    if (isEmpty(s) && tag.hasLanguage() && tag.hasScript()) {
      LanguageIdType id = _languageScript(tag);
      if (id != null) {
        s = this.getVectorAlt(arrow, id, type);
      }
    }

    // Try language if script and region are empty
    if (isEmpty(s) && !tag.hasScript() && !tag.hasRegion()) {
      LanguageIdType id = LanguageIdType.fromString(tag.language());
      if (id != null) {
        s = this.getVectorAlt(arrow, id, type);
      }
    }

    // Resolve to fill in unknown subtags, then attempt combinations
    if (isEmpty(s)) {
      CLocale locale = resolveLocale(tag.compact());
      tag = locale.tag();
      for (Function<LanguageTag, LanguageIdType> func : LANGUAGE_FUNCS) {
        LanguageIdType id = func.apply(tag);
        if (id != null) {
          s = this.getVectorAlt(arrow, id, type);
          if (!isEmpty(s)) {
            // Found one
            break;
          }
        }
      }
    }
    return s;
  }

  protected static LanguageIdType _languageRegion(LanguageTag tag) {
    String code = String.format("%s-%s", tag.language(), tag.region());
    return LanguageIdType.fromString(code);
  }

  protected static LanguageIdType _languageScript(LanguageTag tag) {
    String code = String.format("%s-%s", tag.language(), tag.script());
    return LanguageIdType.fromString(code);
  }

  protected static List<Function<LanguageTag, LanguageIdType>> LANGUAGE_FUNCS = Arrays.asList(
      GeneralImpl::_languageRegion,
      GeneralImpl::_languageScript,
      tag -> LanguageIdType.fromString(tag.language())
  );

  public String getScriptDisplayName(String code, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options).mergeIf(DEFAULT_NAME_OPTIONS);
    AltType type = options.type.or(AltType.NONE);
    Vector2Arrow<AltType, ScriptIdType> arrow = this.names.scripts.displayName;
    ScriptIdType id = ScriptIdType.fromString(code);
    String s = "";
    if (id != null) {
      s = this.getVectorAlt(arrow, id, type);
    }
    if (isEmpty(s)) {
      LanguageTag tag = parseLanguageTag(code);
      s = this._getScriptDisplayName(arrow, tag, type);
    }
    return this.general.contextTransform(s, this.transform, options.context.get(),
        ContextTransformFieldType.SCRIPT);
  }

  public String getScriptDisplayName(LanguageTag tag, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options).mergeIf(DEFAULT_NAME_OPTIONS);
    AltType type = options.type.or(AltType.NONE);
    Vector2Arrow<AltType, ScriptIdType> arrow = this.names.scripts.displayName;
    String s = this._getScriptDisplayName(arrow, tag, type);
    return this.general.contextTransform(s, this.transform, options.context.get(),
        ContextTransformFieldType.SCRIPT);
  }

  protected String _getScriptDisplayName(Vector2Arrow<AltType, ScriptIdType> arrow, LanguageTag tag, AltType type) {
    String s = "";

    // If language is blank or we have an explicit script subtag, use the
    // script subtag as-is. This will resolve "und-Zzzz" to "Unknown" but
    // "en-Zzzz" will fall through to resolve "Latin"
    if (!tag.hasLanguage() && tag.hasScript()) {
      ScriptIdType id = ScriptIdType.fromString(tag.script());
      if (id != null) {
        s = this.getVectorAlt(arrow, id, type);
      }
    }

    if (isEmpty(s)) {
      CLocale locale = resolveLocale(tag.compact());
      ScriptIdType id = ScriptIdType.fromString(locale.tag().script());
      if (id != null) {
        s = this.getVectorAlt(arrow, id, type);
      }
    }

    return s;
  }

  public String getRegionDisplayName(String code, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options).mergeIf(DEFAULT_NAME_OPTIONS);
    AltType type = options.type.or(AltType.NONE);
    Vector2Arrow<AltType, RegionIdType> arrow = this.names.regions.displayName;
    RegionIdType id = RegionIdType.fromString(code);
    String s = "";
    if (id != null) {
      s = this.getVectorAlt(arrow, id, type);
    }
    if (isEmpty(s)) {
      LanguageTag tag = parseLanguageTag(code);
      s = this._getRegionDisplayName(arrow, tag, type);
    }
    return s;
  }

  public String getRegionDisplayName(LanguageTag tag, DisplayNameOptions options) {
    options = (options == null ? DEFAULT_NAME_OPTIONS : options).mergeIf(DEFAULT_NAME_OPTIONS);
    AltType type = options.type.or(AltType.NONE);
    Vector2Arrow<AltType, RegionIdType> arrow = this.names.regions.displayName;
    return this._getRegionDisplayName(arrow, tag, type);
  }

  protected String _getRegionDisplayName(Vector2Arrow<AltType, RegionIdType> arrow, LanguageTag tag, AltType type) {
    String s = "";
    // If language is blank or we have an explicit region subtag, use
    // the region subtag as-is. This will resolve "und-ZZ" to "Unknown" but
    // "en-Zzzz" will fall through to resolve "United States"
    if (!tag.hasLanguage() || tag.hasRegion()) {
      RegionIdType id = RegionIdType.fromString(tag.region());
      if (id != null) {
        s = this.getVectorAlt(arrow, id, type);
      }
    }

    if (isEmpty(s)) {
      // Resolve to populate the region
      CLocale locale = resolveLocale(tag.compact());
      RegionIdType id = RegionIdType.fromString(locale.tag().region());
      if (id != null) {
        s = this.getVectorAlt(arrow, id, type);
      }
    }
    return s;
  }

  protected <T> String getVectorAlt(Vector2Arrow<AltType, T> arrow, T code, AltType type) {
    String s = arrow.get(this.bundle, type, code);
    return isEmpty(s) ? arrow.get(this.bundle, AltType.NONE, code) : s;
  }
}
