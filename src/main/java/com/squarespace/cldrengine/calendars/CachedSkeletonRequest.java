package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.parsing.DateTimePattern;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class CachedSkeletonRequest {

  public DateSkeleton dateSkel;
  public DateTimePattern date;
  public DateTimePattern time;

}
