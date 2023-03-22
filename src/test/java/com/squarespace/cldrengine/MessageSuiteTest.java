package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.MessageArgs;
import com.squarespace.cldrengine.api.MessageFormatFuncMap;
import com.squarespace.cldrengine.api.MessageFormatter;
import com.squarespace.cldrengine.api.MessageFormatterOptions;
import com.squarespace.cldrengine.message.MessageCode;
import com.squarespace.cldrengine.message.MessagePatternParser;

public class MessageSuiteTest extends CoverageSuite {

  @Test
  public void testMessages() throws Exception {
    run("messages");
  }

  protected String upper(List<Object> args, List<String> options) {
    return !args.isEmpty() ? args.get(0).toString().toUpperCase() : "";
  }

  protected void run(String name) throws Exception {
    CLDR en = CLDR.get("en");
    int cases = 0;

    MessageFormatFuncMap formatters = new MessageFormatFuncMap();
    formatters.put("upper", this::upper);

    MessageFormatterOptions options = MessageFormatterOptions.build()
        .formatters(formatters)
        .language("en")
        .region("US");
    MessageFormatter formatter = new MessageFormatter(options);
    Set<String> formatterNames = formatters.keySet();

    try (BufferedReader reader = getTestCase(name)) {
      for (;;) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }

        JsonObject row = JsonParser.parseString(line).getAsJsonObject();
        String type = row.get("type").getAsString();
        switch (type) {
          case "fixed": {
            MessageArgs args = parseArgs(row.get("args"));
            String message = row.get("message").getAsString();
            String expected = row.get("result").getAsString();
            String actual = formatter.format(message, args);
            assertEquals(actual, expected, "FIXED: " + message);
            break;
          }

          case "random": {
            String message = row.get("message").getAsString();
            JsonElement expected = row.get("code");
            MessagePatternParser parser = new MessagePatternParser(formatterNames, message, options.disableEscapes.or(false));
            MessageCode code = parser.parse();
            JsonElement actual = code.toJson();
            assertEquals(actual, expected, "RANDOM: " + message);
            break;
          }
        }

        cases++;
        if ((cases % 100000) == 0) {
          System.out.println(name + " " + cases);
        }

      }
    }

    System.out.println(name + ": " +
        en.Numbers.formatDecimal(new Decimal(cases), null) + " successful cases");
  }

  protected MessageArgs parseArgs(JsonElement elem) {
    JsonArray arr = elem.getAsJsonArray();
    MessageArgs args = MessageArgs.build();
    for (int i = 0; i < arr.size(); i++) {
      JsonElement e = arr.get(i);
      if (e.isJsonPrimitive()) {
        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isNumber()) {
          args.add(e.getAsNumber());
        } else if (p.isString()) {
          args.add(p.getAsString());
        }
      }
    }
    return args;
  }

}
