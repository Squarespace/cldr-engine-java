package com.squarespace.cldrengine.internal;

import java.util.ArrayList;
import java.util.List;

import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.parsing.WrapperPattern;

public class PartsValue implements AbstractValue<List<Part>> {

  private ArrayList<Part> parts = new ArrayList<>();

  public int length() {
    return this.parts.size();
  }

  public String get(int i) {
    return i < this.parts.size() ? this.parts.get(i).value : "";
  }

  public void add(String type, String value) {
    this.parts.add(new Part(type, value));
  }

  public void append(List<Part> value) {
    this.parts.addAll(value);
  }

  public void insert(int i, String type, String value) {
    this.parts.add(i, new Part(type, value));
  }

  public List<Part> render() {
    List<Part> p = this.parts;
    this.parts = new ArrayList<>();
    return p;
  }

  public void reset() {
    this.parts = new ArrayList<>();
  }

  public List<Part> join(List<List<Part>> lists) {
    List<Part> res = new ArrayList<>();
    for (List<Part> lst : lists) {
      res.addAll(lst);
    }
    return res;
  }

  public void wrap(WrapperPattern pattern, List<List<Part>> args) {
    for (Object node : pattern.nodes) {
      if (node instanceof String) {
        this.add("literal", (String)node);
      } else {
        int idx = (int) node;
        if (idx < args.size()) {
          List<Part> arg = args.get(idx);
          if (arg != null) {
            for (Part part : arg) {
              this.parts.add(part);
            }
          }
        }
      }
    }
  }

  public List<Part> empty() {
    return new ArrayList<>();
  }
}
