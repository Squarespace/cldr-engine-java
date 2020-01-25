package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.parsing.DateTimePattern;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class CachedIntervalRequest {

  public DateTimePattern date;
  public DateTimePattern range;
  public String skeleton;

}
