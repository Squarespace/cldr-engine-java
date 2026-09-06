package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

/**
 * The General.messageFormatter factory must be behaviorally identical to
 * constructing a MessageFormatter directly with the same inputs.
 */
public class GeneralMessageFormatterTest {

  private final static CLDR EN = CLDR.get("en");

  @Test
  public void testMessageFormatterPlural() {
    String message = "{0 plural one {# item} other {# items}}";

    MessageFormatter viaGeneral = EN.General.messageFormatter();
    MessageFormatter direct = new MessageFormatter(MessageFormatterOptions.build().language("en"));

    for (int n = 0; n <= 5; n++) {
      MessageArgs args = new MessageArgs().add(n);
      assertEquals(viaGeneral.format(message, args), direct.format(message, args));
    }

    assertEquals(viaGeneral.format(message, new MessageArgs().add(1)), "1 item");
    assertEquals(viaGeneral.format(message, new MessageArgs().add(5)), "5 items");
  }

  @Test
  public void testMessageFormatterSelect() {
    String message = "{gender select female {her bar} male {his foo} they {their baz} other {unknown quux}}";

    MessageFormatter viaGeneral = EN.General.messageFormatter();
    MessageFormatter direct = new MessageFormatter(MessageFormatterOptions.build().language("en"));

    MessageArgs args = new MessageArgs().add("gender", "male");
    assertEquals(viaGeneral.format(message, args), direct.format(message, args));
    assertEquals(viaGeneral.format(message, args), "his foo");

    args = new MessageArgs().add("gender", "zzz");
    assertEquals(viaGeneral.format(message, args), direct.format(message, args));
    assertEquals(viaGeneral.format(message, args), "unknown quux");
  }

  @Test
  public void testMessageFormatterDisableEscapes() {
    String message = "'a' '{a}' {a}";
    MessageArgs args = new MessageArgs().add("a", 123);

    // Escaping enabled by default
    MessageFormatter viaGeneral = EN.General.messageFormatter();
    MessageFormatter direct = new MessageFormatter(MessageFormatterOptions.build().language("en"));
    assertEquals(viaGeneral.format(message, args), direct.format(message, args));
    assertEquals(viaGeneral.format(message, args), "a {a} 123");

    // disableEscapes(true) changes apostrophe handling consistently
    MessageFormatter viaGeneral2 = EN.General
        .messageFormatter(MessageFormatterOptions.build().disableEscapes(true));
    MessageFormatter direct2 = new MessageFormatter(
        MessageFormatterOptions.build().language("en").disableEscapes(true));
    assertEquals(viaGeneral2.format(message, args), direct2.format(message, args));
    assertEquals(viaGeneral2.format(message, args), "'a' '123' 123");
  }

}
