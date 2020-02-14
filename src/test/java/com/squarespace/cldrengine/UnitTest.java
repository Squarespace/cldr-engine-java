package com.squarespace.cldrengine;

import java.util.stream.Collectors;

import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.units.conversion.Factors;
import com.squarespace.cldrengine.units.conversion.UnitConversion;
import com.squarespace.cldrengine.units.conversion.UnitFactors;

public class UnitTest {

  public static void main(String[] args) throws Exception {
    UnitFactors factors;
    UnitConversion r;

//    factors = new UnitFactors(Factors.AREA);
//    r = factors.get(UnitType.SQUARE_CENTIMETER, UnitType.SQUARE_YARD);
//    System.out.println(r.path);
//    System.out.println(r.factors.stream().map(e -> e.toString()).collect(Collectors.toList()));

    factors = new UnitFactors(Factors.DURATION);
    r = factors.get(UnitType.HOUR, UnitType.YEAR);
    System.out.println(r.path);
    System.out.println(r.factors.stream().map(e -> e.toString()).collect(Collectors.toList()));

  }
}
