package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.RelativeTimeFieldFormatOptions;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;

public class Sketch14 {

  public static void main(String[] args) {
    CLDR cldr = CLDR.get("en");
    RelativeTimeFieldFormatOptions options = RelativeTimeFieldFormatOptions.build()
        .context(ContextType.BEGIN_SENTENCE)
        .minimumFractionDigits(1);
    String s = cldr.Calendars.formatRelativeTimeField(new Decimal("-5.7"), RelativeTimeFieldType.DAY, options);
    System.out.println(s);
  }
}
