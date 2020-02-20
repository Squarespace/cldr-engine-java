package com.squarespace.cldrengine.message;

import java.util.List;

import com.google.gson.JsonArray;

public class MessageSelectCode extends MessageCode {

  protected final List<? extends Object> arguments;
  protected final List<SelectChoice> choices;

  public MessageSelectCode(List<? extends Object> arguments, List<SelectChoice> choices) {
    super(MessageOpType.SELECT);
    this.arguments = arguments;
    this.choices = choices;
  }

  public List<? extends Object> args() {
    return arguments;
  }

  public List<SelectChoice> choices() {
    return choices;
  }

  @Override
  protected void _toJson(JsonArray arr) {
    arr.add(arrayOf(arguments));
    arr.add(arrayOf(choices));
  }
}
