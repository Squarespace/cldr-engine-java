package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.internal.ContextType;
import com.squarespace.cldrengine.internal.NumberSystemCategory;
import com.squarespace.cldrengine.internal.NumberSystemName;

import lombok.Builder;
import lombok.ToString;

@Builder(toBuilder = true)
@ToString
public class DateIntervalFormatOptions {

  private String skeleton;
  private CalendarType calendar;
  private NumberSystemName numberSystemName;
  private NumberSystemCategory numberSystemCategory;
  private ContextType context;

}
