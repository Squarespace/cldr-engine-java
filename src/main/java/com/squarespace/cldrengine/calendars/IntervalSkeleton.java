package com.squarespace.cldrengine.calendars;

import java.util.Map;

import com.squarespace.cldrengine.internal.DateTimePatternFieldType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class IntervalSkeleton {

  DateSkeleton skeleton;
  Map<DateTimePatternFieldType, String> patterns;

}
