package com.squarespace.cldrengine.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.squarespace.cldrengine.locale.LanguageTag;
import com.squarespace.cldrengine.locale.LanguageTagParser;
import com.squarespace.cldrengine.utils.ResourceUtil;

public class ResourcePacks {

  private static final Map<String, Pack> PACKS = load();

  public static Pack get(String language) {
    return PACKS.get(language);
  }

  private static Map<String, Pack> load() {
    Map<String, Pack> map = new HashMap<>();
    String[] locales = LocaleConstants.AVAILABLELOCALESRAW.split("\\|");
    for (String locale : locales) {
      LanguageTag tag = LanguageTagParser.parse(locale);
      String language = tag.language();
      if (map.containsKey(language)) {
        continue;
      }
      String name = language + ".json";
      try {
        JsonObject obj = ResourceUtil.load(ResourcePacks.class, name);
        map.put(language, new Pack(obj));
      } catch (IOException e) {
        throw new RuntimeException("Fatal: failed to load resource pack: '" + name + "'", e);
      }
    }
    return map;
  }


}
