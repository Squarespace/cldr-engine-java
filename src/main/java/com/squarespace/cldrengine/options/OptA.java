package com.squarespace.cldrengine.options;

public class OptA {

  public final Option<Integer> age = Option.option();

  protected OptA() {
  }

  protected OptA(OptA a) {
    this.age.set(a.age);
  }

  public OptA age(Integer n) {
    this.age.set(n);
    return this;
  }

  public OptA merge(OptA ...args) {
    OptA o = new OptA(this);
    for (OptA x : args) {
      o._merge(x);
    }
    return o;
  }

  protected void _merge(OptA src) {
    this.age.setIf(src.age);
  }

  public static OptA build() {
    return new OptA();
  }

  public OptA copy() {
    return new OptA(this);
  }

//  public static class Builder {
//    protected final OptA instance;
//
//    protected Builder() {
//      this.instance = new OptA();
//    }
//
//    protected Builder(OptA a) {
//      this.instance = new OptA(a);
//    }
//
//    public Builder age(int n) {
//      instance.age.set(n);
//      return this;
//    }
//
//    public OptA build() {
//      return this.instance;
//    }
//  }

//  public static Builder builder() {
//    return new Builder();
//  }
//
//  public Builder toBuilder() {
//    return new Builder();
//  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("OptA(");
    this.repr(buf);
    return buf.append(')').toString();
  }

  protected void repr(StringBuilder buf) {
    buf.append("age=").append(age);
  }
}
