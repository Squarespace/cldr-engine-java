package com.squarespace.cldr2.internal;

public class Pair<F, S> {

  public final F _1;
  public final S _2;

  private Pair(F first, S second) {
    this._1 = first;
    this._2 = second;
  }

  public static <F, S> Pair<F, S> of(F first, S second) {
    return new Pair<>(first, second);
  }

}
