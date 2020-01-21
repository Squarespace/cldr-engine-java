package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.internal.ContextType;
import com.squarespace.cldrengine.internal.FormatWidthType;
import com.squarespace.cldrengine.internal.NumberSystemCategory;
import com.squarespace.cldrengine.internal.NumberSystemName;

import lombok.Builder;
import lombok.ToString;

@Builder(toBuilder = true)
@ToString
public class DateFormatOptions {

  public FormatWidthType datetime;
  public FormatWidthType date;
  public FormatWidthType time;
  public String skeleton;
  public FormatWidthType wrap;
  public CalendarType calendar;
  public NumberSystemName numberSystemName;
  public NumberSystemCategory numberSystemCategory;
  public ContextType context;

}
