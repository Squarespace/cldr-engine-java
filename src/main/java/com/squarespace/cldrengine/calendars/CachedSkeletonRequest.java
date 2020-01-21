package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.parsing.DateTimePattern;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CachedSkeletonRequest {

  public DateSkeleton dateSkel;
  public DateTimePattern date;
  public DateTimePattern time;

}
