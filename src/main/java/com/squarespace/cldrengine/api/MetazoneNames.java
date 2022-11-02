package com.squarespace.cldrengine.api;


public class MetazoneNames {

  private final MetazoneName long_;
  private final MetazoneName short_;

  public MetazoneNames(MetazoneName long_, MetazoneName short_) {
    this.long_ = long_;
    this.short_ = short_;
  }

  public MetazoneName long_() {
    return long_;
  }

  public MetazoneName short_() {
    return short_;
  }
}
