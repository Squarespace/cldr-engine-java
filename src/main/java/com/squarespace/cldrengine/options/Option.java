package com.squarespace.cldrengine.options;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Option<T> {

  private T value;

  public static <T> Option<T> option() {
    return new Option<>(null);
  }

  public static <T> Option<T> option(T value) {
    return new Option<>(value);
  }

  public void set(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  public T or(T defvalue) {
    return value == null ? defvalue : value;
  }

  public boolean ok() {
    return value != null;
  }
}
