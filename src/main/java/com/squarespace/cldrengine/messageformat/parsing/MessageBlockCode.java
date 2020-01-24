package com.squarespace.cldrengine.messageformat.parsing;

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
    for (MessageCode code : block) {
      JsonArray _code = code.toJson();
      arr.add(_code);
    }
  }

}
