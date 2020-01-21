package com.squarespace.cldrengine.calendars;

import java.util.Map;

import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.ContextTransformFieldType;
import com.squarespace.cldrengine.internal.ContextType;
import com.squarespace.cldrengine.internal.FormatWidthType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.internal.StringValue;
import com.squarespace.cldrengine.numbering.NumberParams;

public class CalendarsImpl implements Calendars {

  private static final DateFormatOptions DATE_FORMAT_OPTIONS_DEFAULT = DateFormatOptions
      .builder()
      .date(FormatWidthType.FULL)
      .build();

  private final Bundle bundle;
  private final Internals internals;
  private final PrivateApi privateApi;
  private final CalendarManager manager;

  public CalendarsImpl(Bundle bundle, Internals internals, PrivateApi privateApi) {
    this.bundle = bundle;
    this.internals = internals;
    this.privateApi = privateApi;
    this.manager = new CalendarManager(bundle, internals);
  }

  @Override
  public String formatDate(CalendarDate date, DateFormatOptions options) {
    return this._formatDate(new StringValue(), date, options);
  }

  protected <R> R _formatDate(AbstractValue<R> value, CalendarDate date, DateFormatOptions options) {
    options = options == null ? DATE_FORMAT_OPTIONS_DEFAULT : options;
    CalendarType calendar = this.internals.calendars.selectCalendar(this.bundle, options.calendar);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem, "default");
    DateFormatRequest req = this.manager.getDateFormatRequest(date, options, params);
    CalendarContext<CalendarDate> ctx = this._context(date, params, options.context);
    return this.internals.calendars.formatDateTime(calendar, ctx, value, req.date, req.time, req.wrapper);
  }

  protected <T extends CalendarDate> CalendarContext<T> _context(T date, NumberParams params, ContextType context) {
    Map<ContextTransformFieldType, String> transform = this.privateApi.getContextTransformInfo();
    return new CalendarContext<T>(date, this.bundle, params.system,
        params.latnSystem, context, transform);
  }

}
