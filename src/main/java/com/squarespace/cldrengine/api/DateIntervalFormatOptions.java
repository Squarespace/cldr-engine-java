package com.squarespace.cldrengine.api;

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
