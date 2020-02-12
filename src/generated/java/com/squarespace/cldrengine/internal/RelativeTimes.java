package com.squarespace.cldrengine.internal;



public class RelativeTimes {

  public final RelativeTimeFields wide;
  public final RelativeTimeFields short_;
  public final RelativeTimeFields narrow;

  public RelativeTimes(
      RelativeTimeFields wide,
      RelativeTimeFields short_,
      RelativeTimeFields narrow) {
    this.wide = wide;
    this.short_ = short_;
    this.narrow = narrow;
  }

}
