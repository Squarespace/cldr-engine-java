package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.parsing.DateTimePattern;

public class Sketch17 {

  public static void main(String[] args) {
    CLDR cldr = CLDR.get("en");

    String s = "";
    long epoch = 0;
    String zoneId = "America/New_York";

    DateTimePattern p1 = DateTimePattern.parse("h");
    DateTimePattern p2 = DateTimePattern.parse("h");

//    DateSkeletonParser parser = new DateSkeletonParser(p1.nodes, p2.nodes);
//    DateSkeleton skel = parser.parse("Bhhmm", false);
//    System.out.println(skel.canonical());

//    System.exit(1);

    DateFormatOptions opts = DateFormatOptions.build()
//        .calendar(CalendarType.JAPANESE)
        .skeleton("Bhhmm")
//        .skeleton("yMMMd")
//        .date(FormatWidthType.SHORT)
//        .datetime(FormatWidthType.SHORT)
//        .time(FormatWidthType.SHORT)
        .context(ContextType.BEGIN_SENTENCE);
    CalendarDate date = cldr.Calendars.toGregorianDate(epoch, zoneId);
    System.out.println(date.toString());
    s = cldr.Calendars.formatDate(date, opts);
    System.out.println(s);
  }
}
