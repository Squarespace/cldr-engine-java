package com.squarespace.cldrengine.decimal;

public class StringDecimalFormatter implements DecimalFormatter<String> {

  private String[] parts = new String[16];
  private int size = 0;

  public void add(String c) {
    if (this.size == this.parts.length) {
      String[] next = new String[this.parts.length * 2];
      System.arraycopy(this.parts, 0, next, 0, this.size);
      this.parts = next;
    }
    this.parts[this.size++] = c;
  }

  public String render() {
    int len = 0;
    for (int i = 0; i < this.size; i++) {
      len += this.parts[i].length();
    }
    StringBuilder buf = new StringBuilder(len);
    for (int i = this.size - 1; i >= 0; i--) {
      buf.append(this.parts[i]);
    }
    return buf.toString();
  }

}
