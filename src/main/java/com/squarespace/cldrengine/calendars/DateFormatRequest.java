package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.numbering.NumberParams;
import com.squarespace.cldrengine.parsing.DateTimePattern;

public class DateFormatRequest {

  public String wrapper;
  public DateTimePattern date;
  public DateTimePattern time;
  public NumberParams params;

}
