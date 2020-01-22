package com.squarespace.cldrengine.options;

import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public class FooOptions {

  public Optional<Integer> num = Optional.empty();

  public void num(Integer value) {
    this.num = Optional.of(value);
  }

  public static void main(String[] args) {
//    FooOptions.builder().
  }

}
