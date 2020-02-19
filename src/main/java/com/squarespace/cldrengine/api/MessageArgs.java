package com.squarespace.cldrengine.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageArgs {

  private final List<Object> positional = new ArrayList<>();
  private final Map<String, Object> named = new HashMap<>();

  public static MessageArgs build() {
    return new MessageArgs();
  }

  public MessageArgs add(Object arg) {
    this.positional.add(arg);
    return this;
  }

  public MessageArgs add(String name, Object arg) {
    this.named.put(name, arg);
    return this;
  }

  public Object get(int i) {
    return i < positional.size() ? positional.get(i) : null;
  }

  public Object get(String name) {
    return named.get(name);
  }

}
