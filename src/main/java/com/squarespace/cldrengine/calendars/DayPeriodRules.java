package com.squarespace.cldrengine.calendars;

import java.util.Arrays;
import java.util.Map;

import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.internal.CalendarExternalData;
import com.squarespace.cldrengine.utils.Cache;
import com.squarespace.cldrengine.utils.JsonUtils;
import com.squarespace.cldrengine.utils.Search;
import com.squarespace.cldrengine.utils.StringUtils;

import lombok.AllArgsConstructor;

public class DayPeriodRules {

  private static final String[] DAY_PERIOD_KEYS;
  private static Map<String, String> DAY_PERIOD_RULES;

  static {
    DAY_PERIOD_KEYS = JsonUtils.decodeArray(JsonParser.parseString(CalendarExternalData.DAYPERIODKEYS));
    DAY_PERIOD_RULES = JsonUtils.decodeObject(JsonParser.parseString(CalendarExternalData.DAYPERIODRULES));
  }

  private final Cache<Rule> cache;

  public DayPeriodRules() {
    this.cache = new Cache<>(this::parse, 128);
  }

  public String get(Bundle bundle, long minutes) {
    String raw = DAY_PERIOD_RULES.get(bundle.languageRegion());
    if (raw == null) {
      raw = DAY_PERIOD_RULES.get(bundle.language());
    }
    if (raw == null) {
      return null;
    }
    Rule rule = this.cache.get(raw);
    int i = Search.binarySearch(rule.minutes, true, minutes);
    return rule.keys[i];
  }

  private Rule parse(String raw) {
    String[] parts = raw.split("\\|");
    long[] minutes = StringUtils.longArray(parts[1]);
    long[] indices = StringUtils.longArray(parts[0], 10);
    String[] keys = Arrays.stream(indices).mapToObj(i -> DAY_PERIOD_KEYS[(int)i]).toArray(String[]::new);
    return new Rule(minutes, keys);
  }

  @AllArgsConstructor
  public static class Rule {
    long[] minutes;
    String[] keys;
  }

}
