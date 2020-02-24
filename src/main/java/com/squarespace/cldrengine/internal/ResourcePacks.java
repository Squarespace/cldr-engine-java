package com.squarespace.cldrengine.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;
import com.squarespace.cldrengine.utils.JsonUtils;

public class ResourcePacks {

  private static final ConcurrentHashMap<String, Pack> PACKS = new ConcurrentHashMap<>();
  private static final String[] AVAILABLE_LOCALES;

  static {
    AVAILABLE_LOCALES = LocaleExternalData.AVAILABLELOCALESRAW.split("\\|");
  }

  public static List<String> availableLocales() {
    return Arrays.asList(AVAILABLE_LOCALES);
  }

  public static Pack get(String language) {
    return PACKS.computeIfAbsent(language, lang -> {
      String name = language + ".json";
      try {
        JsonObject obj = JsonUtils.loadJson(ResourcePacks.class, name);
        return obj == null ? null : new Pack(obj);
      } catch (IOException e) {
        throw new RuntimeException("Fatal: failed to load resource pack: '" + name + "'", e);
      }
    });
  }

}
