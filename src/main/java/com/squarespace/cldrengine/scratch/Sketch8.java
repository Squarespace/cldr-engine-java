package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.api.DecimalFormatOptions;
import com.squarespace.cldrengine.api.DecimalFormatStyleType;
import com.squarespace.cldrengine.api.RoundingModeType;

public class Sketch8 {

  public static void main(String[] args) {
    DecimalFormatOptions o = DecimalFormatOptions.build()
        .style(DecimalFormatStyleType.PERCENT)
        .minimumFractionDigits(3)
        .round(RoundingModeType.HALF_EVEN);

    DecimalFormatOptions o2 = o.copy();
    DecimalFormatOptions o3 = DecimalFormatOptions.build().merge(o2);
    System.out.println(o3);
  }
}
