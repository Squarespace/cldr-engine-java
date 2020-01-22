package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.calendars.DayPeriodRules;

public class DayPeriodTest {

  public static void main(String[] args) {
    String id = "en";
    CLDR cldr = CLDR.get(id);
    Bundle bundle = cldr.General.bundle();

    DayPeriodRules rules = new DayPeriodRules();
    for (long minutes : new long[] { 3600L, 1440L }) {
      String rule = rules.get(bundle, minutes);
      System.out.println(rule);
    }
  }

}
