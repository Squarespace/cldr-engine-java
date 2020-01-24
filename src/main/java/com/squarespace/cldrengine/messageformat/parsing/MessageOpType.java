package com.squarespace.cldrengine.messageformat.parsing;

import java.util.HashMap;
import java.util.Map;

public enum MessageOpType {

  TEXT(0),
  ARG(1),
  PLURAL(2),
  SELECT(3),
  BLOCK(4),
  NOOP(5),
  SIMPLE(6),
  ARGSUB(7)
  ;

  private final static Map<Integer, MessageOpType> REVERSE = new HashMap<>();

  static {
    for (MessageOpType m : MessageOpType.values()) {
      REVERSE.put(m.value, m);
    }
  }

  private final int value;

  private MessageOpType(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

  public static MessageOpType fromInt(int i) {
    return REVERSE.get(i);
  }

}
