package com.squarespace.cldrengine.internal;

import java.util.List;

import com.squarespace.cldrengine.parsing.WrapperPattern;

public class StringValue implements AbstractValue<String> {

  private StringBuilder buf = new StringBuilder();

  public int length() {
    return this.buf.length();
  }

  public String get(int i) {
    return i < this.buf.length() ? "" + this.buf.charAt(i) : "";
  }

  public void add(String _type, String value) {
    this.buf.append(value);
  }

  public void append(String value) {
    this.buf.append(value);
  }

  public void insert(int i, String _type, String value) {
    String prefix = buf.substring(0, i);
    String suffix = buf.substring(i);
    this.buf = new StringBuilder();
    this.buf.append(prefix);
    this.buf.append(value);
    this.buf.append(suffix);
  }

  public String render() {
    String s = this.buf.toString();
    this.buf = new StringBuilder();
    return s;
  }

  public void reset() {
    this.buf = new StringBuilder();
  }

  public String join(List<String> elems) {
    StringBuilder tmp = new StringBuilder();
    for (String s : elems) {
      tmp.append(s);
    }
    return tmp.toString();
  }

  public void wrap(WrapperPattern pattern, List<String> args) {
    for (Object node : pattern.nodes) {
      if (node instanceof String) {
        this.add("literal", (String)node);
      } else {
        int idx = (int) node;
        if (idx < args.size()) {
          String arg = args.get(idx);
          if (arg != null && !arg.isEmpty()) {
            this.buf.append(arg);
          }
        }
      }
    }
  }

  public String empty() {
    return "";
  }

}
