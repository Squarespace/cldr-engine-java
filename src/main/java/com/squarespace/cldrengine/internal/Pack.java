package com.squarespace.cldrengine.internal;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.locale.LocaleResolver;
import com.squarespace.cldrengine.utils.JsonUtils;

public class Pack {

  private final String version;
  private final String cldrVersion;
  private final String language;
  private final LanguageTag defaultTag;
  private final Map<String, PackScript> scripts;

  public Pack(String data) {
    this(JsonParser.parseString(data).getAsJsonObject());
  }

  public Pack(JsonObject data) {
    this.version = data.get("version").getAsString();
    this.cldrVersion = data.get("cldr").getAsString();
    this.language = data.get("language").getAsString();

    // TODO:
//    this.defaultTag = data.get("defaultTag").getAsString();
    this.defaultTag = null;
    JsonObject scripts = data.get("scripts").getAsJsonObject();
    this.scripts = new HashMap<>();
    for (Map.Entry<String, JsonElement> entry : scripts.entrySet()) {
      String key = entry.getKey();
      PackScript script = new PackScript(entry.getValue().getAsJsonObject());
      this.scripts.put(key, script);
    }
  }

  public String version() {
    return this.version;
  }

  public String cldrVersion() {
    return this.cldrVersion;
  }

  public String language() {
    return this.language;
  }

  public LanguageTag defaultTag() {
    return this.defaultTag;
  }

  public Bundle get(LanguageTag tag) {
    // We need the script and region to find the correct string layer. Caller should
    // ideally supply a resolved language tag to avoid the overhead of this call.
    if (!tag.hasLanguage() || !tag.hasScript() || !tag.hasRegion()) {
      tag = LocaleResolver.resolve(tag);
    }

    PackScript script = this.scripts.get(tag.script());
    if (script == null) {
      script = this.scripts.get(this.defaultTag.script());
      return script.get(this.defaultTag);
    }
    return script.get(tag);
  }

  private static class PackScript {

    private static final String SEP = "_";

    final String[] strings;
    final String[] exceptions;
    final Map<String, String> regions;
    final Map<String, Map<Integer, Integer>> cache;
    final String defaultRegion;

    PackScript(JsonObject obj) {
      this.strings = obj.get("strings").getAsString().split(SEP);
      this.exceptions = obj.get("exceptions").getAsString().split(SEP);
      this.regions = JsonUtils.decodeObject(obj.get("regions"));
      this.cache = new HashMap<>();
      this.defaultRegion = obj.get("defaultRegion").getAsString();
    }

    Bundle get(LanguageTag tag) {
      String region = tag.region();
      Map<Integer, Integer> index = this.cache.get(region);
      if (index == null) {
        index = this.decode(region);
      }
      if (index == null) {
        region = this.defaultRegion;
        tag = new LanguageTag(tag.language(), tag.script(), region, tag.variant(), tag.extensions(), tag.privateUse());
        index = this.cache.get(region);
        if (index == null) {
          index = this.decode(region);
        }
      }
      return new StringBundle(tag.compact(), tag, this.strings, this.exceptions, index);
    }

    private Map<Integer, Integer> decode(String region) {
      String raw = this.regions.get(region);
      if (raw == null) {
        return null;
      }
      Map<Integer, Integer> index = new HashMap<>();
      if (!isEmpty(raw)) {
        String[] parts = raw.split("\\s+");
        for (int i = 0; i < parts.length; i += 2) {
          Integer k = Integer.valueOf(parts[i], 36);
          Integer v = Integer.valueOf(parts[i + 1], 36);
          index.put(k, v);
        }
      }
      this.cache.put(region, index);
      return index;
    }

  }

}
