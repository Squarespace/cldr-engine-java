package com.squarespace.cldrengine.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squarespace.cldrengine.utils.Checksum;
import com.squarespace.cldrengine.utils.JsonUtils;
import com.squarespace.cldrengine.utils.StringUtils;

public class SchemaConfig extends HashMap<String, List<String>> {

  private static final Set<String> CHECKSUM_IGNORE = new HashSet<>(Arrays.asList("calendars"));

  public SchemaConfig() {
    puts("calendars", new KeyIndex<>(new String[] {
        "gregory", "buddhist", "japanese", "persian"
    }));

    pute("alt-key", Meta.KEY_ALT_KEY);
    pute("day-period-alt-key", Meta.KEY_DAY_PERIOD_ALT_KEY);
    pute("era-alt-key", Meta.KEY_ERA_ALT_KEY);
    pute("plural-key", Meta.KEY_PLURAL_KEY);

    // CALENDAR INDICES
    pute("date-time-pattern-field", Meta.KEY_DATE_TIME_PATTERN_FIELD);
    puts("day-period", Meta.KEY_DAY_PERIOD);
    pute("era-type", Meta.KEY_ERA_TYPE);
    puts("field-width", Meta.KEY_FIELD_WIDTH);
    pute("format-width", Meta.KEY_FORMAT_WIDTH);
    puts("quarter", Meta.KEY_QUARTER);
    puts("weekday", Meta.KEY_WEEKDAY);

    puts("gregorian-era", Meta.KEY_GREGORIAN_ERA);
    puts("gregorian-month", Meta.KEY_GREGORIAN_MONTH);
    puts("buddhist-era", Meta.KEY_BUDDHIST_ERA);
    puts("buddhist-month", Meta.KEY_BUDDHIST_MONTH);
    puts("japanese-era", Meta.KEY_JAPANESE_ERA);
    puts("japanese-month", Meta.KEY_JAPANESE_MONTH);
    puts("persian-era", Meta.KEY_PERSIAN_ERA);
    puts("persian-month", Meta.KEY_PERSIAN_MONTH);

    // DATEFIELDS INDICES
    pute("date-field", Meta.KEY_DATE_FIELD);
    pute("date-field-width", Meta.KEY_DATE_FIELD_WIDTH);
    pute("relative-time-field", Meta.KEY_RELATIVE_TIME_FIELD);

    // GENERAL INDICES
    pute("context-transform-field", Meta.KEY_CONTEXT_TRANSFORM_FIELD);
    pute("list-pattern-position", Meta.KEY_LIST_PATTERN_POSITION);

    // NUMBERS INDICES
    pute("currency-spacing-pattern", Meta.KEY_CURRENCY_SPACING_PATTERN);
    pute("currency-spacing-pos", Meta.KEY_CURRENCY_SPACING_POS);

    pute("number-misc-pattern", Meta.KEY_NUMBER_MISC_PATTERN);
    pute("number-symbol", Meta.KEY_NUMBER_SYMBOL);
    pute("number-system", Meta.KEY_NUMBER_SYSTEM);

    // TIMEZONE INDICES
    pute("metazone", Meta.KEY_METAZONE);
    pute("timezone-type", Meta.KEY_TIMEZONE_TYPE);

    try {
      load();
    } catch (IOException e) {
      throw new RuntimeException("FATAL: failed to load schema config", e);
    }
  }

  private <T extends StringEnum<T>> void pute(String key, KeyIndex<T> index) {
    T[] keys = index.keys();
    List<String> res = new ArrayList<>(keys.length);
    for (T t : keys) {
      res.add(t.value());
    }
    put(key, res);
  }

  private void puts(String key, KeyIndex<String> index) {
    String[] keys = index.keys();
    put(key, Arrays.asList(keys));
  }

  /**
   * Load the JSON configuration.
   */
  private void load() throws IOException {
    JsonObject root = (JsonObject) JsonUtils.loadJson(SchemaConfig.class, "config.json");
    copy("currency-id", root);
    copy("language-id", root);
    copy("script-id", root);
    copy("region-id", root);
    copy("unit-id", root);
    copy("timezone-id", root);
    copy("number-system-name", root);
    copy("gregorian-available-format", root);
    copy("gregorian-plural-format", root);
    copy("gregorian-interval-format", root);
    copy("buddhist-available-format", root);
    copy("buddhist-plural-format", root);
    copy("buddhist-interval-format", root);
    copy("japanese-available-format", root);
    copy("japanese-plural-format", root);
    copy("japanese-interval-format", root);
    copy("persian-available-format", root);
    copy("persian-plural-format", root);
    copy("persian-interval-format", root);
  }

  private void copy(String key, JsonObject root) {
    JsonArray arr = root.getAsJsonArray(key);
    put(key, _array(arr));
  }

  private List<String> _array(JsonArray array) {
    List<String> values = new ArrayList<>();
    for (int i = 0; i < array.size(); i++) {
      values.add(array.get(i).getAsString());
    }
    return values;
  }

  public String checksum(String fullversion) {
    List<String> keys = new ArrayList<>(keySet());
    Collections.sort(keys, String::compareTo);
    Checksum c = new Checksum();
    List<String> v = StringUtils.split(fullversion, '.');
    String version = v.get(0) + '.' + v.get(1);
    c.update(version);
    for (String key : keys) {
      if (CHECKSUM_IGNORE.contains(key)) {
        continue;
      }
      c.update(key);
      for (String val : this.get(key)) {
        c.update(val);
      }
    }
    return c.get();
  }

}
