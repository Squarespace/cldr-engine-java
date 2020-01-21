package com.squarespace.cldrengine.calendars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.ToString;

public class SkeletonData {

  public static enum Field {
    ERA,
    YEAR,
    QUARTER,
    MONTH,
    WEEK_OF_YEAR,
    WEEK_OF_MONTH,
    WEEKDAY,
    DAY,
    DAY_OF_YEAR,
    DAY_OF_WEEK_IN_MONTH,
    DAYPERIOD,
    HOUR,
    MINUTE,
    SECOND,
    FRACTIONAL_SECOND,
    ZONE,

    MAX_TYPE
  }

  private static final int DELTA = 0x10;
  private static final int NUMERIC = 0x100;
  private static final int NONE = 0;
  private static final int NARROW = -0x101;
  private static final int SHORTER = -0x102;
  private static final int SHORT = -0x103;
  private static final int LONG = -0x104;
  public static final int EXTRA_FIELD = 0x10000;
  public static final int MISSING_FIELD = 0x1000;

  @AllArgsConstructor
  @ToString
  public static class FieldType {
    public final char ch; // pattern character
    public final Field field;
    public final int subfield;
    public final int repeat;
    public final int width;
  }

  private static final FieldType f(char ch, Field field, int type, int width, int repeat) {
    return new FieldType(ch, field, type, width, repeat);
  }

  public static final FieldType[] FIELD_TYPES = new FieldType[] {
      f('G', Field.ERA, SHORT, 1, 3),
      f('G', Field.ERA, LONG, 4, 4),
      f('G', Field.ERA,  NARROW, 5, 5),

      f('y', Field.YEAR, NUMERIC, 1, 20),
      f('Y', Field.YEAR, NUMERIC + DELTA, 1, 20),
      f('u', Field.YEAR, NUMERIC + 2 * DELTA, 1, 20),
      f('r', Field.YEAR, NUMERIC + 3 * DELTA, 1, 20),
      f('U', Field.YEAR, SHORT, 1, 3),
      f('U', Field.YEAR, LONG, 4, 4),
      f('U', Field.YEAR, NARROW, 5, 5),

      f('Q', Field.QUARTER, NUMERIC, 1, 2),
      f('Q', Field.QUARTER, SHORT, 3, 3),
      f('Q', Field.QUARTER, LONG, 4, 4),
      f('Q', Field.QUARTER, NARROW, 5, 5),
      f('q', Field.QUARTER, NUMERIC + DELTA, 1, 2),
      f('q', Field.QUARTER, SHORT - DELTA, 3, 3),
      f('q', Field.QUARTER, LONG - DELTA, 4, 4),
      f('q', Field.QUARTER, NARROW - DELTA, 5, 5),

      f('M', Field.MONTH, NUMERIC, 1, 2),
      f('M', Field.MONTH, SHORT, 3, 3),
      f('M', Field.MONTH, LONG, 4, 4),
      f('M', Field.MONTH, NARROW, 5, 5),
      f('L', Field.MONTH, NUMERIC + DELTA, 1, 2),
      f('L', Field.MONTH, SHORT - DELTA, 3, 3),
      f('L', Field.MONTH, LONG - DELTA, 4, 4),
      f('L', Field.MONTH, NARROW - DELTA, 5, 5),
      f('l', Field.MONTH, NUMERIC + DELTA, 1, 1),

      f('w', Field.WEEK_OF_YEAR, NUMERIC, 1, 2),

      f('W', Field.WEEK_OF_MONTH, NUMERIC, 1, 1),

      f('E', Field.WEEKDAY, SHORT, 1, 3),
      f('E', Field.WEEKDAY, LONG, 4, 4),
      f('E', Field.WEEKDAY, NARROW, 5, 5),
      f('E', Field.WEEKDAY, SHORTER, 6, 6),
      f('c', Field.WEEKDAY, NUMERIC + 2 * DELTA, 1, 2),
      f('c', Field.WEEKDAY, SHORT - 2 * DELTA, 3, 3),
      f('c', Field.WEEKDAY, LONG - 2 * DELTA, 4, 4),
      f('c', Field.WEEKDAY, NARROW - 2 * DELTA, 5, 5),
      f('c', Field.WEEKDAY, SHORTER - 2 * DELTA, 6, 6),
      f('e', Field.WEEKDAY, NUMERIC + DELTA, 1, 2),
      f('e', Field.WEEKDAY, SHORT - DELTA, 3, 3),
      f('e', Field.WEEKDAY, LONG - DELTA, 4, 4),
      f('e', Field.WEEKDAY, NARROW - DELTA, 5, 5),
      f('e', Field.WEEKDAY, SHORTER - DELTA, 6, 6),

      f('d', Field.DAY, NUMERIC, 1, 2),
      f('g', Field.DAY, NUMERIC + DELTA, 1, 20),

      f('D', Field.DAY_OF_YEAR, NUMERIC, 1, 3),

      f('F', Field.DAY_OF_WEEK_IN_MONTH, NUMERIC, 1, 1),

      f('a', Field.DAYPERIOD, SHORT, 1, 3),
      f('a', Field.DAYPERIOD, LONG, 4, 4),
      f('a', Field.DAYPERIOD, NARROW, 5, 5),
      f('b', Field.DAYPERIOD, SHORT - DELTA, 1, 3),
      f('b', Field.DAYPERIOD, LONG - DELTA, 4, 4),
      f('b', Field.DAYPERIOD, NARROW - DELTA, 5, 5),
      f('B', Field.DAYPERIOD, SHORT - 3 * DELTA, 1, 3),
      f('B', Field.DAYPERIOD, LONG - 3 * DELTA, 4, 4),
      f('B', Field.DAYPERIOD, NARROW - 3 * DELTA, 5, 5),

      f('H', Field.HOUR, NUMERIC + 10 * DELTA, 1, 2), // 24 hour
      f('k', Field.HOUR, NUMERIC + 11 * DELTA, 1, 2),
      f('h', Field.HOUR, NUMERIC, 1, 2), // 12 hour
      f('K', Field.HOUR, NUMERIC + DELTA, 1, 2),

      f('m', Field.MINUTE, NUMERIC, 1, 2),

      f('s', Field.SECOND, NUMERIC, 1, 2),
      f('A', Field.SECOND, NUMERIC + DELTA, 1, 1000),

      f('S', Field.FRACTIONAL_SECOND, NUMERIC, 1, 1000),

      f('v', Field.ZONE, SHORT - 2 * DELTA, 1, 1),
      f('v', Field.ZONE, LONG - 2 * DELTA, 4, 4),
      f('z', Field.ZONE, SHORT, 1, 3),
      f('z', Field.ZONE, LONG, 4, 4),
      f('Z', Field.ZONE, NARROW - DELTA, 1, 3),
      f('Z', Field.ZONE, LONG - DELTA, 4, 4),
      f('Z', Field.ZONE, SHORT - DELTA, 5, 5),
      f('O', Field.ZONE, SHORT - DELTA, 1, 1),
      f('O', Field.ZONE, LONG - DELTA, 4, 4),
      f('V', Field.ZONE, SHORT - DELTA, 1, 1),
      f('V', Field.ZONE, LONG - DELTA, 2, 2),
      f('V', Field.ZONE, LONG - 1 - DELTA, 3, 3),
      f('V', Field.ZONE, LONG - 2 - DELTA, 4, 4),
      f('X', Field.ZONE, NARROW - DELTA, 1, 1),
      f('X', Field.ZONE, SHORT - DELTA, 2, 2),
      f('X', Field.ZONE, LONG - DELTA, 4, 4),
      f('x', Field.ZONE, NARROW - DELTA, 1, 1),
      f('x', Field.ZONE, SHORT - DELTA, 2, 2),
      f('x', Field.ZONE, LONG - DELTA, 4, 4),
  };

  // Index skeleton field types by character
  public static final Map<Character, List<FieldType>> FIELD_INDEX = new HashMap<>();

  static {
    for (FieldType type : FIELD_TYPES) {
      List<FieldType> entry = FIELD_INDEX.get(type.ch);
      if (entry == null) {
        entry = new ArrayList<>();
        FIELD_INDEX.put(type.ch, entry);
      }
      entry.add(type);
    }
  }

  public static int[] skeletonFields() {
    return new int[Field.values().length];
  }

  public static FieldType getFieldType(char field, int width) {
    List<FieldType> entries = FIELD_INDEX.get(field);
    if (entries == null) {
      return null;
    }
    FieldType best = entries.get(0);
    for (FieldType type : entries) {
      best = type;
      if (type.repeat > width || type.width < width) {
        continue;
      }
      return type;
    }
    return best;
  }

}
