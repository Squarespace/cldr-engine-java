package com.squarespace.cldrengine.plurals;

import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Plurals {

  /**
   * Get the plural rules for a given language and optional region.
   */
  public static PluralRulesImpl get(String language, String region) {
    Rule[] cardinals = resolve(PluralData.CARDINALS, language, region);
    Rule[] ordinals = resolve(PluralData.ORDINALS, language, region);

    // TODO: add plural ranges

    return new PluralRulesImpl(cardinals, ordinals);
  }

  private static Rule[] resolve(Map<String, Rule[]> map, String language, String region) {
    Rule[] result = null;
    if (region != null) {
      String key = String.format("%s-%s", language, region);
      result = map.get(key);
    }
    if (result == null) {
      result = map.get(language);
    }
    return result == null ? map.get("root") : result;
  }
}
