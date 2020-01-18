package com.squarespace.cldrengine.decimal;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class StringDecimalFormatter implements DecimalFormatter<String> {

  private List<String> parts = new ArrayList<>();

  public void add(String c) {
    this.parts.add(c);
  }

  public String render() {
    StringBuilder buf = new StringBuilder();
    ListIterator<String> iter = this.parts.listIterator(this.parts.size());
    while (iter.hasPrevious()) {
      buf.append(iter.previous());
    }
    return buf.toString();
  }

}
