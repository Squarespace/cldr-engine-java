package com.squarespace.cldrengine.api;


public class TimePeriod {

  public final Option<Double> year = Option.option();
  public final Option<Double> month = Option.option();
  public final Option<Double> week = Option.option();
  public final Option<Double> day = Option.option();
  public final Option<Double> hour = Option.option();
  public final Option<Double> minute = Option.option();
  public final Option<Double> second = Option.option();
  public final Option<Double> millis = Option.option();

  public TimePeriod() {
  }

  public TimePeriod(TimePeriod arg) {
    this.year.set(arg.year);
    this.month.set(arg.month);
    this.week.set(arg.week);
    this.day.set(arg.day);
    this.hour.set(arg.hour);
    this.minute.set(arg.minute);
    this.second.set(arg.second);
    this.millis.set(arg.millis);
  }

  public TimePeriod year(Double arg) {
    this.year.set(arg);
    return this;
  }

  public TimePeriod year(Long arg) {
    this.year.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod year(Integer arg) {
    this.year.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod year(Option<Double> arg) {
    this.year.set(arg);
    return this;
  }

  public TimePeriod month(Double arg) {
    this.month.set(arg);
    return this;
  }

  public TimePeriod month(Long arg) {
    this.month.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod month(Integer arg) {
    this.month.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod month(Option<Double> arg) {
    this.month.set(arg);
    return this;
  }

  public TimePeriod week(Double arg) {
    this.week.set(arg);
    return this;
  }

  public TimePeriod week(Long arg) {
    this.week.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod week(Integer arg) {
    this.week.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod week(Option<Double> arg) {
    this.week.set(arg);
    return this;
  }

  public TimePeriod day(Double arg) {
    this.day.set(arg);
    return this;
  }

  public TimePeriod day(Long arg) {
    this.day.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod day(Integer arg) {
    this.day.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod day(Option<Double> arg) {
    this.day.set(arg);
    return this;
  }

  public TimePeriod hour(Double arg) {
    this.hour.set(arg);
    return this;
  }

  public TimePeriod hour(Long arg) {
    this.hour.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod hour(Integer arg) {
    this.hour.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod hour(Option<Double> arg) {
    this.hour.set(arg);
    return this;
  }

  public TimePeriod minute(Double arg) {
    this.minute.set(arg);
    return this;
  }

  public TimePeriod minute(Long arg) {
    this.minute.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod minute(Integer arg) {
    this.minute.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod minute(Option<Double> arg) {
    this.minute.set(arg);
    return this;
  }

  public TimePeriod second(Double arg) {
    this.second.set(arg);
    return this;
  }

  public TimePeriod second(Long arg) {
    this.second.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod second(Integer arg) {
    this.second.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod second(Option<Double> arg) {
    this.second.set(arg);
    return this;
  }

  public TimePeriod millis(Double arg) {
    this.millis.set(arg);
    return this;
  }

  public TimePeriod millis(Long arg) {
    this.millis.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod millis(Integer arg) {
    this.millis.set(arg == null ? null : arg.doubleValue());
    return this;
  }

  public TimePeriod millis(Option<Double> arg) {
    this.millis.set(arg);
    return this;
  }

  public static TimePeriod build() {
    return new TimePeriod();
  }

  public TimePeriod copy() {
    return new TimePeriod(this);
  }

  public TimePeriod mergeIf(TimePeriod ...args) {
    TimePeriod o = new TimePeriod(this);
    for (TimePeriod arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(TimePeriod o) {
    this.year.setIf(o.year);
    this.month.setIf(o.month);
    this.week.setIf(o.week);
    this.day.setIf(o.day);
    this.hour.setIf(o.hour);
    this.minute.setIf(o.minute);
    this.second.setIf(o.second);
    this.millis.setIf(o.millis);
  }

  public TimePeriod merge(TimePeriod ...args) {
    TimePeriod o = new TimePeriod(this);
    for (TimePeriod arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(TimePeriod o) {
    this.year.set(o.year);
    this.month.set(o.month);
    this.week.set(o.week);
    this.day.set(o.day);
    this.hour.set(o.hour);
    this.minute.set(o.minute);
    this.second.set(o.second);
    this.millis.set(o.millis);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("TimePeriod( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (year.ok()) {
      buf.append("year=").append(year).append(' ');
    }
    if (month.ok()) {
      buf.append("month=").append(month).append(' ');
    }
    if (week.ok()) {
      buf.append("week=").append(week).append(' ');
    }
    if (day.ok()) {
      buf.append("day=").append(day).append(' ');
    }
    if (hour.ok()) {
      buf.append("hour=").append(hour).append(' ');
    }
    if (minute.ok()) {
      buf.append("minute=").append(minute).append(' ');
    }
    if (second.ok()) {
      buf.append("second=").append(second).append(' ');
    }
    if (millis.ok()) {
      buf.append("millis=").append(millis).append(' ');
    }
  }

}
