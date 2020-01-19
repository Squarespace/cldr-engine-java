package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.internal.Bundle;
import com.squarespace.cldrengine.internal.ContextType;
import com.squarespace.cldrengine.numbering.NumberingSystem;

public abstract class CalendarContext<T extends CalendarDate> {

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
  ContextTransformInfo transform;

}
