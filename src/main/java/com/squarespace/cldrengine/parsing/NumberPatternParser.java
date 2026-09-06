package com.squarespace.cldrengine.parsing;

public class NumberPatternParser {

  private NumberPattern curr = new NumberPattern();
  private StringBuilder buf = new StringBuilder();
  private boolean attached = false;

  /**
   * Return a pair of patterns, positive and negative.
   */
  public static NumberPattern[] parse(String raw) {
    return new NumberPatternParser()._parse(raw);
  }

  /**
   * Return a pair of patterns, positive and negative.
   */
  public NumberPattern[] _parse(String raw) {
    int len = raw.length();

    NumberPattern save = null;
    NumberPattern curr = this.curr;
    boolean ingroup = false;
    boolean indecimal = false;
    int i = 0;

    outer: while (i < len) {
      char ch = raw.charAt(i);
      switch (ch) {
        case '\'':
          // Consume the quoted run. Stop at the end of the string rather than
          // reading one past it, so a trailing unterminated quote cannot throw
          // StringIndexOutOfBoundsException. TS v1.15.0 instead reads raw[len]
          // (undefined) and appends the literal text "undefined" to the buffer;
          // we deliberately treat the terminal quote as the end of the quoted
          // run (the sane lenient behavior).
          i++;
          while (i < len) {
            ch = raw.charAt(i);
            if (ch == '\'') {
              break;
            }
            buf.append(ch);
            i++;
          }
          break;

        case ';':
          // If we encounter more than one pattern separator, bail out.
          if (save != null) {
            break outer;
          }
          this.pushText();
          // Save current pattern and start parsing a new one.
          save = curr;
          curr = new NumberPattern();
          this.curr = curr;
          // Reset state for next parse
          indecimal = false;
          ingroup = false;
          this.attached = false;
          break;

        case '-':
          this.pushText();
          this.curr.nodes.add(NumberPattern.Field.MINUS);
          break;

        case '%':
          this.pushText();
          this.curr.nodes.add(NumberPattern.Field.PERCENT);
          break;

        case '\u00a4':
          this.pushText();
          this.curr.nodes.add(NumberPattern.Field.CURRENCY);
          break;

        case 'E':
          this.pushText();
          this.curr.nodes.add(NumberPattern.Field.EXPONENT);
          break;

        case '+':
          this.pushText();
          this.curr.nodes.add(NumberPattern.Field.PLUS);
          break;

        case '#':
          this.attach();
          if (ingroup) {
            this.curr.priGroup++;
          } else if (indecimal) {
            this.curr.maxFrac++;
          }
          break;

        case ',':
          this.attach();
          if (ingroup) {
            this.curr.secGroup = this.curr.priGroup;
            curr.priGroup = 0;
          } else {
            ingroup = true;
          }
          break;

        case '.':
          ingroup = false;
          this.attach();
          indecimal = true;
          break;

        case '0':
          this.attach();
          if (ingroup) {
            this.curr.priGroup++;
          } else if (indecimal) {
            this.curr.maxFrac++;
            this.curr.minFrac++;
          }
          if (!indecimal) {
            this.curr.minInt++;
          }
          break;

        default:
          this.buf.append(ch);
          break;
      }

      i++;
    }
    this.pushText();
    if (save == null) {
      // Derive positive from negative by prepending a minus node
      save = curr;
      curr = new NumberPattern(save);
      curr.nodes.add(0, NumberPattern.Field.MINUS);
    }
    return new NumberPattern[] { save, curr };
  }

  private void attach() {
    this.pushText();
    if (!this.attached) {
      this.curr.nodes.add(NumberPattern.Field.NUMBER);
      this.attached = true;
    }
  }

  private void pushText() {
    if (this.buf.length() > 0) {
      this.curr.nodes.add(this.buf.toString());
      this.buf = new StringBuilder();
    }
  }
}
