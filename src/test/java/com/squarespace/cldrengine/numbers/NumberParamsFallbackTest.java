package com.squarespace.cldrengine.numbers;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;

/**
 * Regression tests for the latn fallback in NumberParamsCache.buildNumberSystem.
 *
 * TS oracle (cldr-engine v1.15.0): a non-latn numbering system whose standard
 * decimal patterns are empty in a given bundle falls back to that bundle's latn
 * standard pattern to determine grouping sizes. The Java port tested only for
 * `null` (lookups return "" for missing data) and moreover dropped the fallback
 * result, so compact formatting of non-latn systems lost grouping entirely.
 *
 * Every expected string below was verified byte-for-byte against the TS engine
 * at tag v1.15.0 (see the formatDecimal path with {style: short, nu: <system>}).
 */
public class NumberParamsFallbackTest {

  private static final Decimal BIG = new Decimal(999999999999999999L);

  private static String compact(String locale, String system, DecimalFormatStyleType style) {
    DecimalFormatOptions opts = DecimalFormatOptions.build()
        .style(style)
        .numberSystem(system);
    return CLDR.get(locale).Numbers.formatDecimal(BIG, opts);
  }

  /**
   * en + thai: the thai system has no standard patterns in the en bundle, so the
   * latn fallback must supply grouping -> "๑,๐๐๐,๐๐๐T" (was "๑๐๐๐๐๐๐T").
   */
  @Test
  public void testThaiCompactShort() {
    assertEquals(compact("en", "thai", DecimalFormatStyleType.SHORT), "๑,๐๐๐,๐๐๐T");
  }

  @Test
  public void testThaiCompactLong() {
    assertEquals(compact("en", "thai", DecimalFormatStyleType.LONG), "๑,๐๐๐,๐๐๐ trillion");
  }

  /**
   * en + beng: another (bundle, non-latn system) pair with empty standard patterns.
   */
  @Test
  public void testBengCompactShort() {
    assertEquals(compact("en", "beng", DecimalFormatStyleType.SHORT), "১,০০০,০০০T");
  }

  /**
   * th + thai: thai IS th's default numbering system, so th's pack carries its
   * own standard patterns. The fallback must NOT trigger; the grouped output
   * comes from thai's own pattern.
   */
  @Test
  public void testThaiOwnPatternsNoFallback() {
    assertEquals(compact("th", "thai", DecimalFormatStyleType.SHORT), "๑,๐๐๐,๐๐๐T");
  }

  /**
   * bn + beng: beng has its own standard patterns with Indian 2-2-3 grouping
   * ("১০,০০,০০০"). If the fallback were applied (or grouping dropped) this
   * would not match, so it pins the no-fallback behavior for systems that have
   * their own patterns.
   */
  @Test
  public void testBengOwnPatternsIndianGrouping() {
    assertEquals(compact("bn", "beng", DecimalFormatStyleType.SHORT), "১০,০০,০০০\u00A0লা.কো.");
  }

  /**
   * fa + arab: the arab system has empty standard patterns in the fa bundle, so
   * the latn fallback must supply comma grouping -> "١,٠٠٠,٠٠٠ تریلیون".
   */
  @Test
  public void testArabFallbackInPersian() {
    assertEquals(compact("fa", "arab", DecimalFormatStyleType.SHORT), "١,٠٠٠,٠٠٠\u00A0تریلیون");
  }
}
