package com.squarespace.cldrengine.internal;

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
    StringBuilder tmp = this.buf;
    String prefix = tmp.substring(0, i);
    String suffix = tmp.substring(i);
    this.buf = new StringBuilder();
    this.buf.append(prefix);
    this.buf.append(tmp);
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

  public String join(String ...str) {
    StringBuilder tmp = new StringBuilder();
    for (String s : str) {
      tmp.append(s);
    }
    return tmp.toString();
  }

  public void wrap(WrapperPattern pattern, String ...args) {
    for (Object node : pattern.nodes) {
      if (node instanceof String) {
        this.add("literal", (String)node);
      } else {
        int i = (int) node;
        if (i < args.length) {
          String arg = args[i];
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
