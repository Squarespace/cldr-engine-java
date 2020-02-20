package com.squarespace.cldrengine.message;

import com.google.gson.JsonArray;

public class MessageArgCode extends MessageCode {

  private final Object argument;

  public MessageArgCode(Object argument) {
    super(MessageOpType.ARG);
    this.argument = argument;
  }

  public Object arg() {
    return argument;
  }

  @Override
  protected void _toJson(JsonArray arr) {
    if (argument instanceof String) {
      arr.add( (String) argument );
    } else {
      arr.add( (Integer) argument );
    }
  }
}
