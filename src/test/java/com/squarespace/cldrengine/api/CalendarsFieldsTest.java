package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squarespace.cldrengine.CLDR;

/**
 * Calendar locale-field lookups, ported from @phensley/cldr 1.15.0
 * (packages/cldr-core/src/api/calendars.ts:64-120).
 *
 * Expected values in calendar-fields-expected.json were captured from the
 * published 1.15.0 TS engine for the locales below; the Java engine must
 * produce byte-identical output.
 *
 * The TS API returns `any` for dayPeriods/months/quarters/weekdays/eras.
 * In Java that maps to a nested {@code Map<String, Object>} with String
 * leaves; the comparison method here rebuilds the expected nested maps from
 * the JSON using Gson (order-insensitive Map equality).
 */
public class CalendarsFieldsTest {

  private static final Gson GSON = new Gson();
  private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() { }.getType();

  private static final String[] LOCALES = new String[] { "en", "ja", "ar" };

  private static JsonObject expectedRoot() throws Exception {
    try (InputStream in = CalendarsFieldsTest.class.getResourceAsStream("calendar-fields-expected.json")) {
      return JsonParser.parseReader(new InputStreamReader(in, "UTF-8")).getAsJsonObject();
    }
  }

  /**
   * Convert a JSON object element into the nested Map<String,Object> shape
   * returned by the Calendars API methods.
   */
  @SuppressWarnings("unchecked")
  private static Map<String, Object> map(JsonElement e) {
    return (Map<String, Object>) GSON.fromJson(e, MAP_TYPE);
  }

  private static CalendarFieldsOptions fieldOptions(String width) {
    return CalendarFieldsOptions.build().width(FieldWidthType.fromString(width));
  }

  private static CalendarFieldsOptions fieldOptions(String width, ContextType context) {
    return CalendarFieldsOptions.build().width(FieldWidthType.fromString(width)).context(context);
  }

  private static DateFieldFormatOptions dateOptions(String width, ContextType context) {
    DateFieldFormatOptions o = DateFieldFormatOptions.build().width(DateFieldWidthType.fromString(width));
    if (context != null) {
      o.context(context);
    }
    return o;
  }

  @Test
  public void testWeekData() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      assertEquals(c.firstDayOfWeek(), expected.get("firstDayOfWeek").getAsInt(), lang + " firstDayOfWeek");
      assertEquals(c.minDaysInFirstWeek(), expected.get("minDaysInFirstWeek").getAsInt(), lang + " minDaysInFirstWeek");
    }
  }

  @Test
  public void testDateField() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      JsonObject table = expected.getAsJsonObject("dateField");
      for (Map.Entry<String, JsonElement> e : table.entrySet()) {
        DateFieldType type = DateFieldType.fromString(e.getKey());
        JsonObject row = e.getValue().getAsJsonObject();
        for (String width : new String[] { "wide", "short", "narrow" }) {
          assertEquals(c.dateField(type, dateOptions(width, null)), row.get(width).getAsString(),
              lang + " dateField " + type + " " + width);
        }
        assertEquals(c.dateField(type, dateOptions("wide", ContextType.BEGIN_SENTENCE)), row.get("context").getAsString(),
            lang + " dateField " + type + " begin-sentence");
      }
    }
  }

  @Test
  public void testMonths() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      JsonObject table = expected.getAsJsonObject("months");
      for (String w : new String[] { "wide", "abbreviated", "short", "narrow" }) {
        assertEquals(map(table.get(w)), c.months(fieldOptions(w)), lang + " months " + w);
        assertEquals(map(table.get(w + "-standalone")), c.months(fieldOptions(w, ContextType.STANDALONE)),
            lang + " months " + w + " standalone");
      }
    }
  }

  @Test
  public void testWeekdays() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      JsonObject table = expected.getAsJsonObject("weekdays");
      for (String w : new String[] { "wide", "abbreviated", "short", "narrow" }) {
        assertEquals(map(table.get(w)), c.weekdays(fieldOptions(w)), lang + " weekdays " + w);
        assertEquals(map(table.get(w + "-standalone")), c.weekdays(fieldOptions(w, ContextType.STANDALONE)),
            lang + " weekdays " + w + " standalone");
      }
    }
  }

  @Test
  public void testEras() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      JsonObject table = expected.getAsJsonObject("eras");
      for (String w : new String[] { "names", "abbr", "narrow" }) {
        assertEquals(map(table.get(w)), c.eras(EraFieldsOptions.build().width(EraWidthType.fromString(w))),
            lang + " eras " + w);
      }
    }
  }

  @Test
  public void testDayPeriods() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      JsonObject table = expected.getAsJsonObject("dayPeriods");
      for (String w : new String[] { "wide", "abbreviated", "narrow" }) {
        assertEquals(map(table.get(w)), c.dayPeriods(fieldOptions(w)), lang + " dayPeriods " + w);
      }
    }
  }

  @Test
  public void testQuarters() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      JsonObject table = expected.getAsJsonObject("quarters");
      // 'short' is not a quarter width in the CLDR data; it maps to no data.
      for (String w : new String[] { "wide", "abbreviated", "narrow", "short" }) {
        assertEquals(map(table.get(w)), c.quarters(fieldOptions(w)), lang + " quarters " + w);
      }
    }
  }

  @Test
  public void testDefaults() throws Exception {
    JsonObject root = expectedRoot();
    for (String lang : LOCALES) {
      JsonObject expected = root.getAsJsonObject(lang);
      Calendars c = CLDR.get(lang).Calendars;
      // TS defaults: months/weekdays/quarters/dayPeriods default to 'wide',
      // eras default to 'names'.
      assertEquals(map(expected.getAsJsonObject("months").get("wide")), c.months(null), lang + " months default");
      assertEquals(map(expected.getAsJsonObject("weekdays").get("wide")), c.weekdays(null), lang + " weekdays default");
      assertEquals(map(expected.getAsJsonObject("quarters").get("wide")), c.quarters(null), lang + " quarters default");
      assertEquals(map(expected.getAsJsonObject("dayPeriods").get("wide")), c.dayPeriods(null), lang + " dayPeriods default");
      assertEquals(map(expected.getAsJsonObject("eras").get("names")), c.eras(null), lang + " eras default");
    }
  }

  /**
   * Context transforms produce different names where CLDR defines them:
   * ru months are title-cased in standalone/begin-sentence contexts
   * (month-standalone-except-narrow == "TT"), while 'en' dates fields are
   * capped at begin-sentence/standalone (calendar-field == "TT").
   */
  @Test
  public void testContextTransforms() throws Exception {
    JsonObject root = expectedRoot();
    JsonObject ruExpected = root.getAsJsonObject("ru");
    JsonObject csExpected = root.getAsJsonObject("cs");
    JsonObject enExpected = root.getAsJsonObject("en");

    CLDR ru = CLDR.get("ru");
    Map<String, Object> ruDefault = ru.Calendars.months(fieldOptions("wide"));
    Map<String, Object> ruStandalone = ru.Calendars.months(fieldOptions("wide", ContextType.STANDALONE));
    Map<String, Object> ruBegin = ru.Calendars.months(fieldOptions("wide", ContextType.BEGIN_SENTENCE));

    assertEquals(map(ruExpected.getAsJsonObject("months").getAsJsonObject("wide")), ruDefault, "ru months wide");
    assertEquals(map(ruExpected.getAsJsonObject("context").getAsJsonObject("monthsStandalone")), ruStandalone,
        "ru months wide standalone");
    assertEquals(map(ruExpected.getAsJsonObject("context").getAsJsonObject("monthsBeginSentence")), ruBegin,
        "ru months wide begin-sentence");

    // CLDR defines standalone names for Russian: they differ from the default (format) names.
    assertNotEquals(ruDefault, ruStandalone, "ru months default vs standalone");

    // cs uses "NT" -> ui-list-or-menu title-cases, standalone does not.
    CLDR cs = CLDR.get("cs");
    assertEquals(map(csExpected.getAsJsonObject("context").getAsJsonObject("monthsUiListMenu")),
        cs.Calendars.months(fieldOptions("wide", ContextType.UI_LIST_OR_MENU)), "cs months ui-list-or-menu");
    assertEquals(map(csExpected.getAsJsonObject("context").getAsJsonObject("monthsStandalone")),
        cs.Calendars.months(fieldOptions("wide", ContextType.STANDALONE)), "cs months standalone");

    // en dateField: begin-sentence and standalone cap the first letter.
    CLDR en = CLDR.get("en");
    assertEquals(enExpected.getAsJsonObject("context").get("dateFieldStandalone").getAsString(),
        en.Calendars.dateField(DateFieldType.DAY, dateOptions("wide", ContextType.STANDALONE)),
        "en dateField day standalone");
  }

}
