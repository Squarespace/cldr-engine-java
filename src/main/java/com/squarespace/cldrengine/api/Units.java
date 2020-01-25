package com.squarespace.cldrengine.api;

import java.util.List;

public interface Units {

  /**
   * Returns an array of available units.
   */
  List<UnitType> availableUnits();

  /**
   * Formats the given unit quantity to string.
   */
  String formatQuantity(Quantity qty, UnitFormatOptions options);

  /**
   * Formats the given unit quantity to an array of parts.
   */
  List<Part> formatQuantityToParts(Quantity qty, UnitFormatOptions options);

  /**
   * Formats the given unit quantity sequence to string.
   */
  String formatQuantitySequence(List<Quantity> qty, UnitFormatOptions options);

  /**
   * Formats the given unit quantity sequence to an array of parts.
   */
  List<Part> formatQuantitySequenceToParts(List<Quantity> qty, UnitFormatOptions options);

  /**
   * Returns the display name for the given unit.
   */
  String getUnitDisplayName(UnitType unit, UnitLength length);

}
