package com.squarespace.cldrengine.messageformat;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.MessageArgs;
import com.squarespace.cldrengine.api.MessageFormatter;
import com.squarespace.cldrengine.api.MessageFormatterOptions;
import com.squarespace.cldrengine.message.DefaultMessageArgConverter;

public class MessageFormatTest {

  @Test
  public void testMessageFormat() {
    MessageFormatterOptions opts = MessageFormatterOptions.build()
        .converter(new ArgConverter())
        .language("en")
        .region("US");

    MessageFormatter formatter = new MessageFormatter(opts);

    MessageArgs args = new MessageArgs();
    args.add("b1", true);
    args.add("b2", false);
    args.add("b3", null);

    String message = "{b1 select yes {YES} no {NO} hmm {HMM}} " +
        "{b2 select yes {YES} no {NO} hmm {HMM}} " +
        "{b3 select yes {YES} no {NO} hmm {HMM}}";
    String actual = formatter.format(message, args);
    assertEquals(actual, "YES NO HMM");
  }

  @Test
  public void testSelectOrdinal() {
    MessageFormatterOptions opts = MessageFormatterOptions.build()
        .converter(new ArgConverter())
        .language("en")
        .region("US");

    MessageFormatter formatter = new MessageFormatter(opts);

    String actual;
    String message = "{0 selectordinal one{#st} two{#nd} few{#rd} other{#th}}";
    actual = formatter.format(message, new MessageArgs().add("1"));
    assertEquals(actual, "1st");

    actual = formatter.format(message, new MessageArgs().add("2"));
    assertEquals(actual, "2nd");

    actual = formatter.format(message, new MessageArgs().add("3"));
    assertEquals(actual, "3rd");

    actual = formatter.format(message, new MessageArgs().add("4"));
    assertEquals(actual, "4th");
  }

  private static class ArgConverter extends DefaultMessageArgConverter {
    @Override
    public String asString(Object arg) {
      if (arg instanceof Boolean) {
        return ((boolean) arg) ? "yes" : "no";
      }
      if (arg == null) {
        return "hmm";
      }
      return super.asString(arg);
    }
  }

}
