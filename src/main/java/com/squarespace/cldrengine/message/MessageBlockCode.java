package com.squarespace.cldrengine.message;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

public class MessageBlockCode extends MessageCode {

  private final List<MessageCode> block;

  public MessageBlockCode() {
    this(new ArrayList<>());
  }

  public MessageBlockCode(List<MessageCode> block) {
    super(MessageOpType.BLOCK);
    this.block = block;
  }

  public void add(MessageCode code) {
    this.block.add(code);
  }

  public List<MessageCode> block() {
    return block;
  }

  @Override
  protected void _toJson(JsonArray arr) {
    JsonArray outer = new JsonArray();
    for (MessageCode code : block) {
      JsonArray _code = code.toJson();
      outer.add(_code);
    }
    arr.add(outer);
  }

}
