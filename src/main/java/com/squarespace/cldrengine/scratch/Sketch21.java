package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;

public class Sketch21 {

  public static void main(String[] args) {
    CalendarDate date = GregorianDate.fromUnixEpoch(0, "UTC", 1, 1);
    TimePeriod period = TimePeriod.build().hour(2.2).millis(1.5);
    CalendarDate end = date.subtract(period);
    System.out.println(end.unixEpoch());
  }
}
