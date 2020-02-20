package com.squarespace.cldrengine.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class PluralChoice implements ToJson {

  protected final PluralChoiceType type;
  protected final String match;
  protected final MessageCode code;

  public PluralChoice(PluralChoiceType type, String match, MessageCode code) {
    this.type = type;
    this.match = match;
    this.code = code;
  }

  public PluralChoiceType type() {
    return type;
  }

  public String match() {
    return match;
  }

  public MessageCode code() {
    return code;
  }

  public JsonElement toJson() {
    JsonArray arr = new JsonArray();
    arr.add(type.value());
    arr.add(match);
    arr.add(code.toJson());
    return arr;
  }

}
