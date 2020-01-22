package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.calendars.DayOfWeek;
import com.squarespace.cldrengine.calendars.TimeZoneData;

public class Sketch6 {

  public static void main(String[] args) {
    String newYork = "America/New_York";
    String utc = "Etc/UTC";
    long base = 1324513333333L;
    long day = 86400 * 1000;
    for (String zoneId : new String[] { newYork, utc }) {
      for (int i = 0; i < 1; i++) {
        long epoch = base + ((day * i) * 30);
        GregorianDate date = GregorianDate.fromUnixEpoch(epoch, zoneId, DayOfWeek.SUNDAY, 1);
        System.out.println(date.toString());
        TimeZoneData.zoneInfoFromUTC(zoneId, epoch);
      }
    }
  }


}
