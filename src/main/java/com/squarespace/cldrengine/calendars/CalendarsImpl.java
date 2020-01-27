package com.squarespace.cldrengine.calendars;

import static com.squarespace.cldrengine.utils.StringUtils.isEmpty;
import static com.squarespace.cldrengine.utils.TypeUtils.defaulter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.api.BuddhistDate;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.CalendarType;
import com.squarespace.cldrengine.api.Calendars;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateFieldWidthType;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.DateIntervalFormatOptions;
import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.GregorianDate;
import com.squarespace.cldrengine.api.ISO8601Date;
import com.squarespace.cldrengine.api.JapaneseDate;
import com.squarespace.cldrengine.api.Part;
import com.squarespace.cldrengine.api.PersianDate;
import com.squarespace.cldrengine.api.RelativeTimeFieldFormatOptions;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;
import com.squarespace.cldrengine.api.RelativeTimeFormatOptions;
import com.squarespace.cldrengine.api.TimePeriodField;
import com.squarespace.cldrengine.internal.AbstractValue;
import com.squarespace.cldrengine.internal.DateTimePatternFieldType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.PartsValue;
import com.squarespace.cldrengine.internal.PrivateApi;
import com.squarespace.cldrengine.internal.StringValue;
import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.parsing.WrapperPattern;
import com.squarespace.cldrengine.utils.Pair;

public class CalendarsImpl implements Calendars {

  private static final DateFormatOptions DATE_FORMAT_OPTIONS_DEFAULT =
      DateFormatOptions
          .build()
          .date(FormatWidthType.FULL);

  private static final DateIntervalFormatOptions DATE_INTERVAL_OPTIONS_DEFAULT =
      DateIntervalFormatOptions.build()
          .skeleton("yMd");

  private static final RelativeTimeFieldFormatOptions RELATIVE_FIELD_OPTIONS_DEFAULT =
      RelativeTimeFieldFormatOptions.build()
          .width(DateFieldWidthType.WIDE);

  private static final RelativeTimeFormatOptions RELATIVE_OPTIONS_DEFAULT =
      RelativeTimeFormatOptions.build()
          .width(DateFieldWidthType.WIDE)
          .maximumFractionDigits(0)
          .group(true);

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
  public BuddhistDate toBuddhistDate(long epoch, String zoneId) {
    return BuddhistDate.fromUnixEpoch(epoch, zoneId, firstDay, minDays);
  }

  @Override
  public BuddhistDate toBuddhistDate(Date date, String zoneId) {
    return toBuddhistDate(date.getTime(), zoneId);
  }

  @Override
  public BuddhistDate toBuddhistDate(CalendarDate date) {
    return toBuddhistDate(date.unixEpoch(), date.timeZoneId());
  }

  @Override
  public GregorianDate toGregorianDate(long epoch, String zoneId) {
    return GregorianDate.fromUnixEpoch(epoch, zoneId, firstDay, minDays);
  }

  @Override
  public GregorianDate toGregorianDate(Date date, String zoneId) {
    return toGregorianDate(date.getTime(), zoneId);
  }

  @Override
  public GregorianDate toGregorianDate(CalendarDate date) {
    return toGregorianDate(date.unixEpoch(), date.timeZoneId());
  }

  @Override
  public ISO8601Date toISO8601Date(long epoch, String zoneId) {
    return ISO8601Date.fromUnixEpoch(epoch, zoneId, firstDay, minDays);
  }

  @Override
  public ISO8601Date toISO8601Date(Date date, String zoneId) {
    return toISO8601Date(date.getTime(), zoneId);
  }

  @Override
  public ISO8601Date toISO8601Date(CalendarDate date) {
    return toISO8601Date(date.unixEpoch(), date.timeZoneId());
  }

  @Override
  public JapaneseDate toJapaneseDate(long epoch, String zoneId) {
    return JapaneseDate.fromUnixEpoch(epoch, zoneId, firstDay, minDays);
  }

  @Override
  public JapaneseDate toJapaneseDate(Date date, String zoneId) {
    return toJapaneseDate(date.getTime(), zoneId);
  }

  @Override
  public JapaneseDate toJapaneseDate(CalendarDate date) {
    return toJapaneseDate(date.unixEpoch(), date.timeZoneId());
  }

  @Override
  public PersianDate toPersianDate(long epoch, String zoneId) {
    return PersianDate.fromUnixEpoch(epoch, zoneId, firstDay, minDays);
  }

  @Override
  public PersianDate toPersianDate(Date date, String zoneId) {
    return toPersianDate(date.getTime(), zoneId);
  }

  @Override
  public PersianDate toPersianDate(CalendarDate date) {
    return toPersianDate(date.unixEpoch(), date.timeZoneId());
  }

  /**
   * Find the field of visual difference between two dates. For example, the dates "2019-03-31" and "2019-04-01" differ
   * visually in the month field, even though the dates are only 1 day apart.
   *
   * This can be used by applications to select an appropriate skeleton for date interval formatting, e.g. to format
   * "March 31 - April 01, 2019"
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
  public String formatDateInterval(CalendarDate start, CalendarDate end, DateIntervalFormatOptions options) {
    return this._formatInterval(new StringValue(), start, end, options);
  }

  @Override
  public List<Part> formatDateIntervalToParts(CalendarDate start, CalendarDate end, DateIntervalFormatOptions options) {
    return this._formatInterval(new PartsValue(), start, end, options);
  }

  @Override
  public String formatRelativeTimeField(Decimal value, RelativeTimeFieldType field,
      RelativeTimeFieldFormatOptions options) {
    options = defaulter(options, RelativeTimeFieldFormatOptions::build)
        .mergeIf(RELATIVE_FIELD_OPTIONS_DEFAULT);
    Map<ContextTransformFieldType, String> transform = this.privateApi.getContextTransformInfo();
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), null);
    return this.internals.dateFields.formatRelativeTimeField(bundle, value, field, options, params, transform);
  }

  @Override
  public String formatRelativeTime(CalendarDate start, CalendarDate end, RelativeTimeFormatOptions options) {
    options = defaulter(options, RelativeTimeFormatOptions::build)
        .mergeIf(RELATIVE_OPTIONS_DEFAULT);
    Pair<TimePeriodField, Double> res = start.relativeTime(end, options.field.get());
    TimePeriodField field = res._1;
    double amount = res._2;
    if (start.compare(end) == 1) {
      amount *= -1;
    }

    if (field == TimePeriodField.MILLIS) {
      amount /= 1000.0;
      field = TimePeriodField.SECOND;
    }
    RelativeTimeFieldType relField = translateRelativeFieldType(field);
    // See if we can use day of the week formatting
    boolean dayOfWeek = options.dayOfWeek.or(false);
    if (dayOfWeek && relField == RelativeTimeFieldType.WEEK && start.dayOfWeek() == end.dayOfWeek()) {
      long dow = end.dayOfWeek() - 1;
      relField = DOW_FIELDS[(int)dow];
    }
    return formatRelativeTimeField(new Decimal(amount), relField, options);
  }

  @Override
  public List<String> timeZoneIds() {
    return TimeZoneData.zoneIds();
  }

  @Override
  public String resolveTimeZoneId(String zoneId) {
    return TimeZoneData.resolveId(zoneId);
  }

  protected static final RelativeTimeFieldType[] DOW_FIELDS = new RelativeTimeFieldType[] {
      RelativeTimeFieldType.SUN,
      RelativeTimeFieldType.MON,
      RelativeTimeFieldType.TUE,
      RelativeTimeFieldType.WED,
      RelativeTimeFieldType.THU,
      RelativeTimeFieldType.FRI,
      RelativeTimeFieldType.SAT,
  };

  protected RelativeTimeFieldType translateRelativeFieldType(TimePeriodField field) {
    switch (field) {
      case YEAR:
        return RelativeTimeFieldType.YEAR;
      case MONTH:
        return RelativeTimeFieldType.MONTH;
      case WEEK:
        return RelativeTimeFieldType.WEEK;
      case DAY:
        return RelativeTimeFieldType.DAY;
      case HOUR:
        return RelativeTimeFieldType.HOUR;
      case MINUTE:
        return RelativeTimeFieldType.MINUTE;
      case SECOND:
      default:
        return RelativeTimeFieldType.SECOND;
    }
  }

  protected <R> R _formatDate(AbstractValue<R> value, CalendarDate date, DateFormatOptions options) {
    options = defaulter(options, () -> DATE_FORMAT_OPTIONS_DEFAULT);
    CalendarType calendar = this.internals.calendars.selectCalendar(bundle, options.calendar.get());
    date = convertDateTo(calendar, date);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), "default");
    DateFormatRequest req = this.manager.getDateFormatRequest(date, options, params);
    CalendarContext<CalendarDate> ctx = this._context(date, params, options.context.get());
    return this.internals.calendars.formatDateTime(calendar, ctx, value, req.date, req.time, req.wrapper);
  }

  protected <R> R _formatInterval(AbstractValue<R> value, CalendarDate start, CalendarDate end,
      DateIntervalFormatOptions options) {
    options = defaulter(options, DateIntervalFormatOptions::build)
        .mergeIf(DATE_INTERVAL_OPTIONS_DEFAULT);
    CalendarType calendar = this.internals.calendars.selectCalendar(bundle, options.calendar.get());
    DateTimePatternFieldType fieldDiff = this.fieldOfVisualDifference(start, end);
    NumberParams params = this.privateApi.getNumberParams(options.numberSystem.get(), "default");
    DateIntervalFormatRequest req =
        this.manager.getDateIntervalFormatRequest(calendar, start, fieldDiff, options, params);

    if (!isEmpty(req.skeleton)) {
      DateFormatOptions opts = DateFormatOptions.build()
          .calendar(options.calendar)
          .numberSystem(options.numberSystem)
          .skeleton(req.skeleton);
      DateFormatRequest r = this.manager.getDateFormatRequest(start, opts, params);
      CalendarContext<CalendarDate> ctx = this._context(start, params, options.context.get());
      R _start = this.internals.calendars.formatDateTime(calendar, ctx, value, r.date, r.time, r.wrapper);
      ctx.date = end;
      R _end = this.internals.calendars.formatDateTime(calendar, ctx, value, r.date, r.time, r.wrapper);
      WrapperPattern wrapper = this.internals.general.parseWrapper(req.wrapper);
      value.wrap(wrapper, Arrays.asList(_start, _end));
      return value.render();
    }

    R _date = null;
    if (req.date != null) {
      CalendarContext<CalendarDate> ctx = this._context(start, params, options.context.get());
      _date = this.internals.calendars.formatDateTime(calendar, ctx, value, req.date, null, null);
    }

    if (req.range != null) {
      CalendarContext<CalendarDate> ctx = this._context(start, params, options.context.get());
      R _range = this.internals.calendars.formatInterval(calendar, ctx, value, end, req.range);
      if (_date == null) {
        return _range;
      }

      // Note: This case is covered in ICU but not mentioned in the CLDR docs. Use the MEDIUM
      // dateTimeFormat to join a common date with a time range.
      // Ticket referencing the discrepancy:
      // https://www.unicode.org/cldr/trac/ticket/11158
      // Docs don't mention this edge case:
      // https://www.unicode.org/reports/tr35/tr35-dates.html#intervalFormats
      CalendarPatterns patterns = this.manager.getCalendarPatterns(calendar.value);
      WrapperPattern wrapper = this.internals.general.parseWrapper(patterns.getWrapperPattern(FormatWidthType.MEDIUM));
      value.wrap(wrapper, Arrays.asList(_range, _date));
      return value.render();
    }

    return _date == null ? value.empty() : _date;
  }

  protected <T extends CalendarDate> CalendarContext<T> _context(T date, NumberParams params, ContextType context) {
    Map<ContextTransformFieldType, String> transform = this.privateApi.getContextTransformInfo();
    return new CalendarContext<T>(date, this.bundle, params.system,
        params.latnSystem, context, transform);
  }

  protected CalendarDate convertDateTo(CalendarType type, CalendarDate date) {
    if (type != null && date.type() != type) {
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
    }
    return date;
  }

}
