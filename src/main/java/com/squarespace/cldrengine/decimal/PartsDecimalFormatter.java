package com.squarespace.cldrengine.decimal;

import java.util.ArrayList;
import java.util.List;

import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.utils.StringUtils;

public class PartsDecimalFormatter implements DecimalFormatter<List<Part>> {

  private Part[] parts = new Part[16];
  private int size = 0;
  private List<String> curr = new ArrayList<>();
  private String decimal;
  private String group;

  public PartsDecimalFormatter(String decimal, String group) {
    this.decimal = decimal;
    this.group = group;
  }

  public void add(String c) {
    if (this.decimal.equals(c)) {
      this.addPart(new Part("fraction", this.current()));
      this.addPart(new Part("decimal", c));
      this.curr = new ArrayList<>();
    } else if (this.group.equals(c)) {
      this.addPart(new Part("integer", this.current()));
      this.addPart(new Part("group", c));
      this.curr = new ArrayList<>();
    } else {
      this.curr.add(c);
    }
  }

  public List<Part> render() {
    if (!this.curr.isEmpty()) {
      this.addPart(new Part("integer", this.current()));
    }
    List<Part> copy = new ArrayList<>(this.size);
    for (int i = this.size - 1; i >= 0; i--) {
      copy.add(this.parts[i]);
    }
    return copy;
  }

  private void addPart(Part p) {
    if (this.size == this.parts.length) {
      Part[] next = new Part[this.parts.length * 2];
      System.arraycopy(this.parts, 0, next, 0, this.size);
      this.parts = next;
    }
    this.parts[this.size++] = p;
  }

  private String current() {
    return StringUtils.reverseJoin(this.curr, "");
  }
}
