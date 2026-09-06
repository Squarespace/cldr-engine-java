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
 * TS v1.15.0 reads one character past the end of the string in an
 * unterminated quoted run (raw[len] is {@code undefined}), and
 * {@code buf += undefined} appends the literal text "undefined" to the
 * parse buffer. The Java port previously called {@code raw.charAt(i)} past
 * the end and threw StringIndexOutOfBoundsException; it now mirrors the TS
 * artifact for byte-for-byte parity (quirks included).
 *
 * Note: {@code parse()} returns [positive, negative]; for a single-pattern
 * input the positive pattern is the parsed result at index 0 and index 1 is
 * derived from it with a MINUS node prepended.
 */
public class NumberPatternParserTest {

  @Test
  public void testTrailingQuoteMirrorsTsUndefinedArtifact() {
    // Previously: StringIndexOutOfBoundsException: Index 9 out of bounds for length 9
    // TS v1.15.0: the unterminated quote reads raw[len] (undefined) and the
    // "undefined" text is pushed as a literal node.
    NumberPattern[] p = NumberPatternParser.parse("#,##0.00'");
    assertNotNull(p);
    assertEquals(p[0].minInt, 1);
    assertEquals(p[0].minFrac, 2);
    assertEquals(p[0].maxFrac, 2);
    assertEquals(p[0].nodes, Arrays.asList(Field.NUMBER, "undefined"));
  }

  @Test
  public void testUnterminatedLeadingQuoteMirrorsTsUndefinedArtifact() {
    // Previously: StringIndexOutOfBoundsException on the final charAt(len)
    NumberPattern p = NumberPatternParser.parse("'unterminated#,##0.00")[0];
    assertNotNull(p);
    // The quoted run swallows the rest of the pattern as literal text, and
    // the out-of-bounds read appends the literal "undefined" (TS artifact).
    assertEquals(p.nodes, Arrays.asList("unterminated#,##0.00undefined"));
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
