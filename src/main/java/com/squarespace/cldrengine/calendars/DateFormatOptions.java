package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.internal.ContextType;
import com.squarespace.cldrengine.internal.FormatWidthType;
import com.squarespace.cldrengine.internal.NumberSystemCategory;
import com.squarespace.cldrengine.internal.NumberSystemName;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class DateFormatOptions {

  private FormatWidthType datetime;
  private FormatWidthType date;
  private FormatWidthType time;
  private String skeleton;
  private FormatWidthType wrap;
  private CalendarType calendar;
  private NumberSystemName numberSystemName;
  private NumberSystemCategory numberSystemCategory;
  private ContextType context;

}
