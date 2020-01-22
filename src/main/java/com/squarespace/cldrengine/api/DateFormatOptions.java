package com.squarespace.cldrengine.api;

import lombok.Builder;
import lombok.ToString;

@Builder(toBuilder = true)
@ToString
public class DateFormatOptions {

  public final FormatWidthType datetime;
  public final FormatWidthType date;
  public final FormatWidthType time;
  public final String skeleton;
  public final FormatWidthType wrap;
  public final CalendarType calendar;
  public final String numberSystem;
  public final ContextType context;

}
