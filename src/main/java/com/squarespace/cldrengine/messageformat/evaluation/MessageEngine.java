package com.squarespace.cldrengine.messageformat.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.MessageArgConverter;
import com.squarespace.cldrengine.api.MessageArgs;
import com.squarespace.cldrengine.api.MessageFormatFunc;
import com.squarespace.cldrengine.api.MessageFormatFuncMap;
import com.squarespace.cldrengine.api.PluralRules;
import com.squarespace.cldrengine.decimal.DecimalConstants;
import com.squarespace.cldrengine.messageformat.parsing.MessageArgCode;
import com.squarespace.cldrengine.messageformat.parsing.MessageBlockCode;
import com.squarespace.cldrengine.messageformat.parsing.MessageCode;
import com.squarespace.cldrengine.messageformat.parsing.MessagePluralCode;
import com.squarespace.cldrengine.messageformat.parsing.MessageSelectCode;
import com.squarespace.cldrengine.messageformat.parsing.MessageSimpleCode;
import com.squarespace.cldrengine.messageformat.parsing.MessageTextCode;
import com.squarespace.cldrengine.messageformat.parsing.PluralChoice;
import com.squarespace.cldrengine.messageformat.parsing.PluralNumberType;
import com.squarespace.cldrengine.messageformat.parsing.SelectChoice;

public class MessageEngine {

  private static final Map<String, Decimal> DECIMAL_EXACT = new HashMap<String, Decimal>() {
    {
      put("0", DecimalConstants.ZERO);
      put("1", DecimalConstants.ONE);
      put("2", DecimalConstants.TWO);
    }
  };

  private final StringBuilder buf = new StringBuilder();
  private final PluralRules plurals;
  private final MessageArgConverter converter;
  private final MessageFormatFuncMap formatters;
  private final MessageCode code;

  public MessageEngine(
      PluralRules plurals,
      MessageArgConverter converter,
      MessageFormatFuncMap formatters,
      MessageCode code) {
    this.plurals = plurals;
    this.converter = converter;
    this.formatters = formatters;
    this.code = code;
  }

  public String evaluate(MessageArgs args) {
    return this._evaluate(code, args, null);
  }

  protected String _evaluate(MessageCode code, MessageArgs args, Object argsub) {
    switch (code.type()) {
      case NOOP:
        break;

      case TEXT: {
        this.buf.append(((MessageTextCode)code).text());
        break;
      }

      case BLOCK: {
        for (MessageCode n : ((MessageBlockCode)code).block()) {
          this._evaluate(n, args, argsub);
        }
        break;
      }

      case ARG: {
        Object key = ((MessageArgCode)code).arg();
        Object arg = getarg(args, key);
        this.buf.append(this.converter.asString(arg));
        break;
      }

      case ARGSUB: {
        this.buf.append(this.converter.asString(argsub));
        break;
      }

      case PLURAL: {
        MessagePluralCode pcode = (MessagePluralCode)code;
        Object arg = getarg(args, pcode.args().get(0));
        int offset = pcode.offset();
        Decimal num = this.converter.asDecimal(arg);
        Decimal darg = offset != 0 ? num.subtract(new Decimal(offset)) : num;
        String category = pcode.pluralType() == PluralNumberType.CARDINAL
            ? this.plurals.cardinal(darg).value()
            : this.plurals.ordinal(darg).value();
        argsub = darg;

        MessageCode other = null;
        boolean found = false;

        loop: for (PluralChoice c : pcode.choices()) {
          switch (c.type()) {
            case EXACT:
              Decimal v = DECIMAL_EXACT.get(c.match());
              if (v == null) {
                v = new Decimal(c.match());
              }
              if (num.compare(v) == 0) {
                this._evaluate(c.code(), args, num);
                found = true;
                break loop;
              }
              break;

            case CATEGORY:
              if (c.match().equals(category)) {
                this._evaluate(c.code(), args, argsub);
                found = true;
                break loop;
              } else if (c.match().equals("other")) {
                // Capture the 'other' as a fallback
                other = c.code();
              }
              break;
          }
        }

        // If no match and 'other' exists, emit that value.
        if (!found && other != null) {
          this._evaluate(other, args, argsub);
        }
        break;
      }

      case SELECT: {
        MessageSelectCode scode = (MessageSelectCode)code;
        Object arg = getarg(args, scode.args().get(0));
        String str = this.converter.asString(arg);

        MessageCode other = null;
        boolean found = false;

        loop: for (SelectChoice c : scode.choices()) {
          if (c.match().equals(str)) {
            this._evaluate(c.code(), args, arg);
            found = true;
            break loop;
          }

          if (c.match().equals("other")) {
            // Capture the 'other' as a fallback
            other = c.code();
          }
        }

        // If no match and 'other' exists, emit that value.
        if (!found && other != null) {
          this._evaluate(other, args, arg);
        }
        break;
      }

      case SIMPLE: {
        // One or more arguments and zero or more options
        MessageSimpleCode scode = (MessageSimpleCode) code;
        String name = scode.name();
        MessageFormatFunc func = this.formatters == null ? null : this.formatters.get(name);
        if (func != null) {
          List<Object> funcargs = new ArrayList<>();
          for (Object key : scode.args()) {
            Object arg = getarg(args, key);
            funcargs.add(arg);
          }
          String result = func.apply(funcargs, scode.options());
          buf.append(result);
        }
        break;
      }
    }
    return buf.toString();
  }

  protected Object getarg(MessageArgs args, Object key) {
    if (key instanceof String) {
      return args.get((String)key);
    }
    return args.get((int)key);
  }

}
