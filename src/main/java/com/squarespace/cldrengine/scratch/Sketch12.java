package com.squarespace.cldrengine.scratch;

import java.util.Arrays;
import java.util.List;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.Quantity;
import com.squarespace.cldrengine.api.UnitFormatOptions;
import com.squarespace.cldrengine.api.UnitLength;
import com.squarespace.cldrengine.api.UnitType;

public class Sketch12 {

  public static void main(String[] args) {
    CLDR cldr = CLDR.get("en");
    UnitFormatOptions options = UnitFormatOptions.build()
        .length(UnitLength.SHORT);
    Quantity qty = Quantity.build().value(new Decimal("13.4559"))
        .unit(UnitType.METER_PER_SECOND);
    String s = cldr.Units.formatQuantity(qty, options);
    System.out.println(s);

    List<Part> parts = cldr.Units.formatQuantityToParts(qty, options);
    for (Part part : parts) {
      System.out.println(part);
    }

    List<Quantity> seq = Arrays.asList(
        Quantity.build()
          .value(new Decimal("2"))
          .unit(UnitType.MILE),
        Quantity.build()
          .value(new Decimal("17.4"))
          .unit(UnitType.YARD));
    s = cldr.Units.formatQuantitySequence(seq, options);
    System.out.println(s);
  }
}
