package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.parsing.DateTimePattern;

class DateFormatRequest {

  public String wrapper;
  public DateTimePattern date;
  public DateTimePattern time;
  public NumberParams params;

}
