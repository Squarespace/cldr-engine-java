package com.squarespace.cldrengine.calendars;

import java.util.Map;

import com.squarespace.cldrengine.CLDR;
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

  @Override
  public String formatDate(long epochUTC, String zoneId) {
    // TODO Auto-generated method stub
    return null;
  }

  protected <R> R _formatDate(AbstractValue<R> value, CalendarDate date, DateFormatOptions options) {
    CalendarType calendar = this.internals.calendars.selectCalendar(this.bundle, options.calendar);
    System.out.println("calendar " + calendar);
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

  public static void main(String[] args) {
    String id = "en";
    CLDR cldr = CLDR.get(id);
    String zoneId = "America/New_York";
    CalendarDate date = BuddhistDate.fromUnixEpoch(1579634069000L, zoneId, 1, 1);
    System.out.println(date.toString());
    DateFormatOptions options = DateFormatOptions.builder()
        .calendar(CalendarType.BUDDHIST)
        .date(FormatWidthType.FULL).build();
    String r = cldr.Calendars.formatDate(date, options);
    System.out.println(r);
  }


}
