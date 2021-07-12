package com.squarespace.cldrengine.internal;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;
import com.squarespace.cldrengine.ResourcePack;
import com.squarespace.cldrengine.ResourcePackLoader;
import com.squarespace.cldrengine.utils.JsonUtils;

/**
 * Loads the bundled resources from the Jar.
 */
public class DefaultResourcePackLoader implements ResourcePackLoader {

  private static final ConcurrentHashMap<String, ResourcePack> PACKS = new ConcurrentHashMap<>();

  public ResourcePack get(String language) {
    return PACKS.computeIfAbsent(language, lang -> {
      String name = language + ".json";
      try {
        JsonObject obj = JsonUtils.loadJsonResource(DefaultResourcePackLoader.class, name);
        return obj == null ? null : new ResourcePack(obj);
      } catch (IOException e) {
        throw new RuntimeException("Fatal: failed to load resource pack: '" + name + "'", e);
      }
    });
  }

}
