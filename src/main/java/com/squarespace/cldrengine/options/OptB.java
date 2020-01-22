package com.squarespace.cldrengine.options;

public class OptB extends OptA {

  public final Option<String> name = Option.option();

  protected OptB() {
  }

  protected OptB(OptB b) {
    super(b);
    this.name.set(b.name.get());
  }

  public OptB name(String n) {
    this.name.set(n);
    return this;
  }

  public OptB age(Integer n) {
    this.age.set(n);
    return this;
  }

  public OptB merge(OptB ...args) {
    OptB o = new OptB(this);
    for (OptB x : args) {
      o._merge(x);
    }
    return o;
  }

  protected void _merge(OptB src) {
    this.name.setIf(src.name);
    this.age.setIf(src.age);
  }

//  public static class Builder extends OptA.Builder {
//    protected OptB instance;
//
//    protected Builder() {
//      this.instance = new OptB();
//    }
//
//    protected Builder(OptB b) {
//      this.instance = new OptB(b);
//    }
//
//    public Builder name(String n) {
//      instance.name.set(n);
//      return this;
//    }
//
//    public Builder age(int n) {
//      instance.age.set(n);
//      return this;
//    }
//
//    public OptB build() {
//      return this.instance;
//    }
//  }
//
//  public static Builder builder() {
//    return new Builder();
//  }
//
//  public Builder toBuilder() {
//    return new Builder();
//  }

  public static OptB build() {
    return new OptB();
  }

  public OptB copy() {
    return new OptB(this);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("OptB(");
    this.repr(buf);
    return buf.append(')').toString();
  }

  protected void repr(StringBuilder buf) {
    super.repr(buf);
    buf.append(" name=").append(name);
  }
}
