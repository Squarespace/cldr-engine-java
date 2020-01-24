package com.squarespace.cldrengine.general;

import java.util.Map;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CharacterOrderType;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.api.LineOrderType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.LayoutSchema;
import com.squarespace.cldrengine.internal.ListPatternsSchema;
import com.squarespace.cldrengine.internal.NamesSchema;
import com.squarespace.cldrengine.internal.Schema;
import com.squarespace.cldrengine.parsing.WrapperPattern;
import com.squarespace.cldrengine.utils.Cache;

public class GeneralInternals {

  public final Internals internals;
  public final Schema schema;
  public final LayoutSchema layout;
  public final ListPatternsSchema listPatterns;
  public final NamesSchema names;
  public final Cache<WrapperPattern> wrapperPatternCache;

  public GeneralInternals(Internals internals) {
    this.internals = internals;
    this.schema = internals.schema;
    this.layout = schema.Layout;
    this.listPatterns = schema.ListPatterns;
    this.names = schema.Names;
    this.wrapperPatternCache = new Cache<WrapperPattern>(WrapperPattern::parse, 64);
  }

  public CharacterOrderType characterOrder(Bundle bundle) {
    String s = this.layout.characterOrder.get(bundle);
    return CharacterOrderType.fromString(s);
  }

  public LineOrderType lineOrder(Bundle bundle) {
    String s = this.layout.lineOrder.get(bundle);
    return LineOrderType.fromString(s);
  }

  /**
   * Contextually transform a string
   */
  public String contextTransform(String value, Map<ContextTransformFieldType, String> info,
      ContextType context, ContextTransformFieldType field) {
    if (value.isEmpty()) {
      return value;
    }

    String flag = field != null ? info.get(field) : "";
    boolean title = false;
    switch (context) {
      case BEGIN_SENTENCE:
        title = true;
        break;
      case STANDALONE:
        title = flag != null && (flag.charAt(0) == 'T');
        break;
      case UI_LIST_OR_MENU:
        title = flag != null && (flag.charAt(1) == 'T');
        break;
      default:
        break;
    }
    if (!title) {
      return value;
    }
    // TODO: Future: Unicode title case. The use of upper case matches the TypeScript transform.
    char first = value.charAt(0);
    return Character.toUpperCase(first) + value.substring(1);
  }

  public String formatWrapper(String format, String ...args) {
    WrapperPattern pattern = this.wrapperPatternCache.get(format);
    StringBuilder buf = new StringBuilder();
    for (Object node : pattern.nodes) {
      if (node instanceof String) {
        buf.append((String)node);
      } else {
        int i = (int)node;
        if (i < args.length) {
          String arg = args[i];
          if (!arg.isEmpty()) {
            buf.append(arg);
          }
        }
      }
    }
    return buf.toString();
  }

  public WrapperPattern parseWrapper(String raw) {
    return this.wrapperPatternCache.get(raw);
  }
}
