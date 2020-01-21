package com.squarespace.cldrengine.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DateTimePattern {

  public final List<Object> nodes;
  public final String raw;

  public static final Set<Character> DATE_PATTERN_CHARS = new HashSet<>(Arrays.asList(
      'G', 'y', 'Y', 'u', 'U', 'r', 'Q', 'q', 'M', 'L', 'l', 'w', 'W', 'd', 'D',
      'F', 'g', 'E', 'e', 'c', 'a', 'b', 'B', 'h', 'H', 'K', 'k', 'j', 'J', 'C',
      'm', 's', 'S', 'A', 'z', 'Z', 'O', 'v', 'V', 'X', 'x'));

  public static DateTimePattern parse(String raw) {
    List<Object> nodes = new ArrayList<>();
    int len = raw.length();

    StringBuilder buf = new StringBuilder();
    char field = '\0';
    int width = 0;
    boolean inquote = false;
    int i = 0;

    while (i < len) {
      char ch = raw.charAt(i);
      if (inquote) {
        if (ch == '\'') {
          inquote = false;
          field = '\0';
        } else {
          buf.append(ch);
        }
        i++;
        continue;
      }

      if (DATE_PATTERN_CHARS.contains(ch)) {
        if (buf.length() > 0) {
          nodes.add(buf.toString());
          buf = new StringBuilder();
        }

        if (ch != field) {
          if (field != '\0') {
            nodes.add(new DateTimeNode(field, width));
          }

          field = ch;
          width = 1;
        } else {
          // Wide the current field.
          width++;
        }
      } else {
        if (field != '\0') {
          nodes.add(new DateTimeNode(field, width));
        }
        field = '\0';
        if (ch == '\'') {
          inquote = true;
        } else {
          buf.append(ch);
        }
      }
      i++;
    }

    // In the final state we'll either have a field+width or
    // some characters in the buf.
    if (width > 0 && field != '\0') {
      nodes.add(new DateTimeNode(field, width));
    } else {
      nodes.add(buf.toString());
    }
    return new DateTimePattern(nodes, raw);
  }

  @AllArgsConstructor
  public static class DateTimeNode {
    public final char field;
    public final int width;
  }

}
