package com.squarespace.cldrengine.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.locale.CLocaleImpl;
import com.squarespace.cldrengine.utils.StringUtils;

public class Locales {

  private static final List<CLocale> LOCALES;
  private static final int LANG = 1;
  private static final int SCRIPT = 2;
  private static final int REGION = 4;
  private static final int VARIANT = 8;

  static {
    // Decode the compact available locales structure. This reconstitutes the locale
    // ids and language tags more efficiently than going through the resolver.
    JsonObject root = JsonParser.parseString(LocaleExternalData.RAWLOCALES).getAsJsonObject();
    List<CLocale> locales = new ArrayList<>();
    for (String lang : root.keySet()) {
      JsonObject scripts = root.get(lang).getAsJsonObject();
      for (String script : scripts.keySet()) {
        JsonArray regionInfo = scripts.get(script).getAsJsonArray();
        int end = regionInfo.size() - 1;
        String[] regions = regionInfo.get(end).getAsString().split(" ");
        for (int i = 0; i < regions.length; i++) {
          int flags = regionInfo.get(i).getAsInt();
          String region = regions[i];
          String variant = null;
          List<String> parts = new ArrayList<>();

          if ((flags & LANG) != 0) {
            parts.add(lang);
          }
          if ((flags & SCRIPT) != 0) {
            parts.add(script);
          }
          boolean hasVariant = (flags & VARIANT) != 0;
          if (hasVariant) {
            String[] p = region.split("-");
            region = p[0];
            variant = p[1];
          }
          if ((flags & REGION) != 0) {
            parts.add(region);
          }
          if (hasVariant) {
            parts.add(variant);
          }
          final String id = StringUtils.join(parts, "-");
          final LanguageTag tag = new LanguageTag(lang, script, region);
          CLocale locale = new CLocaleImpl(id, tag);
          locales.add(locale);
        }
      }
    }
    LOCALES = Collections.unmodifiableList(locales);
  }

  public static List<CLocale> availableLocales() {
    return LOCALES;
  }

}
