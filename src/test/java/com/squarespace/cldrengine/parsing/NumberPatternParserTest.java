package com.squarespace.cldrengine.parsing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.parsing.NumberPattern.Field;

/**
 * Regression tests for the quote handling in {@link NumberPatternParser}.
 *
 * TS v1.15.0 reads one character past the end of the string in the quoted
 * run (raw[len] is {@code undefined}), so a trailing or unterminated quote
 * simply appends the literal text "undefined" to the parse buffer. The Java
 * port called {@code raw.charAt(i)} past the end and threw
 * StringIndexOutOfBoundsException. We deliberately treat the end of the
 * string as the end of the quoted run (the sane lenient behavior) instead
 * of mirroring the "undefined" artifact.
 *
 * Note: {@code parse()} returns [positive, negative]; for a single-pattern
 * input the positive pattern is the parsed result at index 0 and index 1 is
 * derived from it with a MINUS node prepended.
 */
public class NumberPatternParserTest {

  @Test
  public void testTrailingQuoteDoesNotThrow() {
    // Previously: StringIndexOutOfBoundsException: Index 9 out of bounds for length 9
    NumberPattern[] p = NumberPatternParser.parse("#,##0.00'");
    assertNotNull(p);
    assertEquals(p[0].minInt, 1);
    assertEquals(p[0].minFrac, 2);
    assertEquals(p[0].maxFrac, 2);
  }

  @Test
  public void testTrailingQuoteMatchesBarePattern() {
    // A trailing quote is handled exactly like the bare pattern (no nodes added).
    NumberPattern[] bare = NumberPatternParser.parse("#,##0.00");
    NumberPattern[] quoted = NumberPatternParser.parse("#,##0.00'");
    assertEquals(quoted[0].nodes, bare[0].nodes);
    assertEquals(quoted[0].minInt, bare[0].minInt);
    assertEquals(quoted[0].minFrac, bare[0].minFrac);
    assertEquals(quoted[0].maxFrac, bare[0].maxFrac);
  }

  @Test
  public void testUnterminatedLeadingQuoteDoesNotThrow() {
    // Previously: StringIndexOutOfBoundsException on the final charAt(len)
    NumberPattern p = NumberPatternParser.parse("'unterminated#,##0.00")[0];
    assertNotNull(p);
    // The quoted run swallows the rest of the pattern as literal text.
    assertEquals(p.nodes, Arrays.asList("unterminated#,##0.00"));
  }

  @Test
  public void testEmbeddedQuoteStillWorks() {
    // Well-formed quoted runs are unchanged: NUMBER field then literal "foo".
    NumberPattern p = NumberPatternParser.parse("0.00'foo'")[0];
    assertEquals(p.nodes, Arrays.asList(Field.NUMBER, "foo"));
    assertEquals(p.minFrac, 2);
  }

  @Test
  public void testEscapedQuotePairIsEmptyRun() {
    // '' is an empty quoted run; identical to the bare pattern.
    NumberPattern[] bare = NumberPatternParser.parse("0.00");
    NumberPattern[] quoted = NumberPatternParser.parse("0.00''");
    assertEquals(quoted[0].nodes, bare[0].nodes);
  }

  @Test
  public void testCommonPatternsDoNotThrow() {
    // Sanity: parse must not throw on these common inputs.
    for (String s : new String[] { "", "#,##0", "0", "#,##0.00;(#,##0.00)", "¤#,##0.00" }) {
      try {
        assertNotNull(NumberPatternParser.parse(s));
      } catch (Exception e) {
        fail("parse(" + s + ") threw: " + e);
      }
    }
  }

}
