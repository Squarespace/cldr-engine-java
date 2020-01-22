package com.squarespace.cldrengine.decimal;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.utils.StringUtils;

public class PartsDecimalFormatter implements DecimalFormatter<List<Part>> {

  private List<Part> parts = new ArrayList<>();
  private List<String> curr = new ArrayList<>();
  private String decimal;
  private String group;

  public PartsDecimalFormatter(String decimal, String group) {
    this.decimal = decimal;
    this.group = group;
  }

  public void add(String c) {
    if (this.decimal.equals(c)) {
      this.parts.add(new Part("fraction", this.current()));
      this.parts.add(new Part("decimal", c));
      this.curr = new ArrayList<>();
    } else if (this.group.equals(c)) {
      this.parts.add(new Part("integer", this.current()));
      this.parts.add(new Part("group", c));
      this.curr = new ArrayList<>();
    } else {
      this.curr.add(c);
    }
  }

  public List<Part> render() {
    if (!this.curr.isEmpty()) {
      this.parts.add(new Part("integer", this.current()));
    }
    List<Part> copy = new ArrayList<>(this.parts.size());
    ListIterator<Part> iter = this.parts.listIterator(this.parts.size());
    while (iter.hasPrevious()) {
      copy.add(iter.previous());
    }
    return copy;
  }

  private String current() {
    return StringUtils.reverseJoin(this.curr, "");
  }
}
