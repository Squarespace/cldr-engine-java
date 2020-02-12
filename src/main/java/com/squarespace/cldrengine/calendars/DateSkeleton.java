package com.squarespace.cldrengine.calendars;

import com.squarespace.cldrengine.calendars.SkeletonData.Field;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
class DateSkeleton {

  public int[] type = SkeletonData.skeletonFields();
  public SkeletonField[] info = new SkeletonField[type.length];
  public String skeleton = "";
  public String pattern = null;

  public boolean isDate = false;
  public boolean isTime = false;

  public boolean compound() {
    return isDate && isTime;
  }

  public boolean has(int field) {
    return this.type[field] != 0;
  }

  public int monthWidth() {
    SkeletonField f = this.info[Field.MONTH.ordinal()];
    return f == null ? 0 : f.width;
  }

  /**
   * Split this compound skeleton, removing all time fields and copying
   * them to another skeleton.
   */
  public DateSkeleton split() {
    DateSkeleton r = new DateSkeleton();
    for (int i = Field.DAYPERIOD.ordinal(); i < Field.MAX_TYPE.ordinal(); i++) {
      if (this.type[i] != 0) {
        r.type[i] = this.type[i];
        SkeletonField info = this.info[i];
        if (info != null) {
          r.info[i] = new SkeletonField(info);
        }
        this.type[i] = 0;
        this.info[i] = null;
      }
    }
    this.isTime = false;
    this.skeleton = this.canonical();
    r.isTime = true;
    r.skeleton = r.canonical();
    return r;
  }

  /**
   * Build a canonical representation of the skeleton.
   */
  public String canonical() {
    StringBuilder r = new StringBuilder();
    for (int i = 0; i < Field.MAX_TYPE.ordinal(); i++) {
      SkeletonField info = this.info[i];
      if (info != null) {
        // Skip day period for backwards-compatibility
        if (info.field != 'a') {
          int repeat = info.repeat;
          // Override skeleton repeat for these fields.
          if ("GEzvQ".indexOf(info.field) != -1) {
            repeat = 1;
          }
          for (int j = 0; j < repeat; j++) {
            r.append(info.field);
          }
        }
      }
    }
    return r.toString();
  }

  @AllArgsConstructor
  @ToString
  public static class SkeletonField {

    final char input;
    final char field;
    int width;
    int repeat;

    public SkeletonField(SkeletonField s) {
      this.input = s.input;
      this.field = s.field;
      this.width = s.width;
      this.repeat = s.repeat;
    }
  }

}
