package com.squarespace.cldrengine.calendars;

import java.util.List;

import com.squarespace.cldrengine.calendars.DateSkeleton.SkeletonField;
import com.squarespace.cldrengine.calendars.SkeletonData.Field;
import com.squarespace.cldrengine.calendars.SkeletonData.FieldType;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.parsing.DateTimePattern.DateTimeNode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class DateSkeletonParser {

  private final List<Object> preferredFlex;
  private final List<Object> allowedFlex;

  public DateSkeleton parse(String skeleton, boolean isPattern) {
    DateSkeleton s = new DateSkeleton();
    this._parse(s, skeleton, isPattern);
    return s;
  }

  private void _parse(DateSkeleton s, String raw, boolean isPattern) {
    int len = raw.length();
    boolean noDayPeriod = false;
    char field = '\0';
    int width = 0;
    boolean inquote = false;
    int i = 0;
    while (i < len) {
      char ch = raw.charAt(i);
      if (inquote) {
        if (ch == '\'') {
          inquote = false;
        }
        i++;
        continue;
      }

      if (ch == '\'') {
        inquote = true;

      } else if (DateTimePattern.DATE_PATTERN_CHARS.contains(ch)) {
        if (ch != field) {
          if (field != '\0') {
            noDayPeriod = this.setDayPeriod(s, field, width, noDayPeriod);
          }
          field = ch;
          width = 1;
        } else {
          width++;
        }
      }

      // Lenient parse.. skip all non-field characters.
      i++;
    }

    // Push the last field
    if (width > 0 && field != '\0') {
      noDayPeriod = this.setDayPeriod(s, field, width, noDayPeriod);
    }

    // Handle some special hour cycle / day period behaviors
    SkeletonField hour = s.info[Field.HOUR.ordinal()];
    SkeletonField dayPeriod = s.info[Field.DAYPERIOD.ordinal()];
    if (noDayPeriod) {
      this.clear(s, Field.DAYPERIOD);

    } else if (hour != null) {
      // If we have a 12-hour-cycle but no dayperiod, add the default.
      if (hour.field == 'h' || hour.field == 'K') {
        if (dayPeriod == null) {
          // Add the default day period
          FieldType row = SkeletonData.FIELD_INDEX.get('a').get(0);
          s.type[Field.DAYPERIOD.ordinal()] = row.subfield;
          s.info[Field.DAYPERIOD.ordinal()] = new SkeletonField('a', 'a', row.repeat, row.repeat);
        }
      } else if (dayPeriod != null) {
        this.clear(s, Field.DAYPERIOD);
      }
    }

    s.skeleton = isPattern ? s.canonical() : raw;
    if (isPattern) {
      s.pattern = raw;
    }
  }

  private boolean setDayPeriod(DateSkeleton s, char field, int width, boolean noDayPeriod) {
    if ("jJC".indexOf(field) != -1) {
      noDayPeriod = field == 'J';
      this.setMeta(s, field);
    } else {
      this.set(s, field, field, width);
    }
    return noDayPeriod;
  }

  private void setMeta(DateSkeleton s, char field) {
    List<Object> meta = field == 'C' ? this.allowedFlex : this.preferredFlex;
    for (Object n : meta) {
      if (!(n instanceof String)) {
        DateTimeNode node = (DateTimeNode)n;
        this.set(s, field, node.field, node.width);
      }
    }
  }

  private void set(DateSkeleton s, char input, char field, int width) {
    FieldType type = SkeletonData.getFieldType(field, width);
    if (type != null) {
      this.index(s, input, field, width, type);
    }
  }

  private void clear(DateSkeleton s, Field field) {
    s.type[field.ordinal()] = 0;
    s.info[field.ordinal()] = null;
  }

  private void index(DateSkeleton s, char input, char field, int width, FieldType ft) {
    int idx = ft.field.ordinal();
    s.type[idx] = ft.subfield + (ft.subfield > 0 ? width : 0);
    s.info[idx] = new SkeletonField(input, field, width, ft.repeat);
    s.isDate = s.isDate || idx < Field.DAYPERIOD.ordinal();
    s.isTime = s.isTime || idx >= Field.DAYPERIOD.ordinal();
  }

  public static void main(String[] args) {
    DateTimePattern p = DateTimePattern.parse("H");
    DateSkeletonParser parser = new DateSkeletonParser(p.nodes, p.nodes);
    parser.parse("EEEEyMMd", false);
  }
}
