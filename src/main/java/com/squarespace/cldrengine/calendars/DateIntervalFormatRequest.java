package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.parsing.DateTimePattern;

import lombok.ToString;

@ToString
class DateIntervalFormatRequest {

  public DateTimePattern date;
  public DateTimePattern time;
  public DateTimePattern range;
  public String skeleton;
  public NumberParams params;
  public String wrapper;

}
