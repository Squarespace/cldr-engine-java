package com.squarespace.cldrengine.calendars;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class TimePeriod {

  @Builder.Default
  public double year = 0;

  @Builder.Default
  public double month = 0;

  @Builder.Default
  public double week = 0;

  @Builder.Default
  public double day = 0;

  @Builder.Default
  public double hour = 0;

  @Builder.Default
  public double minute = 0;

  @Builder.Default
  public double second = 0;

  @Builder.Default
  public double millis = 0;

  public TimePeriod() {

  }
}
