package com.squarespace.cldrengine.message;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.MessageArgs;
import com.squarespace.cldrengine.api.MessageFormatFuncMap;
import com.squarespace.cldrengine.api.PluralRules;
import com.squarespace.cldrengine.plurals.Plurals;

/**
 * Engine-level port of the @phensley/messageformat evaluation tests
 * (__tests__/evaluation.test.ts) added by commit 739a9ec0, "Fix
 * messageformat to pass through missing args as literal text".
 *
 * A missing argument (not provided, or provided as null) renders as the
 * literal argument reference text, e.g. `{0}` or `{name}`, and no
 * formatter or plural/select branch is evaluated. Verified against
 * ICU4J 57.1-74.1 and the fixed TS engine.
 */
public class MessageEngineTest {

  private static final PluralRules PLURALS = Plurals.get("en", "");
  private static final DefaultMessageArgConverter CONVERTER = new DefaultMessageArgConverter();
  private static final MessageFormatFuncMap FORMATTERS = new MessageFormatFuncMap();

  private static MessageCode parse(String s) {
    return new MessagePatternParser(FORMATTERS.keySet(), s, false).parse();
  }

  private static String evaluate(MessageCode c, MessageArgs args) {
    return new MessageEngine(PLURALS, CONVERTER, FORMATTERS, c).evaluate(args);
  }

  @Test
  public void testMissingArgumentsPassThroughAsLiteralArgumentReferenceText() {
    MessageCode c;

    c = parse("{0}");
    assertEquals(evaluate(c, new MessageArgs()), "{0}");

    c = parse("hi {0}!");
    assertEquals(evaluate(c, new MessageArgs()), "hi {0}!");

    c = parse("{5}");
    assertEquals(evaluate(c, new MessageArgs()), "{5}");

    c = parse("{0} and {1}");
    assertEquals(evaluate(c, new MessageArgs().add("a")), "a and {1}");

    c = parse("{name}");
    assertEquals(evaluate(c, new MessageArgs()), "{name}");

    c = parse("hi {name}!");
    assertEquals(evaluate(c, new MessageArgs()), "hi {name}!");
    assertEquals(evaluate(c, new MessageArgs().add("name", "Bob")), "hi Bob!");

    // Plural and selectordinal: the formatter is not evaluated at all when the
    // number argument is missing (no `other` fallback, no `#` substitution).
    c = parse("{0, plural, other {#}}");
    assertEquals(evaluate(c, new MessageArgs()), "{0}");

    c = parse("{0, plural, one {one item} other {# items}}");
    assertEquals(evaluate(c, new MessageArgs()), "{0}");
    assertEquals(evaluate(c, new MessageArgs().add(1)), "one item");

    c = parse("{count, plural, offset:1 other {# items}}");
    assertEquals(evaluate(c, new MessageArgs()), "{count}");

    c = parse("{0, selectordinal, one {#st} other {#th}}");
    assertEquals(evaluate(c, new MessageArgs()), "{0}");

    c = parse("{name, select, a {A} other {OTHER}}");
    assertEquals(evaluate(c, new MessageArgs()), "{name}");
    assertEquals(evaluate(c, new MessageArgs().add("name", "a")), "A");

    // A null value is treated the same as a missing argument (ICU4J does the same).
    c = parse("{0}");
    assertEquals(evaluate(c, new MessageArgs().add((Object) null)), "{0}");
  }

  @Test
  public void testHiddenTagsWithMissingArgument() {
    // Port of the "hidden tags" evaluation case updated in 739a9ec0: the
    // visible {0} now renders its literal reference text when missing, while
    // the hidden tags ({-0} and friends) are emitted as text regardless.
    MessageCode c = parse("{0}{a}{-0}{-a}{}{-}{--}{---}");
    assertEquals(evaluate(c, new MessageArgs().add("a", "A")), "{0}A{0}{a}{}{-}{--}");
  }
}
