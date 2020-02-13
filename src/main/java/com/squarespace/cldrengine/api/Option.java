package com.squarespace.cldrengine.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class Option<T> {

  private T value;

  public static <T> Option<T> option() {
    return new Option<>(null);
  }

  public static <T> Option<T> option(T value) {
    return new Option<>(value);
  }

  public void clear() {
    this.value = null;
  }

  public void set(T value) {
    this.value = value;
  }

  public void set(Option<T> opt) {
    this.value = opt.get();
  }

  public void setIf(Option<T> opt) {
    if (!ok()) {
      this.value = opt.get();
    }
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

  @Override
  public String toString() {
    return value == null ? "null" : value.toString();
  }
}
