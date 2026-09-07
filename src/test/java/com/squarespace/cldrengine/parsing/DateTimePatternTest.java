package com.squarespace.cldrengine.parsing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.parsing.DateTimePattern.DateTimeNode;

/**
 * Regression tests for the quote handling in {@link DateTimePattern}
 * (parity with the TS port upstream fix that treats '' as an escaped
 * apostrophe per TR35).
 */
public class DateTimePatternTest {

  @Test
  public void testQuotedApostropheEscapes() {
    // TR35: '' inside a quoted section yields one literal apostrophe.
    // ICU4J: yyyy 'o''clock' => 2024 o'clock
    assertNodes("yyyy 'o''clock'",
        node('y', 4), " o'clock");

    // Whole-pattern out-of-quote '' yields one literal apostrophe (ICU parity;
    // ICU4J: '' => ').
    assertNodes("''", "'");

    // Out-of-quote '' between fields is a literal apostrophe, not an empty
    // quoted section (ICU4J: yyyy'' MM => 2024' 03).
    assertNodes("yyyy'' MM",
        node('y', 4), "' ", node('M', 2));

    // A single in-quote apostrophe closes the quote as before; subsequent
    // letters are parsed as pattern fields. Behavior is intentionally lenient
    // (ICU4J rejects this pattern with IllegalArgumentException; we keep
    // parsing and pin the existing output).
    assertNodes("yyyy 'o'clock'",
        node('y', 4), " o", node('c', 1), node('l', 1), "o",
        node('c', 1), node('k', 1), "");

    // A pattern ending in a dangling single quote keeps the existing lenient
    // behavior: the quote opens a run that runs to end of input.
    assertNodes("yyyy'", node('y', 4), "");

    // Unquoted text with an unclosed opening quote still lands in the buffer.
    assertNodes("yyyy 'abc", node('y', 4), " abc");
  }

  /**
   * DateTimeNode has no equals/hashCode, so compare node-by-node.
   */
  private static void assertNodes(String raw, Object... expected) {
    DateTimePattern pattern = DateTimePattern.parse(raw);
    assertEquals(pattern.raw, raw);
    List<Object> nodes = pattern.nodes;
    assertEquals(nodes.size(), expected.length);
    for (int i = 0; i < expected.length; i++) {
      Object exp = expected[i];
      Object act = nodes.get(i);
      if (exp instanceof String) {
        assertTrue(act instanceof String, "node[" + i + "] expected String, got " + act.getClass());
        assertEquals(act, exp);
      } else {
        assertTrue(act instanceof DateTimeNode, "node[" + i + "] expected DateTimeNode, got " + act.getClass());
        DateTimeNode en = (DateTimeNode) exp;
        DateTimeNode an = (DateTimeNode) act;
        assertEquals(an.field, en.field, "node[" + i + "] field");
        assertEquals(an.width, en.width, "node[" + i + "] width");
      }
    }
  }

  private static DateTimeNode node(char field, int width) {
    return new DateTimeNode(field, width);
  }
}
