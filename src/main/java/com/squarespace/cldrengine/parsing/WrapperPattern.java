package com.squarespace.cldrengine.parsing;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WrapperPattern {

  public List<Object> nodes;

  /**
   * Parse a generic wrapper pattern.
   * Example:  "{1} at {0}"
   */
  public static WrapperPattern parse(String raw) {
    List<Object> nodes = new ArrayList<>();
    int len = raw.length();
    StringBuilder buf = new StringBuilder();
    boolean inquote = false;
    boolean intag = false;
    int i = 0;

    while (i < len) {
      char ch = raw.charAt(i);
      switch (ch) {
        case '{':
          if (buf.length() > 0) {
            nodes.add(buf.toString());
            buf = new StringBuilder();
          }
          intag = true;
          break;

        case '}':
          intag = false;
          break;

        case '\'':
          if (inquote) {
            inquote = false;
          } else {
            inquote = true;
          }
          break;

         default:
           if (intag) {
             // Index doesn't exceed single digits
             nodes.add(Integer.valueOf("" + ch));
           } else {
             buf.append(ch);
           }
           break;
      }
      i++;
    }
    if (buf.length() > 0) {
      nodes.add(buf.toString());
    }
    return new WrapperPattern(nodes);
  }

}
