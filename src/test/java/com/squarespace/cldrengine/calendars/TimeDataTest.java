package com.squarespace.cldrengine.calendars;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.DateFormatOptions;
import com.squarespace.cldrengine.api.TimeData;

public class TimeDataTest {

  @Test
  public void testTimeData() {
    TimeData t;

    final CLDR en = CLDR.get("en-US");
    CalendarDate date = en.Calendars.toGregorianDate(1579633019000L, "America/New_York");

    t = en.Calendars.timeData();
    assertEquals(t.preferred(), "h");
    assertEquals(t.allowed(), Arrays.asList("h", "hb", "H", "hB"));

    assertEquals(en.Calendars.formatDate(date, DateFormatOptions.build().skeleton(t.preferred())), "1 PM");
    assertEquals(t.allowed().stream()
        .map(skeleton -> en.Calendars.formatDate(date, DateFormatOptions.build().skeleton(skeleton)))
        .collect(Collectors.toList()), Arrays.asList("1 PM", "1 PM", "13", "1 in the afternoon"));


    final CLDR es = CLDR.get("es-ES");

    t = es.Calendars.timeData();
    assertEquals(t.preferred(), "H");
    assertEquals(t.allowed(), Arrays.asList("H", "hB", "h", "hb"));

    assertEquals(es.Calendars.formatDate(date, DateFormatOptions.build().skeleton(t.preferred())), "13");
    assertEquals(t.allowed().stream()
        .map(skeleton -> es.Calendars.formatDate(date, DateFormatOptions.build().skeleton(skeleton)))
        .collect(Collectors.toList()), Arrays.asList("13", "1 de la tarde", "1 p. m.", "1 p. m."));
}
}
