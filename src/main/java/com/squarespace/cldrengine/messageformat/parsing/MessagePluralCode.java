package com.squarespace.cldrengine.messageformat.parsing;

import java.util.List;

import com.google.gson.JsonArray;

public class MessagePluralCode extends MessageCode {

  private final List<? extends Object> arguments;
  private final int offset;
  private final PluralNumberType pluralType;
  private final List<PluralChoice> choices;

  public MessagePluralCode(List<? extends Object> arguments, int offset, PluralNumberType pluralType, List<PluralChoice> choices) {
    super(MessageOpType.PLURAL);
    this.arguments = arguments;
    this.offset = offset;
    this.pluralType = pluralType;
    this.choices = choices;
  }

  public List<? extends Object> args() {
    return arguments;
  }

  public int offset() {
    return offset;
  }

  public PluralNumberType pluralType() {
    return pluralType;
  }

  public List<PluralChoice> choices() {
    return choices;
  }

  @Override
  protected void _toJson(JsonArray arr) {
    arr.add(arrayOf(arguments));
    arr.add(offset);
    arr.add(pluralType.value());
    arr.add(arrayOf(choices));
  }
}
