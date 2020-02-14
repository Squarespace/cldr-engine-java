package com.squarespace.cldrengine.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.api.Pair;
import com.squarespace.cldrengine.internal.LocaleExternalData;

public class LocaleResolver {

  private static final LanguageTag UND = new LanguageTag();

  // Markers that let us quickly determine that a given FastTag field is undefined,
  // while still having the ability to call equals() and hashCode() on it.
  private static final Integer LANGUAGE = 0;
  private static final Integer SCRIPT = 1;
  private static final Integer REGION = 2;

  private static Map<FastTag, FastTag> LIKELY_SUBTAGS_MAP = loadLikelySubtags();
  private static Map<String, List<Pair<FastTag, FastTag>>> LANGUAGE_ALIAS_MAP = loadLanguageAliases();
  private static Map<String, List<String>> REGION_ALIAS_MAP = loadRegionAliases();

  // Field flags for match order
  private static int F_LANGUAGE = 1;
  private static int F_SCRIPT = 2;
  private static int F_REGION = 4;

  // Order to match subtags for "add likely subtags" process
  private static int[] MATCH_ORDER = new int[] {
      F_LANGUAGE | F_SCRIPT | F_REGION,
      F_LANGUAGE | F_REGION,
      F_LANGUAGE | F_SCRIPT,
      F_LANGUAGE,
      F_SCRIPT
  };

  /**
   * Substitute all relevant language and region aliases, and then add likely subtags.
   */
  public static LanguageTag resolve(String str) {
    LanguageTag tag = LanguageTagParser.parse(str);
    return resolve(tag);
  }

  /**
   * Substitute all relevant language and region aliases, and then add likely subtags.
   */
  public static LanguageTag resolve(LanguageTag tag) {
    FastTag fast = new FastTag(tag);
    substituteLanguageAliases(fast);
    substituteRegionAliases(fast);
    addLikelySubtags(fast);
    return returnTag(tag, fast);
  }

  /**
   * Add any missing subtags using the likely subtags mapping. For example,
   * this would convert "en" to "en-Latn-US".
   */
  public static LanguageTag addLikelySubtags(String str) {
    LanguageTag tag = LanguageTagParser.parse(str);
    return addLikelySubtags(tag);
  }

  /**
   * Add any missing subtags using the likely subtags mapping. For example,
   * this would convert "en" to "en-Latn-US".
   */
  public static LanguageTag addLikelySubtags(LanguageTag tag) {
    FastTag fast = new FastTag(tag);
    addLikelySubtags(fast);
    return returnTag(tag, fast);
  }

  /**
   * Remove any subtags that would be added by addLikelySubtags(). For example,
   * this would convert "en-Latn-US" to "en".
   */
  public static LanguageTag removeLikelySubtags(String str) {
    LanguageTag tag = LanguageTagParser.parse(str);
    return removeLikelySubtags(tag);
  }

  /**
   * Remove any subtags that would be added by addLikelySubtags(). For example,
   * this would convert "en-Latn-US" to "en".
   */
  public static LanguageTag removeLikelySubtags(LanguageTag tag) {
    FastTag max = new FastTag(tag);
    if (max.language == LANGUAGE || max.script == SCRIPT || max.region == REGION) {
      addLikelySubtags(max);
    }

    FastTag tmp = new FastTag(UND);

    // Using "en-Latn-US" as an example...
    // 1. Match "en-Zzzz-ZZ"
    tmp.language = max.language;
    FastTag match = new FastTag(tmp);
    addLikelySubtags(match);
    if (max.equals(match)) {
      return returnTag(tag, tmp);
    }

    // 2. Match "en-Zzzz-US"
    tmp.region = max.region;
    match = new FastTag(tmp);
    addLikelySubtags(match);
    if (max.equals(match)) {
      tmp.language = max.language;
      return returnTag(tag, tmp);
    }

    // 3. Match "en-Latn-ZZ"
    tmp.region = REGION;
    tmp.script = max.script;
    match = new FastTag(tmp);
    addLikelySubtags(match);
    if (max.equals(match)) {
      return returnTag(tag, tmp);
    }

    // 4. Nothing matched, so return a copy of the original tag.
    return returnTag(tag, max);
  }

  /**
   * Add any missing subtags using the likely subtags mapping. For example,
   * this would convert "en" to "en-Latn-US".
   */
  private static void addLikelySubtags(FastTag fast) {
    FastTag tmp = new FastTag(fast);
    for (int i = 0; i < MATCH_ORDER.length; i++) {
      int flags = MATCH_ORDER[i];
      tmp.setFields(fast, flags);
      FastTag match = LIKELY_SUBTAGS_MAP.get(tmp);
      if (match != null) {
        if (fast.language == LANGUAGE) {
          fast.language = match.language;
        }
        if (fast.script == SCRIPT) {
          fast.script = match.script;
        }
        if (fast.region == REGION) {
          fast.region = match.region;
        }
        break;
      }
    }
  }

  /**
   * Return a new language tag that combines the core fields of the fast tag,
   * with the variant, extensions, and private use field of the original.
   */
  private static LanguageTag returnTag(LanguageTag orig, FastTag fast) {
    return new LanguageTag(
      fast.language == LANGUAGE ? null : (String)fast.language,
      fast.script == SCRIPT ? null : (String)fast.script,
      fast.region == REGION ? null : (String)fast.region,
      orig.variant(),
      orig.extensions(),
      orig.privateUse()
    );
  }

  /**
   * Lookup any aliases that match this tag, and replace any undefined subtags.
   */
  private static void substituteLanguageAliases(FastTag dst) {
    List<Pair<FastTag, FastTag>> aliases = LANGUAGE_ALIAS_MAP.get(dst.language);
    if (aliases == null) {
      return;
    }

    for (int i = 0; i < aliases.size(); i++) {
      Pair<FastTag, FastTag> alias = aliases.get(i);
      FastTag type = alias._1;
      FastTag repl = alias._2;
      boolean exact = type.language.equals(repl.language)
          && type.script.equals(repl.script)
          && type.region.equals(repl.region);
      if ((type.script == SCRIPT && type.region == REGION) || exact) {
        dst.language = repl.language;
        if (dst.script == SCRIPT) {
          dst.script = repl.script;
        }
        if (dst.region == REGION) {
          dst.region = repl.region;
        }
        break;
      }
    }
  }

  /**
   * Replace the tag's region if it has a preferred value.
   */
  private static void substituteRegionAliases(FastTag dst) {
    if (dst.region != REGION) {
      List<String> regions = REGION_ALIAS_MAP.get(dst.region);
      if (regions != null) {
        // TODO: we currently use only the first region. See note in Typescript
        // cldr-engine project.
        dst.region = regions.get(0);
      }
    }
  }

  private static FastTag parseFastTag(String str) {
    LanguageTag tag = LanguageTagParser.parse(str);
    return new FastTag(tag);
  }

  /**
   * Holds core fields of a language tag for faster manipulation.
   */
  static class FastTag {
    Object language;
    Object script;
    Object region;

    FastTag(LanguageTag tag) {
      this.language = tag.hasLanguage() ? tag.language() : LANGUAGE;
      this.script = tag.hasScript() ? tag.script() : SCRIPT;
      this.region = tag.hasRegion() ? tag.region() : REGION;
    }

    FastTag(FastTag tag) {
      this.language = tag.language;
      this.script = tag.script;
      this.region = tag.region;
    }

    void setFields(FastTag src, int flags) {
      this.language = (flags & F_LANGUAGE) == 0 ? LANGUAGE : src.language;
      this.script = (flags & F_SCRIPT) == 0 ? SCRIPT : src.script;
      this.region = (flags & F_REGION) == 0 ? REGION : src.region;
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof FastTag) {
        FastTag o = (FastTag) other;
        return this.language.equals(o.language)
            && this.script.equals(o.script)
            && this.region.equals(o.region);
      }
      return false;
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + this.language.hashCode();
      result = 31 * result + this.script.hashCode();
      result = 31 * result + this.region.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "FastTag(" + this.language + ", " + this.script + ", " + this.region + ")";
    }
  }

  private static Map<FastTag, FastTag> loadLikelySubtags() {
    Map<FastTag, FastTag> map = new HashMap<>();
    for (String row : LocaleExternalData.LIKELYRAW.split("\\|")) {
      String[] parts = row.split(":");
      FastTag key = parseFastTag(parts[0]);
      FastTag val = parseFastTag(parts.length == 2 ? parts[1] : "");
      map.put(key, val);
    }
    return map;
  }

  private static Map<String, List<Pair<FastTag, FastTag>>> loadLanguageAliases() {
    Map<String, List<Pair<FastTag, FastTag>>> map = new HashMap<>();
    for (String row : LocaleExternalData.LANGUAGEALIASRAW.split("\\|")) {
      String[] parts = row.split(":");
      FastTag type = parseFastTag(parts[0]);
      FastTag repl = parseFastTag(parts[1]);
      String language = type.language.toString();
      List<Pair<FastTag, FastTag>> pairs = map.get(language);
      if (pairs == null) {
        pairs = new ArrayList<>();
        map.put(language, pairs);
      }
      pairs.add(Pair.of(type, repl));
    }
    return map;
  }

  private static Map<String, List<String>> loadRegionAliases() {
    Map<String, List<String>> map = new HashMap<>();
    for (String row : LocaleExternalData.TERRITORYALIASRAW.split("\\|")) {
      String[] parts = row.split(":");
      String key = parts[0];
      List<String> regions = new ArrayList<>();
      for (String region : parts[1].split(" ")) {
        regions.add(region);
      }
      map.put(key, regions);
    }
    return map;
  }

}

