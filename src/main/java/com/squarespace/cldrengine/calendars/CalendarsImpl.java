package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.Internals;

public class CalendarsImpl implements Calendars {

  private final Bundle bundle;
  private final Internals internals;

  public CalendarsImpl(Bundle bundle, Internals internals) {
    this.bundle = bundle;
    this.internals = internals;
  }

  @Override
  public String formatDate(CalendarDate date) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String formatDate(long epochUTC, String zoneId) {
    // TODO Auto-generated method stub
    return null;
  }

  protected <R> R _formatDate(AbstractValue<R> value, CalendarDate date, DateFormatOptions options) {
    CalendarContext<CalendarDate> ctx;
    String calendar = this.internals.calendars.selectCalendar(this.bundle, options.calendar);
    // TODO:

    return null;
  }

}
