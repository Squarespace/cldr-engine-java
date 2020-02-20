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
