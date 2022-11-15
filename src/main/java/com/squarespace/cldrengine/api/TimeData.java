package com.squarespace.cldrengine.api;

import java.util.Collections;
import java.util.List;

public class TimeData {

  private String preferred;

  private List<String> allowed;

  public TimeData(String preferred, List<String> allowed) {
    this.preferred = preferred;
    this.allowed = Collections.unmodifiableList(allowed);
  }

  public String preferred() {
    return this.preferred;
  }

  public List<String> allowed() {
    return this.allowed;
  }
}
