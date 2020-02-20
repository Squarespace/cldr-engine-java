package com.squarespace.cldrengine.message;

import java.util.List;

import com.google.gson.JsonArray;

public class MessageSimpleCode extends MessageCode {

  protected final String name;
  protected final List<? extends Object> arguments;
  protected final List<String> options;

  public MessageSimpleCode(String name, List<? extends Object> arguments, List<String> options) {
    super(MessageOpType.SIMPLE);
    this.name = name;
    this.arguments = arguments;
    this.options = options;
  }

  public String name() {
    return name;
  }

  public List<? extends Object> args() {
    return arguments;
  }

  public List<String> options() {
    return options;
  }

  @Override
  protected void _toJson(JsonArray arr) {
    arr.add(name);
    arr.add(arrayOf(arguments));
    arr.add(arrayOf(options));
  }
}
