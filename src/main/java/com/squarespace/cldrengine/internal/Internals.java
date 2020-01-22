package com.squarespace.cldrengine.internal;

import com.squarespace.cldrengine.calendars.CalendarInternals;
import com.squarespace.cldrengine.general.GeneralInternals;
import com.squarespace.cldrengine.numbers.NumberInternals;

public class Internals {

  public final SchemaConfig config;
  public final boolean debug;

  public final String checksum;
  public final Schema schema;

  public final CalendarInternals calendars;
  public final GeneralInternals general;
  public final NumberInternals numbers;

  public Internals(SchemaConfig config, String version, boolean debug) {
    this.config = config;
    this.debug = debug;
    this.checksum = config.checksum(version);
    this.schema = Meta.SCHEMA;

    this.calendars = new CalendarInternals(this);
    this.general = new GeneralInternals(this);
    this.numbers = new NumberInternals(this);
  }

}
