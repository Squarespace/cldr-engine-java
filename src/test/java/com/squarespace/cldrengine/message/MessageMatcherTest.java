package com.squarespace.cldrengine.message;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class MessageMatcherTest {

  @Test
  public void testNegativeOffset() {
    String msg =  "0 plural offset:-12";
    MessageMatcher.State r = new MessageMatcher.State(0, msg.length());
    MessageMatcher m = new MessageMatcher(new ArrayList<>(), msg);

    List<Object> args = m.arguments(r);
    assertEquals(args.get(0), 0);

    m.spaces(r);
    String name = m.formatter(r);
    assertEquals(name, "plural");

    m.spaces(r);
    int offset = m.pluralOffset(r);
    assertEquals(offset, -12);
  }
}
