package com.squarespace.cldrengine.api;

/**
 * Field names usable with {@link CalendarDate#startOf(CalendarDateModFields)}
 * and {@link CalendarDate#endOf(CalendarDateModFields)}.
 *
 * Port of the TS type {@code CalendarDateModFields}.
 */
public enum CalendarDateModFields {
  YEAR, MONTH, DAY, HOUR, MINUTE, SECOND
}
