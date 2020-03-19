package com.squarespace.cldrengine.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.squarespace.cldrengine.utils.StringUtils;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class DateTimePattern {

  public final List<Object> nodes;
  public final String raw;

  private static final List<Character> _CHARS = Arrays.asList(
      'G', 'y', 'Y', 'u', 'U', 'r', 'Q', 'q', 'M', 'L', 'l', 'w', 'W', 'd', 'D',
      'F', 'g', 'E', 'e', 'c', 'a', 'b', 'B', 'h', 'H', 'K', 'k', 'j', 'J', 'C',
      'm', 's', 'S', 'A', 'z', 'Z', 'O', 'v', 'V', 'X', 'x');

  public static final Set<Character> DATE_PATTERN_CHARS = new HashSet<>(_CHARS);

  public static DateTimePattern parse(String raw) {
    List<Object> nodes = new ArrayList<>();
    if (StringUtils.isEmpty(raw)) {
      return new DateTimePattern(nodes, raw);
    }
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

  /**
   * Scan the date interval pattern and return the index of the first repeated field.
   */
  public static int intervalPatternBoundary(DateTimePattern pattern) {
    Set<Character> set = new HashSet<>();
    for (int i = 0; i < pattern.nodes.size(); i++) {
      Object node = pattern.nodes.get(i);
      if (node instanceof DateTimeNode) {
        char field = ((DateTimeNode)node).field;
        if (set.contains(field)) {
          return i;
        }
        set.add(field);
      }
    }
    return -1;
  }

  @AllArgsConstructor
  @ToString
  public static class DateTimeNode {
    public final char field;
    public final int width;
  }

}
