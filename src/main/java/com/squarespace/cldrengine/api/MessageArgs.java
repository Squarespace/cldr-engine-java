package com.squarespace.cldrengine.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageArgs {

  private final List<Object> positional = new ArrayList<>();
  private final Map<String, Object> named = new HashMap<>();

  /**
   * Arguments can be a mix of named and positional, so the count is
   * the sum of both types.
   */
  public int count() {
    return positional.size() + named.size();
  }

  /**
   * Add a positional argument.
   */
  public MessageArgs add(Object arg) {
    this.positional.add(arg);
    return this;
  }

  /**
   * Add a named argument.
   */
  public MessageArgs add(String name, Object arg) {
    this.named.put(name, arg);
    return this;
  }

  /**
   * Get a positional argument.
   */
  public Object get(int i) {
    return i < positional.size() ? positional.get(i) : null;
  }

  /**
   * Get a named argument.
   */
  public Object get(String name) {
    return named.get(name);
  }

}
