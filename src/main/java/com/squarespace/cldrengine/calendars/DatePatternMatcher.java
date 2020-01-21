package com.squarespace.cldrengine.calendars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.calendars.DateSkeleton.SkeletonField;
import com.squarespace.cldrengine.calendars.SkeletonData.Field;
import com.squarespace.cldrengine.calendars.SkeletonData.FieldType;
import com.squarespace.cldrengine.parsing.DateTimePattern;
import com.squarespace.cldrengine.parsing.DateTimePattern.DateTimeNode;

public class DatePatternMatcher {

  // Save some work for exact matches.
  private final Map<String, DateSkeleton> EXACT = new HashMap<>();

  // Array for matching by distances
  private final List<DateSkeleton> ENTRIES = new ArrayList<>();

  public void add(DateSkeleton skeleton, DateTimeNode[] pattern) {
    String key = skeleton.skeleton;
    // Avoid adding patterns with duplicate skeletons
    if (!this.EXACT.containsKey(key)) {
      this.EXACT.put(key, skeleton);
      this.ENTRIES.add(skeleton);
    }
  }

  public DateSkeleton match(DateSkeleton input) {
    DateSkeleton match = this.EXACT.get(input.skeleton);
    if (match != null) {
      return match;
    }

    DateSkeleton best = null;
    int bestDist = Integer.MAX_VALUE;
    for (DateSkeleton entry : this.ENTRIES) {
      int dist = this.getDistance(entry, input, 0);
      if (dist < bestDist) {
        best = entry;
        bestDist = dist;
        if (dist == 0) {
          break;
        }
      }
    }
    return best;
  }

  public DateTimePattern adjust(DateTimePattern pattern, DateSkeleton skeleton, String decimal) {
    List<Object> r = new ArrayList<>();
    for (Object node : pattern.nodes) {
      if (node instanceof String) {
        r.add(node);
        continue;
      }

      DateTimeNode n = (DateTimeNode) node;
      char field = n.field;
      int width = n.width;

      FieldType p = SkeletonData.getFieldType(field, width);
      if (p == null) {
        continue;
      }

      // Adjust field and width to match skeleton below
      char adjfield = field;
      int adjwidth = width;

      Field i = p.field;

      // For hour, minute and second we use the width from the pattern.
      if (i == Field.HOUR || i == Field.MINUTE || i == Field.SECOND) {
        r.add(new DateTimeNode(adjfield, adjwidth));

        // See if skeleton requested fractional seconds and augment the
        // seconds field.
        if (i == Field.SECOND) {
          SkeletonField info = skeleton.info[Field.FRACTIONAL_SECOND.ordinal()];
          if (info != null) {
            r.add(decimal);
            r.add(new DateTimeNode(info.field, info.width));
          }
        }
        continue;
      }

      int ptype = p.subfield;
      int stype = skeleton.type[i.ordinal()];
      // Ensure magnitudes are the same
      if ((ptype < 0 && stype < 0) || (ptype > 0 && stype > 0)) {
        SkeletonField info = skeleton.info[i.ordinal()];
        if (info != null) {
          adjfield = info.field;
          adjwidth = info.width;
        }
      }

      // Metacharacters have already been replaced in the pattern.
      if ("jJC".indexOf(adjfield) != -1) {
        adjfield = field;
      }
      r.add(new DateTimeNode(adjfield, adjwidth));
    }

    // TODO: handle appending missing fields

    return new DateTimePattern(r, "");
  }

  protected int getDistance(DateSkeleton a, DateSkeleton b, int mask) {
    int result = 0;
    for (int i = 0; i < Field.MAX_TYPE.ordinal(); i++) {
      int atype = mask > 0 && (mask & (i << i)) == 0 ? 0 : a.type[i];
      int btype = b.type[i];
      if (atype == btype) {
        continue;
      }
      if (atype == 0) {
        result += SkeletonData.EXTRA_FIELD;
      } else if (btype == 0) {
        result += SkeletonData.MISSING_FIELD;
      } else {
        result += Math.abs(atype - btype);
      }
    }
    return result;
  }
}
