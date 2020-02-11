package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.units.conversion.Factors;
import com.squarespace.cldrengine.units.conversion.UnitFactors;

public class Sketch20 {

  public static void main(String[] args) {
    UnitFactors factors = new UnitFactors(Factors.ANGLE);
    for (UnitType u : factors.units) {
      for (UnitType v : factors.units) {
        Rational rat = factors.get(u, v);
        System.out.println(u + " " + v + " -> " + rat.toString());
      }
    }
  }
}
