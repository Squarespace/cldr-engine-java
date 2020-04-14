package com.squarespace.cldrengine.calendars;

import java.util.Map;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CalendarDate;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.DateFormatAltOptions;
import com.squarespace.cldrengine.numbers.NumberingSystem;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class CalendarContext<T extends CalendarDate> {

  /**
   * Calendar-specific date
   */
  T date;

  /**
   * Resource bundle for accessing strings
   */
  Bundle bundle;

  // TODO: number params

  /**
   * Numbering system for formatting decimal numbers into strings
   */
  NumberingSystem system;

  /**
   * Latin decimal digit numbering system.
   */
  NumberingSystem latnSystem;

  /**
   * Context in which we're formatting.
   */
  ContextType context;

  /**
   * Context transform info.
   */
  Map<ContextTransformFieldType, String> transform;


  /**
   * Alt formatting options.
   */
  DateFormatAltOptions alt;
}
