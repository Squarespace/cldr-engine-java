package com.squarespace.cldrengine.options;

public class Test {

  public static void main(String[] args) {
    OptA a = OptA.build().age(1);
    System.out.println(a);

    OptB b = OptB.build().age(1).name("foo").age(2);
    System.out.println(b);

    b = b.copy().age(3).name("bar").age(4);
    System.out.println(b);

    b = OptB.build();
    System.out.println(b.age.or(123));

    b = OptB.build().age(2);
    OptB c = OptB.build();
    c = b.merge(c);
    System.out.println(c);

    OptB d = OptB.build().age(5).name("temp");
    c = c.merge(d);
    System.out.println(c);

    OptB e = OptB.build().age(1000).name("final");
    c = c.merge(d, e);
    System.out.println(c);

  }

}
