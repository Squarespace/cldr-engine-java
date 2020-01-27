package com.squarespace.cldrengine.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.locale.LanguageTagParser;
import com.squarespace.cldrengine.utils.JsonUtils;

public class ResourcePacks {

  private static final String[] AVAILABLE_LOCALES;

  static {
    AVAILABLE_LOCALES = LocaleExternalData.AVAILABLELOCALESRAW.split("\\|");
  }

  private static final Map<String, Pack> PACKS = load();

  public static Pack get(String language) {
    return PACKS.get(language);
  }

  private static Map<String, Pack> load() {
    Map<String, Pack> map = new HashMap<>();
    for (String locale : AVAILABLE_LOCALES) {
      LanguageTag tag = LanguageTagParser.parse(locale);
      String language = tag.language();
      if (map.containsKey(language)) {
        continue;
      }
      String name = language + ".json";
      try {
        JsonObject obj = JsonUtils.loadJson(ResourcePacks.class, name);
        map.put(language, new Pack(obj));
      } catch (IOException e) {
        throw new RuntimeException("Fatal: failed to load resource pack: '" + name + "'", e);
      }
    }
    return map;
  }

  public static List<String> availableLocales() {
    return Arrays.asList(AVAILABLE_LOCALES);
  }

}
