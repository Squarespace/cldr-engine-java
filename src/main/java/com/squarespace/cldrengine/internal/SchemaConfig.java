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
import com.squarespace.cldrengine.utils.ResourceUtil;

public class SchemaConfig extends HashMap<String, List<String>> {

  private static final Set<String> CHECKSUM_IGNORE = new HashSet<>(Arrays.asList("calendars"));

  private static final List<String> COPY_KEYS = Arrays.asList(
      "gregorian-available-format",
      "gregorian-plural-format",
      "gregorian-interval-format",
      "buddhist-available-format",
      "buddhist-plural-format",
      "buddhist-interval-format",
      "japanese-available-format",
      "japanese-plural-format",
      "japanese-interval-format",
      "persian-available-format",
      "persian-plural-format",
      "persian-interval-format",
      "currency-id",
      "language-id",
      "script-id",
      "region-id",
      "unit-id",
      "timezone-id",
      "number-system-name"
  );

  public SchemaConfig() {
    pute("alt-key", Meta.KEY_ALT_KEY);
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

// TODO:
//    pute("number-misc-pattern", Meta.KEY_MISC_PATTERN_INDEX);
//    puts("number-misc-pattern", new KeyIndex<String>(new String[] {
//      "at-least", "at-most", "approx", "range"
//    }));
    pute("number-symbol", Meta.KEY_NUMBER_SYMBOL);
    pute("number-system", Meta.KEY_NUMBER_SYSTEM);

    // TIMEZONE INDICES
    pute("metazone", Meta.KEY_METAZONE);
    pute("timezone-type", Meta.KEY_TIMEZONE_TYPE);
  }

  private <T extends StringEnum> void pute(String key, KeyIndex<T> index) {
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

  public static void main(String[] args) throws Exception {
    SchemaConfig c = new SchemaConfig();
    c.load();
    System.out.println(c.checksum("0.14.0-alpha.0"));
  }

  /**
   * Load the JSON configuration.
   */
  public void load() throws IOException {
    JsonObject root = (JsonObject) ResourceUtil.load(SchemaConfig.class, "config.json");
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

  public String checksum(String version) {
    List<String> keys = new ArrayList<>(keySet());
    Collections.sort(keys, String::compareTo);
    Checksum c = new Checksum();
    c.update(version);
    for (String key : keys) {
      if (CHECKSUM_IGNORE.contains(key)) {
        continue;
      }
      c.update(key);
      List<String> values = this.get(key);
      for (String val : values) {
        c.update(val);
      }
    }
    return c.get();
  }

}
