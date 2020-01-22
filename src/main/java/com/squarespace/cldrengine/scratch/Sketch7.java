package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;

public class Sketch7 {

  public static void main(String[] args) {
    String zoneId = "America/New_York";
    CalendarDate start = GregorianDate.fromUnixEpoch(1579634069000L, zoneId, 1, 1);
    CalendarDate end = start.add(TimePeriod.builder().month(1).day(10).build());
    System.out.println(start);
    System.out.println(end);
  }
}
