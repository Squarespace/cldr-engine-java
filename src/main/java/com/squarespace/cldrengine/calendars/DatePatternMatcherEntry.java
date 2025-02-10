package com.squarespace.cldrengine.calendars;

import java.util.Optional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DatePatternMatcherEntry<T> {

  DateSkeleton skeleton;
  Optional<T> data;

  public static final DatePatternMatcherEntry<Object> NONE =
      new DatePatternMatcherEntry<Object>(new DateSkeleton(), Optional.empty());
}
