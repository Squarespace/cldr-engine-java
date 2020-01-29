package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateIntervalFormatOptions;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.TimePeriod;

public class Sketch12 {

  public static void main(String[] args) {
    CLDR cldr = CLDR.get("en");
    DateIntervalFormatOptions options = DateIntervalFormatOptions.build()
        .skeleton("EEEEyMMMd");
    String zoneId = "America/New_York";
//    long epoch = 1513513513535L;
    long epoch = 1520751625000L;
    CalendarDate start = GregorianDate.fromUnixEpoch(epoch, zoneId, 1, 1);
    CalendarDate end;
    String s;

    System.out.println(start.toString());

    end = start.add(TimePeriod.build().year(1.1));
    System.out.println(end.toString());

//    s = cldr.Calendars.formatDateInterval(start, end, options);
//    System.out.println(s);

//    List<Part> parts = cldr.Calendars.formatDateIntervalToParts(start, end, options);
//    for (Part part : parts) {
//      System.out.println(part);
//    }

    end = start.add(TimePeriod.build().day(0.1));
    System.out.println(end.toString());
    s = cldr.Calendars.formatDateInterval(start, end, null);
    System.out.println(s);

    end = start.add(TimePeriod.build().day(0.6));
    s = cldr.Calendars.formatDateInterval(start, end, null);
    System.out.println(s);

    end = start.add(TimePeriod.build().day(0.9));
    s = cldr.Calendars.formatDateInterval(start, end, null);
    System.out.println(s);

    end = start.add(TimePeriod.build().week(0.9));
    s = cldr.Calendars.formatDateInterval(start, end, null);
    System.out.println(s);

    end = start.add(TimePeriod.build().month(0.9));
//    System.out.println(end.toString());
    s = cldr.Calendars.formatDateInterval(start, end, null);
    System.out.println(s);

//    System.exit(1);

    end = start.add(TimePeriod.build().year(2.9));
    s = cldr.Calendars.formatDateInterval(start, end, null);
    System.out.println(s);

    System.out.println("\n---------------\n");

    DateIntervalFormatOptions opts = DateIntervalFormatOptions.build()
        .date("EEEyMMMd").time("Hms");
    end = start.add(TimePeriod.build().day(0.1));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    end = start.add(TimePeriod.build().day(0.4));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    end = start.add(TimePeriod.build().day(0.9));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    end = start.add(TimePeriod.build().day(72.3));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    end = start.add(TimePeriod.build().year(2.9));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    opts.date("yMMM");
    end = start.add(TimePeriod.build().year(2.9));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    opts.date("EEEyMMMdHm");
    end = start.add(TimePeriod.build().day(0.9));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    opts.date("EEEB");
    end = start.add(TimePeriod.build().day(0.9));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    opts.date.clear();
    opts.time("EBhm");
    end = start.add(TimePeriod.build().day(0.3));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

    opts.time("MMMdh");
    end = start.add(TimePeriod.build().day(0.3));
    s = cldr.Calendars.formatDateInterval(start, end, opts);
    System.out.println(s);

  }
}
