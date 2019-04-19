package com.squarespace.cldrengine.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.squarespace.cldrengine.internal.LocaleConstants;
import com.squarespace.cldrengine.utils.ResourceUtil;

public class LocaleMatcher {

  private static final Map<String, Integer> PARADIGM_LOCALES = load();
  private static final Pattern TAG_SEP = Pattern.compile("[,\\s]+", Pattern.MULTILINE);

  private static final LanguageTag UNDEFINED = new LanguageTag();

  private final Map<String, List<Entry>> exactMap = new HashMap<>();
  private final List<Entry> supported;
  private Entry defaultEntry;
  private int count;

  public LocaleMatcher(String supportedLocales) {
    this.supported = parse(supportedLocales);
    init();
  }

  public LocaleMatcher(String[] supportedLocales) {
    this.supported = parse(supportedLocales);
    init();
  }

  public LocaleMatcher(List<String> supportedLocales) {
    this.supported = parse(supportedLocales);
    init();
  }

  /**
   * Find the desired locale that is the closed match to a supported locale, within
   * the given threshold. Any matches whose distance is greater than or equal to the
   * threshold will be treated as having maximum distance.
   */
  public LocaleMatch match(String desiredLocales, int threshold) {
    List<Entry> desireds = parse(desiredLocales);
    return this._match(desireds, threshold);
  }

  /**
   * Find the desired locale that is the closed match to a supported locale, within
   * the given threshold. Any matches whose distance is greater than or equal to the
   * threshold will be treated as having maximum distance.
   */
  public LocaleMatch match(String[] desiredLocales, int threshold) {
    List<Entry> desireds = parse(desiredLocales);
    return this._match(desireds, threshold);
  }

  /**
   * Find the desired locale that is the closed match to a supported locale, within
   * the given threshold. Any matches whose distance is greater than or equal to the
   * threshold will be treated as having maximum distance.
   */
  public LocaleMatch match(List<String> desiredLocales, int threshold) {
    List<Entry> desireds = parse(desiredLocales);
    return this._match(desireds, threshold);
  }

  private LocaleMatch _match(List<Entry> desireds, int threshold) {
    int len = desireds.size();
    int bestDistance = DistanceTable.MAX_DISTANCE;
    Entry bestMatch = null;
    Entry bestDesired = len == 0 ? this.defaultEntry : desireds.get(0);
    for (int i = 0; i < len; i++) {
      Entry desired = desireds.get(i);
      List<Entry> exact = this.exactMap.get(desired.compact);
      if (exact != null) {
        CLocale locale = new CLocaleImpl(exact.get(0).id, exact.get(0).tag);
        return new LocaleMatch(locale, 0);
      }

      for (int j = 0; j < this.count; j++) {
        Entry supported = this.supported.get(j);
        int distance = DistanceTable.distance(desired.tag, supported.tag, threshold);
        if (distance < bestDistance) {
          bestDistance = distance;
          bestMatch = supported;
          bestDesired = desired;
        }
      }
    }

    if (bestMatch == null) {
      bestMatch = this.defaultEntry;
    }

    LanguageTag tag = new LanguageTag(
        bestMatch.tag.language(),
        bestMatch.tag.script(),
        bestMatch.tag.region(),
        bestMatch.tag.variant(),
        bestDesired.tag.extensions(),
        bestDesired.tag.privateUse()
    );
    CLocale locale = new CLocaleImpl(bestMatch.id, tag);
    return new LocaleMatch(locale, bestDistance);
  }

  private void init() {
    this.count = this.supported.size();

    // The first locale in the list is used as the default.
    this.defaultEntry = this.supported.get(0);

    this.supported.sort((Entry a, Entry b) -> {
      // Keep default tag at the front..
      if (a.tag == this.defaultEntry.tag) {
        return -1;
      }
      if (b.tag == this.defaultEntry.tag) {
        return 1;
      }

      // Sort all paradigm locales before non-paradigms.
      Integer pa = PARADIGM_LOCALES.get(a.compact);
      Integer pb = PARADIGM_LOCALES.get(b.compact);
      if (pa != null) {
        return pb == null ? -1 : Integer.compare(pa, pb);
      } else if (pb != null) {
        return 1;
      }

      // All other locales stay in their relative positions.
      return 0;
    });

    // Wire up a map for quick lookups of exact matches. These have a
    // distance of 0 and will short-circuit the matching loop.
    this.supported.forEach(locale -> {;
      String key = locale.compact;
      List<Entry> bundles = this.exactMap.get(key);
      if (bundles == null) {
        bundles = new ArrayList<>();
        this.exactMap.put(key, bundles);
      }
      bundles.add(locale);
    });
  }

  private static List<Entry> parse(String locales) {
    List<Entry> res = new ArrayList<>();
    for (String locale : TAG_SEP.split(locales)) {
      res.add(parseEntry(locale));
    }
    return res;
  }

  private static List<Entry> parse(List<String> locales) {
    List<Entry> res = new ArrayList<>();
    for (String locale : locales) {
      res.addAll(parse(locale));
    }
    return res;
  }

  private static List<Entry> parse(String[] locales) {
    List<Entry> res = new ArrayList<>();
    for (String locale : locales) {
      res.addAll(parse(locale));
    }
    return res;
  }

  private static Entry parseEntry(String raw) {
    String id = raw.trim();
    LanguageTag tag = LanguageTagParser.parse(id);
    if (tag.hasLanguage() || tag.hasScript() || tag.hasRegion()) {
      return new Entry(id, LocaleResolver.resolve(tag));
    }
    return new Entry(id, UNDEFINED);
  }

  static class Entry implements CLocale {

    private final String id;
    private final LanguageTag tag;
    private final String compact;

    public Entry(String id, LanguageTag tag) {
      this.id = id;
      this.tag = tag;
      this.compact = tag.compact();
    }

    @Override
    public String id() {
      return this.id;
    }

    @Override
    public LanguageTag tag() {
      return this.tag;
    }

    public String compact() {
      return this.compact;
    }
  }

  private static final Map<String, Integer> load() {
    JsonArray elems = (JsonArray) ResourceUtil.parse(LocaleConstants.PARADIGMLOCALES);
    Map<String, Integer> res = new HashMap<>();
    for (int i = 0; i < elems.size(); i++) {
      String raw = elems.get(i).getAsString();
      String compact = LocaleResolver.resolve(raw).compact();
      res.put(compact, i);
    }
    return res;
  }
}
