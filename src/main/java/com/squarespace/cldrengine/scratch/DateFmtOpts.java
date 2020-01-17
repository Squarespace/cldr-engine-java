package com.squarespace.cldrengine.scratch;

import com.squarespace.cldrengine.calendars.CalendarType;
import com.squarespace.cldrengine.calendars.DateFormatOptions;

public class DateFmtOpts {

  public static void main(String[] args) throws Exception {
    DateFormatOptions opts = DateFormatOptions.builder().calendar(CalendarType.GREGORY).build();
    System.out.println(opts.toString());
  }
}
