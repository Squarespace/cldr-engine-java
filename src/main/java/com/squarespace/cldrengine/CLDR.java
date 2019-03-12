package com.squarespace.cldrengine;

import com.squarespace.cldrengine.calendars.Calendars;
import com.squarespace.cldrengine.calendars.CalendarsImpl;
import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.locale.Locale;

/**
 * Contains all functionality for a given locale.
 */
public class CLDR {

  private final Locale locale;
  private final Bundle bundle;

  public final Locales locales;
  public final Calendars Calendars;

  public CLDR(Locale locale, Bundle bundle) {
    this.locale = locale;
    this.bundle = bundle;
    this.locales = new LocalesImpl(locale, bundle);
    this.Calendars = new CalendarsImpl(bundle);
  }

}
