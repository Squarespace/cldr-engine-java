package com.squarespace.cldrengine.message;

import com.google.gson.JsonArray;

public class MessageTextCode extends MessageCode {

  private final String text;

  public MessageTextCode(String text) {
    super(MessageOpType.TEXT);
    this.text = text;
  }

  public String text() {
    return this.text;
  }

  @Override
  protected void _toJson(JsonArray arr) {
    arr.add(text);
  }
}
