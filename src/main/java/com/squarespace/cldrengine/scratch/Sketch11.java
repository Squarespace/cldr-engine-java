package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.api.MessageFormatter;
import com.squarespace.cldrengine.api.MessageFormatterOptions;
import com.squarespace.cldrengine.messageformat.evaluation.MessageArgs;

public class Sketch11 {

  public static void main(String[] args) {
    MessageFormatterOptions options = MessageFormatterOptions.build()
        .language("en").region("US");
    MessageFormatter formatter = new MessageFormatter(options);
    String result = formatter.format("Hi {0}", MessageArgs.build().add("Bob"));
    System.out.println(result);
  }
}
