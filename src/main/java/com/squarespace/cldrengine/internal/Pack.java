package com.squarespace.cldrengine.internal;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.locale.LanguageTag;
import com.squarespace.cldrengine.locale.LocaleResolver;
import com.squarespace.cldrengine.utils.Decoders;

public class Pack {

  private static final JsonParser JSON_PARSER = new JsonParser();

  private final String version;
  private final String cldrVersion;
  private final String language;
  private final LanguageTag defaultTag;
  private final Map<String, PackScript> scripts;

  public Pack(String data) {
    this(JSON_PARSER.parse(data).getAsJsonObject());
  }

  public Pack(JsonObject data) {
    this.version = data.get("version").getAsString();
    this.cldrVersion = data.get("cldr").getAsString();
    this.language = data.get("language").getAsString();

//    this.defaultTag = data.get("default").getAsString();
    // TODO:
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

    private static final String SEP = "\t";

    final String[] strings;
    final String[] exceptions;
    final Map<String, String> regions;
    final Map<String, Map<Integer, Integer>> cache;
    final String defaultRegion;

    PackScript(JsonObject obj) {
      this.strings = obj.get("strings").getAsString().split(SEP);
      this.exceptions = obj.get("exceptions").getAsString().split(SEP);
      this.regions = new HashMap<>();
      this.cache = new HashMap<>();
      this.defaultRegion = obj.get("default").getAsString();
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
      byte[] bytes = Decoders.z85Decode(raw);
      int[] numbers = Decoders.vuintDecode(bytes);
      Map<Integer, Integer> index = new HashMap<>();
      for (int i = 0; i < numbers.length; i += 2) {
        int k = numbers[i];
        int v = numbers[i + 1];
        index.put(k, v);
      }
      this.cache.put(region, index);
      return index;
    }
  }

}
