package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.parsing.DateTimePattern.DateTimeNode;

/**
 * Regression tests for skeleton best-fit matching, ported from the TS
 * commits landing the post-tag best-fit fixes:
 *
 *  - 6aaf3adf: ties prefer unchanged date field widths (getKindDistance)
 *  - e6409209: TR35 rule 2 properties freeze pattern field lengths
 *  - 5bc727c6: standard formats are keyed by CLDR dateSkeletons
 *  - 2bb017c2: best-fit cache key includes plural category + numbering system
 *  - 98aef602: skeleton caching with mixed standard + skeleton formats
 */
public class SkeletonBestFitTest {

  // March 11, 2018 7:00:25 AM UTC
  private static final long MARCH_11_2018_070025_UTC = 1520751625000L;

  private static final String LA = "America/Los_Angeles";

  // Monday, July 15, 2024 (UTC)
  private static final long JUL_15_2024_UTC = 1721030400000L;

  // Saturday, July 5, 2024 (UTC)
  private static final long JUL_05_2024_UTC = 1720166400000L;

  private static DateSkeletonParser parser() {
    List<Object> flex = Arrays.asList((Object) new DateTimeNode('h', 1));
    return new DateSkeletonParser(flex, flex);
  }

  private static DateSkeleton parse(String s, boolean isPattern) {
    return parser().parse(s, isPattern);
  }

  private static DateSkeleton parse(String s) {
    return parse(s, false);
  }

  /**
   * Port of 6aaf3adf 'matching ties prefer unchanged date field widths'.
   * The best-fit tie breaker ranks candidates that keep the matched
   * skeleton's date-field width class over candidates that merely change
   * it (which would drop the pattern's literals).
   */
  @Test
  public void testMatcherTiesPreferUnchangedDateFieldWidths() {
    DatePatternMatcher<Object> matcher = new DatePatternMatcher<>();

    // Model the standard 'full' format shadowing the available 'yMMMMEd'
    // skeleton: both canonicalize to the same key, but the full pattern
    // carries a long weekday (EEEE) and the full pattern's literals.
    matcher.add(parse("EEEE, MMMM d, y", true), null);
    matcher.add(parse("yMMMEd"), null);
    matcher.add(parse("yMMMd"), null);
    matcher.add(parse("yMd"), null);
    matcher.sort();

    Function<String, String> skel =
        (String s) -> matcher.match(parse(s)).skeleton.skeleton;

    // Long month + short weekday ties between the long-month pattern
    // (weekday width off by one) and the short-month pattern (month width
    // off by one). The long-month pattern must win: adjusting the weekday
    // width keeps the pattern's literals, while widening the month cannot
    // restore them (and can degenerate a named month to digits).
    assertEquals(skel.apply("yMMMMdE"), "yMMMMEd");
    assertEquals(skel.apply("EdMMMMy"), "yMMMMEd");

    // Short month + short weekday still selects the short-month pattern,
    // as does a numeric month (never the long-month pattern).
    assertEquals(skel.apply("yEEEMMMd"), "yMMMEd");
    assertEquals(skel.apply("yMMEEd"), "yMMMEd");

    // Long weekday matches the long-month pattern exactly.
    assertEquals(skel.apply("yMMMMEEEEd"), "yMMMMEd");
  }

  /**
   * Port of the 'matching field widths' matcher test from skeletons.test.ts.
   */
  @Test
  public void testMatcherMatchingFieldWidths() {
    String[] skeletons = {
        "y", "yM", "yMEd", "yMMM", "yMMMEEEEd", "yMMMEd", "yMMMM",
        "yMMMMEEEEd", "yMMMMEd", "yMMMMd", "yMMMd", "yMd"
    };
    DatePatternMatcher<Object> matcher = new DatePatternMatcher<>();
    for (String s : skeletons) {
      matcher.add(parse(s), null);
    }
    matcher.sort();

    Function<String, String> skel =
        (String s) -> matcher.match(parse(s)).skeleton.skeleton;

    assertEquals(skel.apply("yMEd"), "yMEd");
    assertEquals(skel.apply("yMEEd"), "yMEd");
    assertEquals(skel.apply("yMMEEd"), "yMEd");
    assertEquals(skel.apply("yMMMEEd"), "yMMMEd");
    assertEquals(skel.apply("yMMMEEEd"), "yMMMEd");
    assertEquals(skel.apply("yMMMMEEEEd"), "yMMMMEEEEd");
    // Field width constraints enforced
    assertEquals(skel.apply("yMMMMMMEEEEEEd"), "yMMMEd");
  }

  /**
   * Port of 4a9801b2 'rule 2: pattern field lengths are authoritative when
   * skeleton lengths match' (e6409209). TR35 Matching Skeletons rule 2:
   * when the matched pattern's skeleton field length equals the requested
   * length, the pattern's own field length is preserved.
   */
  @Test
  public void testRule2Freeze() {
    CLDR fr = CLDR.get("fr");
    assertEquals(fmt(fr, JUL_05_2024_UTC, "UTC", opts().skeleton("yMd")), "05/07/2024");
    assertEquals(fmt(fr, JUL_15_2024_UTC, "UTC", opts().skeleton("yMd")), "15/07/2024");

    // 1-digit patterns stay 1-digit (freeze in the narrow direction).
    CLDR en = CLDR.get("en");
    assertEquals(fmt(en, JUL_05_2024_UTC, "UTC", opts().skeleton("yMd")), "7/5/2024");

    // Requested yMMd (month length 2) vs matched yMd (length 1): widen.
    assertEquals(fmt(en, JUL_15_2024_UTC, "UTC", opts().skeleton("yMMd")), "07/15/2024");

    // 2-digit pattern locales keep their digits for yMd.
    assertEquals(fmt(CLDR.get("pl"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMd")), "15.07.2024");
    assertEquals(fmt(CLDR.get("lt"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMd")), "2024-07-15");
    assertEquals(fmt(CLDR.get("sv"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMd")), "2024-07-15");
    assertEquals(fmt(CLDR.get("hu"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMd")), "2024. 07. 15.");
  }

  /**
   * Port of 4a9801b2 'standards are keyed by CLDR dateSkeletons' (5bc727c6):
   * standard-format entries are excluded from the available-format key
   * space so numeric-month available skeletons remain reachable, and
   * long-month best fit picks the pattern carrying the literal context.
   */
  @Test
  public void testStandardKeys() {
    // ja/zh full is y年M月d日EEEE (dateSkeletons: yMMMEEEEd), so available
    // yMEd ("y/M/d(E)" / "y/M/dE") is reachable for numeric-month requests.
    assertEquals(fmt(CLDR.get("ja"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMEd")), "2024/7/15(月)");
    assertEquals(fmt(CLDR.get("zh"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMEd")), "2024/7/15周一");
    // es available yMMMMEd ("EEE, d 'de' MMMM 'de' y") is distance-0 for yMMMMdE.
    assertEquals(fmt(CLDR.get("es"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMMMMdE")), "lun, 15 de julio de 2024");
  }

  /**
   * Port of 6aaf3adf skeleton-icu cases: long month + short weekday must
   * resolve to the long-month pattern (adjusting the weekday width) rather
   * than the short-month pattern, which would drop the literal-carrying
   * month names ('de' in es, 'den' in da).
   */
  @Test
  public void testTieBreakLongMonth() {
    assertEquals(fmt(CLDR.get("es"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMMMMdE")), "lun, 15 de julio de 2024");
    assertEquals(fmt(CLDR.get("da"), JUL_15_2024_UTC, "UTC", opts().skeleton("yMMMMdE")), "man. den 15. juli 2024");
  }

  /**
   * Port of 2bb017c2 'best-fit cache is keyed by plural category': the cache
   * key includes the plural categories of the week fields, so the same raw
   * skeleton resolves per-date.
   */
  @Test
  public void testCacheKeyPluralCategory() {
    CLDR fil = CLDR.get("fil");
    assertEquals(fmt(fil, 1704110400000L, "UTC", opts().skeleton("yw")), "ika-1 linggo ng 2024");
    assertEquals(fmt(fil, 1705953600000L, "UTC", opts().skeleton("yw")), "linggo 4 ng 2024");
  }

  /**
   * Port of 2bb017c2 'best-fit cache is keyed by numbering system': the
   * fractional-second adjustment embeds the numbering system's decimal
   * symbol, so the cache key must include the numbering system name.
   */
  @Test
  public void testCacheKeyNumberingSystem() {
    CLDR ar = CLDR.get("ar");
    long ms = 1721012425123L; // 2024-07-15T03:00:25.123Z
    assertEquals(fmt(ar, ms, "UTC", opts().skeleton("hmsS").numberSystem("latn")), "3:00:25.1 ص");
    assertEquals(fmt(ar, ms, "UTC", opts().skeleton("hmsS").numberSystem("arab")), "٣:٠٠:٢٥٫١ ص");
  }

  /**
   * Port of 98aef602 'skeleton caching: mixed formats and field widths'.
   * Mixed standard + skeleton requests must not leak cached results between
   * key variations, and fractional-second/quarter widths are part of the raw
   * cache key.
   */
  @Test
  public void testMixedCaching() {
    CLDR en = CLDR.get("en");
    CalendarDate date = en.Calendars.toGregorianDate(MARCH_11_2018_070025_UTC, LA);

    // bare time skeleton, then same skeleton mixed with a standard date
    // format; the cached result must not leak between the two
    assertEquals(en.Calendars.formatDate(date, opts().skeleton("Hm")), "23:00");
    assertEquals(en.Calendars.formatDate(date, opts().date(FormatWidthType.SHORT).skeleton("Hm")), "3/10/18, 23:00");

    // bare compound skeleton, then mixed with a standard time format
    assertEquals(en.Calendars.formatDate(date, opts().skeleton("yMMMMEEEEdHm")), "Saturday, March 10, 2018 at 23:00");
    assertEquals(en.Calendars.formatDate(date, opts().time(FormatWidthType.SHORT).skeleton("yMMMMEEEEdHm")),
        "Saturday, March 10, 2018 at 11:00 PM");

    // same fields, different fractional second width
    assertEquals(en.Calendars.formatDate(date, opts().skeleton("hmsS")), "11:00:25.0 PM");
    assertEquals(en.Calendars.formatDate(date, opts().skeleton("hmsSS")), "11:00:25.00 PM");

    // same fields, different quarter width
    assertEquals(en.Calendars.formatDate(date, opts().skeleton("yQQQ")), "Q1 2018");
    assertEquals(en.Calendars.formatDate(date, opts().skeleton("yQQQQ")), "1st quarter 2018");
  }

  private static String fmt(CLDR cldr, long epoch, String zone, DateFormatOptions opts) {
    return cldr.Calendars.formatDate(cldr.Calendars.toGregorianDate(epoch, zone), opts);
  }

  private static DateFormatOptions opts() {
    return DateFormatOptions.build();
  }

}
