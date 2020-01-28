package com.squarespace.cldrengine.internal;



public class RelativeTimes {

  public final RelativeTimeFields wide;
  public final RelativeTimeFields narrow;
  public final RelativeTimeFields short_;

  public RelativeTimes(
      RelativeTimeFields wide,
      RelativeTimeFields narrow,
      RelativeTimeFields short_) {
    this.wide = wide;
    this.narrow = narrow;
    this.short_ = short_;
  }

}
