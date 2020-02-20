package com.squarespace.cldrengine.message;

import java.util.List;

import com.google.gson.JsonArray;

public class MessageCode implements ToJson {

  protected final MessageOpType type;

  public MessageCode(MessageOpType type) {
    this.type = type;
  }

  public JsonArray toJson() {
    JsonArray arr = new JsonArray();
    arr.add(this.type.value());
    _toJson(arr);
    return arr;
  }

  public MessageOpType type() {
    return type;
  }

  protected void _toJson(JsonArray arr) {
    // empty
  }

  protected JsonArray arrayOf(List<?> objects) {
    JsonArray arr = new JsonArray();
    if (objects != null) {
      for (Object obj : objects) {
        if (obj instanceof String) {
          arr.add( (String) obj );
        } else if (obj instanceof Number) {
          arr.add( (Number) obj );
        } else if (obj instanceof ToJson) {
          ToJson json = (ToJson) obj;
          arr.add(json.toJson());
        }
      }
    }
    return arr;
  }
}
