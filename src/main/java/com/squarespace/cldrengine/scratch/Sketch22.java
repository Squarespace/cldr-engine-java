package com.squarespace.cldrengine.scratch;

import static com.squarespace.cldrengine.api.DateFieldWidthType.NARROW;
import static com.squarespace.cldrengine.api.DateFieldWidthType.SHORT;
import static com.squarespace.cldrengine.api.DateFieldWidthType.WIDE;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.RelativeTimeFieldFormatOptions;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;

public class Sketch22 {

  public static void main(String[] args) throws Exception {
    CLDR cldr = CLDR.get("en");
    for (DateFieldWidthType width : new DateFieldWidthType[] { NARROW, SHORT, WIDE }) {
      RelativeTimeFieldFormatOptions opts = RelativeTimeFieldFormatOptions.build();
      opts.numericOnly(true);
      //.width(width);
//      String s = cldr.Calendars.formatRelativeTimeField(new Decimal(-70), RelativeTimeFieldType.SUN, opts);
      String s = cldr.Calendars.formatRelativeTimeField(new Decimal("-0.1"), RelativeTimeFieldType.YEAR, opts);
      System.out.println(s);
    }
  }
}
