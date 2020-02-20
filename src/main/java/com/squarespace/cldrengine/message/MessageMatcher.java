package com.squarespace.cldrengine.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.squarespace.cldrengine.utils.StringUtils;

import lombok.AllArgsConstructor;

/**
 * Matches against a substring defined by the [start, end) range
 * argument. When a match occurs it updates the range's start pointer. This
 * allows a single matcher instance to be used to match positions recursively.
 *
 * For example, while the outer block is being parsed at [0, 74] the inner
 * block at [24, 73] can be recursively parsed using the same matcher, with
 * the corresponding parse positions maintained in a range object within each
 * stack frame.
 *
 *   "{gender, select, female {guests plural one {her guest} other {her guests}}"
 */
public class MessageMatcher {

  private static final String IDENTIFIER =
      "[^\\u0009-\\u000d \\u0085\\u200e\\u200f\\u2028\\u2029\\u0021-\\u002f\\u003a-\\u0040\\u005b-\\u005e" +
      "\\u0060\\u007b-\\u007e\\u00a1-\\u00a7\\u00a9\\u00ab\\u00ac\\u00ae\\u00b0\\u00b1\\u00b6\\u00bb\\u00bf" +
      "\\u00d7\\u00f7\\u2010-\\u2027\\u2030-\\u203e\\u2041-\\u2053\\u2055-\\u205e\\u2190-\\u245f\\u2500-\\u2775" +
      "\\u2794-\\u2bff\\u2e00-\\u2e7f\\u3001-\\u3003\\u3008-\\u3020\\u3030\\ufd3e\\ufd3f\\ufe45\\ufe46]+";

  private static final Pattern ARG = Pattern.compile("(0[1..9]+|\\d+|" + IDENTIFIER + ")");
  private static final Pattern CHOICE = Pattern.compile("(=\\d+(\\.\\d+)?)|zero|one|two|few|many|other");
  private static final Pattern IDENT = Pattern.compile(IDENTIFIER);
  private static final Pattern OFFSET = Pattern.compile("offset:\\d+");
  private static final Pattern OPTION = Pattern.compile("[^\\s,\\{\\}]+");
  private static final Pattern SPACE = Pattern.compile("[,\\s]+");

  private final String raw;
  private final Pattern _fmt;

  private final Matcher arg;
  private final Matcher choice;
  private final Matcher format;
  private final Matcher ident;
  private final Matcher offset;
  private final Matcher option;
  private final Matcher space;

  public MessageMatcher(Collection<String> formatters, String raw) {
    this.raw = raw;
    if (formatters.stream().anyMatch(s -> StringUtils.isEmpty(s))) {
      throw new IllegalArgumentException("formatter names must not be zero-length");
    }
    this._fmt = Pattern.compile("(plural|select(ordinal)?|" + StringUtils.join(formatters, "|") + ")");
    this.arg = ARG.matcher(raw);
    this.choice = CHOICE.matcher(raw);
    this.ident = IDENT.matcher(raw);
    this.format = this._fmt.matcher(raw);
    this.offset = OFFSET.matcher(raw);
    this.option = OPTION.matcher(raw);
    this.space = SPACE.matcher(raw);
  }

  public void debug(String msg, State r) {
    String sub = raw.substring(r.s, r.e);
    System.err.println(
        String.format("%s [%4d, %4d] => %s", msg, r.s, r.e, sub.replaceAll("\\s", " ")));
  }

  public char character(State r) {
    return raw.charAt(r.s);
  }

  public boolean complete(State r) {
    return r.e == r.s;
  }

  public boolean spaces(State r) {
    return this.match(space, r) != null;
  }

  public List<Object> arguments(State r) {
    List<Object> args = null;
    do {
      String arg = this.match(this.arg, r);
      if (arg == null) {
        break;
      }
      Integer num = null;
      if (Character.isDigit(arg.charAt(0))) {
        try {
          num = Integer.parseInt(arg, 10);
        } catch (NumberFormatException e) {
          // fall through, shouldn't happen since regexp would be invalid
        }
      }
      if (args == null) {
        args = new ArrayList<>();
      }
      if (num != null) {
        args.add(num);
      } else {
        args.add(arg);
      }

      // Tuple arguments are separated by a single semicolon
      if (complete(r) || raw.charAt(r.s) != ';') {
        break;
      }
      r.s++;
    } while (!complete(r));
    return args;
  }

  public String identifier(State r) {
    return this.match(this.ident, r);
  }

  public List<String> options(State r) {
    List<String> options = null;
    do {
      this.spaces(r);
      String opt = this.match(this.option, r);
      if (opt == null) {
        break;
      }
      if (options == null) {
        options = new ArrayList<>();
      }
      options.add(opt);
    } while (!complete(r));
    return options;
  }

  public String formatter(State r) {
    return this.match(this.format, r);
  }

  public String pluralChoice(State r) {
    return this.match(this.choice, r);
  }

  public int pluralOffset(State r) {
    int n = 0;
    String m = this.match(this.offset, r);
    if (m != null) {
      String[] parts = m.split(":");
      // This must parse successfully since it is constrained by the regexp match
      n = Integer.parseInt(parts[1], 10);
    }
    return n;
  }

  /**
   * Attempt to match the pattern at the given starting location. If a
   * match is found, move the start pointer and return the string.
   * Otherwise return undefined.
   */
  protected String match(Matcher matcher, State r) {
    matcher.region(r.s, r.e);
    if (matcher.lookingAt()) {
      int start = r.s;
      int end = matcher.end();
      String match = raw.substring(start, end);
      r.s = end;
      return match;
    }
    return null;
  }

  @AllArgsConstructor
  public static class State {
    int s;
    int e;
  }

}
