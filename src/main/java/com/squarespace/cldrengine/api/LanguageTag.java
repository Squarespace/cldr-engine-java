package com.squarespace.cldrengine.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * IETF BCP 47 language tag with static methods for parsing, adding likely
 * subtags, etc.
 */
public class LanguageTag {

  private static final int LANGUAGE = 0;
  private static final int SCRIPT = 1;
  private static final int REGION = 2;
  private static final int VARIANT = 3;

  private static final char SEP = '-';

  private static final String[] UNDEFINED_VALUES = new String[] {
    "und",
    "Zzzz",
    "ZZ",
    ""
  };

  private static final int[] KEYS = new int[] {
      LANGUAGE, SCRIPT, REGION, VARIANT
  };

  private static final List<Function<String, String>> TRANSFORMS = Arrays.asList(
    s -> s.toLowerCase(),
    s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase(),
    s -> s.toUpperCase(),
    s -> s.toLowerCase()
  );

  /**
   * Ensure the given field is in canonical form.
   */
  private static String canonicalize(int field, String value) {
    if (field == LANGUAGE && "root".equals(value)) {
      value = null;
    } else if (UNDEFINED_VALUES[field].equals(value)) {
      value = null;
    }
    if (value != null && value.length() > 0) {
      return TRANSFORMS.get(field).apply(value);
    }
    return null;
  }

  protected String[] core;
  protected Map<String, List<String>> extensions;
  protected String privateUse;

  private String _compact;
  private String _expanded;

  public LanguageTag() {
    this(null, null, null, null, null, null);
  }

  public LanguageTag(String language, String script, String region) {
    this(language, script, region, null, null, null);
  }

  public LanguageTag(
      String language,
      String script,
      String region,
      String variant,
      Map<String, List<String>> extensions,
      String privateUse) {

    this.core = new String[] {
      canonicalize(LANGUAGE, language),
      canonicalize(SCRIPT, script),
      canonicalize(REGION, region),
      canonicalize(VARIANT, variant)
    };
    this.extensions = extensions;
    this.privateUse = privateUse;
  }

  /**
   * Language subtag.
   */
  public String language() {
    return _field(LANGUAGE);
  }

  /**
   * Returns true if the language subtag is defined.
   */
  public boolean hasLanguage() {
    return this.core[LANGUAGE] != null;
  }

  /**
   * Script subtag.
   */
  public String script() {
    return _field(SCRIPT);
  }

  /**
   * Returns true if the script subtag is defined.
   */
  public boolean hasScript() {
    return this.core[SCRIPT] != null;
  }

  /**
   * Region subtag
   */
  public String region() {
    return _field(REGION);
  }

  /**
   * Returns true if the region subtag is defined.
   */
  public boolean hasRegion() {
    return this.core[REGION] != null;
  }

  /**
   * Variant subtag.
   */
  public String variant() {
    return _field(VARIANT);
  }

  /**
   * Return this language tag's extensions map.
   */
  public Map<String, List<String>> extensions() {
    return this.extensions == null ?
        Collections.emptyMap() : Collections.unmodifiableMap(this.extensions);
  }

  /**
   * Return the extensions of the given type. Use "u" for Unicode
   * and "t" for Transforms.
   */
  public List<String> extensionSubtags(String key) {
    List<String> subtag = this.extensions().get(key);
    return subtag == null ?
        Collections.emptyList() : Collections.unmodifiableList(subtag);
  }

  /**
   * Private use subtag.
   */
  public String privateUse() {
    return this.privateUse == null ? "" : this.privateUse;
  }

  /**
   * Return a compact string representation of the language tag. Any undefined
   * fields will be omitted.
   */
  public String compact() {
    if (this._compact == null) {
      // memoize to save some computation
      this._compact = this.render(false);
    }
    return this._compact;
  }

  /**
   * Return an expanded string representation of the language tag. Any undefined
   * fields will emit their undefined value.
   */
  public String expanded() {
    if (this._expanded == null) {
      // memoize to save some computation
      this._expanded = this.render(true);
    }
    return this._expanded;
  }

  /**
   * Return a compact string representation of the language tag. Any undefined
   * fields will be omitted.
   */
  @Override
  public String toString() {
    return this.compact();
  }

  private String _field(int field) {
    String v = this.core[field];
    return v == null ? UNDEFINED_VALUES[field] : v;
  }

  /**
   * Render a tag in compact or expanded form.
   */
  private String render(boolean expanded) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < KEYS.length; i++) {
      int key = KEYS[i];
      boolean force = key != VARIANT && (key == LANGUAGE || expanded);
      String val = this.core[key];
      if (val != null || force) {
        if (buf.length() > 0) {
          buf.append(SEP);
        }
        buf.append(val == null ? UNDEFINED_VALUES[key] : val);
      }
    }
    Map<String, List<String>> ex = this.extensions();
    if (!ex.isEmpty()) {
      List<String> keys = new ArrayList<>(ex.keySet());
      Collections.sort(keys);
      for (String key : keys) {
        List<String> vals = ex.get(key);
        if (vals != null && !vals.isEmpty()) {
          buf.append(SEP).append(key);
          for (String v : vals) {
            buf.append(SEP).append(v);
          }
        }
      }
    }
    if (this.privateUse != null && this.privateUse.length() > 0) {
      buf.append(SEP).append(this.privateUse);
    }
    return buf.toString();
  }

}

