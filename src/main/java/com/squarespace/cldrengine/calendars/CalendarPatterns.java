package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.StringUtils.parseArray;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DayPeriodAltType;
import com.squarespace.cldrengine.api.EraAltType;
import com.squarespace.cldrengine.api.EraWidthType;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.internal.CalendarExternalData;
import com.squarespace.cldrengine.internal.CalendarSchema;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.utils.JsonUtils;
import com.squarespace.cldrengine.utils.LRU;
import com.squarespace.cldrengine.utils.StringUtils;

import lombok.AllArgsConstructor;

/**
 * Manages patterns for a given calendar.
 */
class CalendarPatterns {

  @AllArgsConstructor
  public static class TimeData {
    String _1;
    String _2;

    @Override
    public String toString() {
      return "TimeData(" + _1 + ", " + _2 + ")";
    }
  }

  private static final Map<String, Map<String, TimeData>> TIMEDATA = new HashMap<>();

  static {
    String[] timeStrings = JsonUtils.decodeArray(JsonParser.parseString(CalendarExternalData.TIMESTRINGS));

    JsonObject root = JsonParser.parseString(CalendarExternalData.TIMEDATA).getAsJsonObject();
    for (String lang : root.keySet()) {
      Map<String, TimeData> map = new HashMap<>();
      TIMEDATA.put(lang, map);

      JsonObject regions = root.getAsJsonObject(lang);
      for (String region : regions.keySet()) {
        int index = regions.get(region).getAsInt();
        String str = timeStrings[index];
        String[] parts = str.split("\\|");
        TimeData data = new TimeData(parts[0], parts[1]);
        map.put(region, data);
      }
    }
  }

  private final String language;
  private final String region;

  private final DateSkeletonParser skeletonParser;
  private final Map<FormatWidthType, String> dateFormats;
  private final Map<FormatWidthType, String> dateSkeletons;
  private final Map<FormatWidthType, String> timeFormats;
  private final Map<FormatWidthType, String> timeSkeletons;
  private final Map<FormatWidthType, String> wrapperFormats;
  private final Map<FormatWidthType, String> wrapperFormatsAt;

  private final DatePatternMatcher<Object> availableMatcher = new DatePatternMatcher<Object>();
  private final DatePatternMatcher<IntervalSkeleton> intervalMatcher = new DatePatternMatcher<IntervalSkeleton>();
  private final Map<String, Map<DateTimePatternFieldType, String>> rawIntervalFormats;
  private final String intervalFallback;
  private final LRU<String, CachedSkeletonRequest> skeletonRequestCache = new LRU<>(50);

  protected final Bundle bundle;
  protected final Internals internals;
  protected final Map<String, String> rawAvailableFormats;
  protected final Map<PluralType, Map<String, String>> rawPluralFormats;
  protected final CalendarSchema schema;

  public CalendarPatterns(Bundle bundle, Internals internals, CalendarSchema schema) {
    this.bundle = bundle;
    this.language = bundle.language();
    this.region = bundle.region();
    this.internals = internals;

    this.dateFormats = schema.dateFormats.mapping(bundle);
    this.dateSkeletons = schema.dateSkeletons.mapping(bundle);
    this.timeFormats = schema.timeFormats.mapping(bundle);
    this.timeSkeletons = schema.timeSkeletons.mapping(bundle);
    this.wrapperFormats = schema.dateTimeFormats.mapping(bundle);
    this.wrapperFormatsAt = schema.dateTimeFormatsAt.mapping(bundle);
    this.skeletonParser = buildSkeletonParser();
    this.schema = schema;

    this.rawAvailableFormats = schema.availableFormats.mapping(bundle);
    this.rawPluralFormats = schema.pluralFormats.mapping(bundle);
    this.rawIntervalFormats = schema.intervalFormats.mapping(bundle);
    this.buildAvailableMatcher();
    this.buildIntervalMatcher();

    this.intervalFallback = schema.intervalFormatFallback.get(bundle);
  }

  public DateSkeleton parseSkeleton(String raw) {
    return this.skeletonParser.parse(raw, false);
  }

  public CachedSkeletonRequest getCachedSkeletonRequest(String key) {
    return this.skeletonRequestCache.get(key);
  }

  public void setCachedSkeletonRequest(String key, CachedSkeletonRequest req) {
    this.skeletonRequestCache.set(key, req);
  }

  /**
   * Map of day period alternate names for each width.
   * Equivalent to TS: `schema.standAlone.dayPeriods.mapping(bundle)`.
   */
  public Map<String, Map<String, Map<DayPeriodAltType, String>>> dayPeriods() {
    return this.schema.standAlone.dayPeriods.mapping(this.bundle);
  }

  /**
   * Map of era names for each width and alternate.
   * Equivalent to TS: `schema.eras.mapping(bundle)`.
   */
  public Map<EraWidthType, Map<String, Map<EraAltType, String>>> eras() {
    return this.schema.eras.mapping(this.bundle);
  }

  /**
   * Map of standalone month names for each width.
   * Equivalent to TS: `schema.standAlone.months.mapping(bundle)`.
   */
  public Map<String, Map<String, String>> months() {
    return this.schema.standAlone.months.mapping(this.bundle);
  }

  /**
   * Map of standalone quarter names for each width.
   * Equivalent to TS: `schema.standAlone.quarters.mapping(bundle)`.
   */
  public Map<String, Map<String, String>> quarters() {
    return this.schema.standAlone.quarters.mapping(this.bundle);
  }

  /**
   * Map of standalone weekday names for each width.
   * Equivalent to TS: `schema.standAlone.weekdays.mapping(bundle)`.
   */
  public Map<String, Map<String, String>> weekdays() {
    return this.schema.standAlone.weekdays.mapping(this.bundle);
  }

  public DateTimePattern getDatePattern(FormatWidthType width) {
    String pattern = this.dateFormats.getOrDefault(width, "");
    return DateTimePattern.parse(pattern);
  }

  public DateTimePattern getTimePattern(FormatWidthType width) {
    String pattern = this.timeFormats.getOrDefault(width, "");
    return DateTimePattern.parse(pattern);
  }

  public String getWrapperPattern(FormatWidthType width, boolean atTime) {
    String w = this.wrapperFormatsAt.getOrDefault(width, "");
    return atTime ? w : this.wrapperFormats.getOrDefault(width, "");
  }

  public DateTimePattern getAvailablePattern(CalendarDate d, DateSkeleton s) {
    String pattern = s.pattern;
    if (pattern == null) {
      pattern = this.rawAvailableFormats.get(s.skeleton);
      if (pattern == null) {
        Map<String, String> formats = this.rawPluralFormats.get(PluralType.OTHER);
        pattern = formats.get(s.skeleton);
      }
    }
    return DateTimePattern.parse(pattern == null ? "" : pattern);
  }

  public String getIntervalFallback() {
    return this.intervalFallback;
  }

  public DateTimePattern adjustPattern(DateTimePattern pattern, DateSkeleton skeleton, DateSkeleton matched, String decimal) {
    return this.availableMatcher.adjust(pattern, skeleton, matched, decimal);
  }

  public DateSkeleton matchAvailable(DateSkeleton skeleton) {
    return this.availableMatcher.match(skeleton).skeleton;
  } 

  public DatePatternMatcherEntry<IntervalSkeleton> matchInterval(DateSkeleton skeleton) {
    return this.intervalMatcher.match(skeleton);
  }

  protected TimeData getTimeData() {
    Map<String, TimeData> root = TIMEDATA.get("");
    TimeData world = root.get("001");
    TimeData region = null;
    Map<String, TimeData> lang = TIMEDATA.get(this.language);
    if (lang != null) {
      region = lang.get(this.region);
    }
    if (region == null) {
      region = root.get(this.region);
    }
    return region == null ? world : region;
  }

  protected DateSkeletonParser buildSkeletonParser() {
    TimeData pair = this.getTimeData();
    DateTimePattern[] allowed = parseArray(pair._1, DateTimePattern.class, DateTimePattern::parse);
    DateTimePattern preferred = DateTimePattern.parse(pair._2);
    return new DateSkeletonParser(preferred.nodes, allowed[0].nodes);
  }

  protected void buildAvailableMatcher() {
    for (FormatWidthType key : this.dateFormats.keySet()) {
      DateSkeleton ds = this.skeletonParser.parse(this.dateFormats.get(key), true);
      String dkey = this.dateSkeletons.get(key);
      if (dkey != null && dkey.length() > 0) {
        ds.skeleton = dkey; // CLDR-authoritative key
      }
      this.availableMatcher.add(ds, null);
      DateSkeleton ts = this.skeletonParser.parse(this.timeFormats.get(key), true);
      String tkey = this.timeSkeletons.get(key);
      if (tkey != null && tkey.length() > 0) {
        ts.skeleton = tkey; // CLDR-authoritative key
      }
      this.availableMatcher.add(ts, null);
    }

    // For the pluralized formats use the 'other' category which will
    // be populated for every locale.
    this.buildAvailableMatcherFormats(this.rawAvailableFormats);
    this.buildAvailableMatcherFormats(this.rawPluralFormats.get(PluralType.OTHER));
    this.availableMatcher.sort();
  }

  protected void buildAvailableMatcherFormats(Map<String, String> formats) {
    if (formats != null) {
      for (String skeleton : formats.keySet()) {
        // Only add skeletons which point to valid formats for this locale.
        // Not all skeletons are implemented for all locales.
        String value = formats.get(skeleton);
        if (!StringUtils.isEmpty(value)) {
          this.availableMatcher.add(this.skeletonParser.parse(skeleton, false), null);
        }
      }
    }
  }

  protected void buildIntervalMatcher() {
    for (String rawSkeleton : this.rawIntervalFormats.keySet()) {
      Map<DateTimePatternFieldType, String> patterns = this.rawIntervalFormats.get(rawSkeleton);
      if (patterns.isEmpty()) {
        continue;
      }
      DateSkeleton skeleton = this.skeletonParser.parse(rawSkeleton, false);
      this.intervalMatcher.add(skeleton, new IntervalSkeleton(skeleton, patterns));
    }
    this.intervalMatcher.sort();
  }
}
