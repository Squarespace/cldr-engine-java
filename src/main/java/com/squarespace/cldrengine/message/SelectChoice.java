package com.squarespace.cldrengine.message;

import com.google.gson.JsonArray;

public class SelectChoice implements ToJson {

  protected final String match;
  protected final MessageCode code;

  public SelectChoice(String match, MessageCode code) {
    this.match = match;
    this.code = code;
  }

  public String match() {
    return match;
  }

  public MessageCode code() {
    return code;
  }

  public JsonArray toJson() {
    JsonArray arr = new JsonArray();
    arr.add(match);
    arr.add(code.toJson());
    return arr;
  }
}
