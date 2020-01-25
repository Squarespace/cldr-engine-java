package com.squarespace.cldrengine.calendars;

import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.api.BuddhistDate;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.CalendarType;
import com.squarespace.cldrengine.api.Calendars;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.ISO8601Date;
import com.squarespace.cldrengine.api.JapaneseDate;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.PersianDate;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.PartsValue;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.internal.StringValue;
import com.squarespace.cldrengine.numbers.NumberParams;

public class CalendarsImpl implements Calendars {

  private static final DateFormatOptions DATE_FORMAT_OPTIONS_DEFAULT = DateFormatOptions
      .build()
      .date(FormatWidthType.FULL);

  private final Bundle bundle;
  private final Internals internals;
  private final PrivateApi privateApi;
  private final CalendarManager manager;
  private final int firstDay;
  private final int minDays;

  public CalendarsImpl(Bundle bundle, Internals internals, PrivateApi privateApi) {
    this.bundle = bundle;
    this.internals = internals;
    this.privateApi = privateApi;
    this.manager = new CalendarManager(bundle, internals);
    String region = bundle.region();
    this.firstDay = internals.calendars.weekFirstDay(region);
    this.minDays = internals.calendars.weekMinDays(region);
  }

  @Override
  public BuddhistDate toBuddhistDate(CalendarDate date) {
    return BuddhistDate.fromUnixEpoch(date.unixEpoch(), date.timeZoneId(), this.firstDay, this.minDays);
  }

  @Override
  public GregorianDate toGregorianDate(CalendarDate date) {
    return GregorianDate.fromUnixEpoch(date.unixEpoch(), date.timeZoneId(), this.firstDay, this.minDays);
  }

  @Override
  public ISO8601Date toISO8601Date(CalendarDate date) {
    return ISO8601Date.fromUnixEpoch(date.unixEpoch(), date.timeZoneId(), this.firstDay, this.minDays);
  }

  @Override
  public JapaneseDate toJapaneseDate(CalendarDate date) {
    return JapaneseDate.fromUnixEpoch(date.unixEpoch(), date.timeZoneId(), this.firstDay, this.minDays);
  }

  @Override
  public PersianDate toPersianDate(CalendarDate date) {
    return PersianDate.fromUnixEpoch(date.unixEpoch(), date.timeZoneId(), this.firstDay, this.minDays);
  }

  /**
   * Find the field of visual difference between two dates. For example, the
   * dates "2019-03-31" and "2019-04-01" differ visually in the month field,
   * even though the dates are only 1 day apart.
   *
   * This can be used by applications to select an appropriate skeleton for date
   * interval formatting, e.g. to format "March 31 - April 01, 2019"
   */
  public DateTimePatternFieldType fieldOfVisualDifference(CalendarDate a, CalendarDate b) {

    // Determine calendar type to use for comparison. We use the type for
    // the left argument.
    CalendarType type = a.type();

    // Convert a and b to the same type
    if (b.type() != type) {
      b = convertDateTo(type, b);
    }
    return a.fieldOfVisualDifference(b);
  }

  @Override
  public String formatDate(CalendarDate date, DateFormatOptions options) {
    return this._formatDate(new StringValue(), date, options);
  }

  @Override
  public List<Part> formatDateToParts(CalendarDate date, DateFormatOptions options) {
    return this._formatDate(new PartsValue(), date, options);
  }

  @Override
  public List<String> timeZoneIds() {
    return TimeZoneData.zoneIds();
  }

  @Override
  public String resolveTimeZoneId(String zoneId) {
    return TimeZoneData.resolveId(zoneId);
  }

  protected <R> R _formatDate(AbstractValue<R> value, CalendarDate date, DateFormatOptions options) {
    options = (options == null ? DateFormatOptions.build() : options).mergeIf(DATE_FORMAT_OPTIONS_DEFAULT);
    CalendarType calendar = this.internals.calendars.selectCalendar(this.bundle, options.calendar.get());
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), "default");
    DateFormatRequest req = this.manager.getDateFormatRequest(date, options, params);
    CalendarContext<CalendarDate> ctx = this._context(date, params, options.context.get());
    return this.internals.calendars.formatDateTime(calendar, ctx, value, req.date, req.time, req.wrapper);
  }

  protected <T extends CalendarDate> CalendarContext<T> _context(T date, NumberParams params, ContextType context) {
    Map<ContextTransformFieldType, String> transform = this.privateApi.getContextTransformInfo();
    return new CalendarContext<T>(date, this.bundle, params.system,
        params.latnSystem, context, transform);
  }

  protected CalendarDate convertDateTo(CalendarType type, CalendarDate date) {
    switch (type) {
      case BUDDHIST:
        return this.toBuddhistDate(date);
      case GREGORY:
        return this.toGregorianDate(date);
      case ISO8601:
        return this.toISO8601Date(date);
      case JAPANESE:
        return this.toJapaneseDate(date);
      case PERSIAN:
        return this.toPersianDate(date);
    }
    return date;
  }

}
