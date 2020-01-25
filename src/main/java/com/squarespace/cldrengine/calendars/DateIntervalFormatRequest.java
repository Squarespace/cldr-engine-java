package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.parsing.DateTimePattern;

class DateIntervalFormatRequest {

  public DateTimePattern date;
  public DateTimePattern range;
  public String skeleton;
  public NumberParams params;
  public String wrapper;

}
