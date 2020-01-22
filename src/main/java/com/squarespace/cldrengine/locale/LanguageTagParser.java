package com.squarespace.cldrengine.locale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.squarespace.cldrengine.api.LanguageTag;
import com.squarespace.cldrengine.internal.LocaleExternalData;
import com.squarespace.cldrengine.utils.StringUtils;

public class LanguageTagParser {

  // Subtag separator
  private static final String SEP = "-";

  // ISO 639 language code
  private static final Pattern LANGUAGE = Pattern.compile("^[a-z]{2,8}$");

  // Selected ISO 639 codes
  private static final Pattern EXTLANG = Pattern.compile("^[a-z]{3}$", Pattern.CASE_INSENSITIVE);

  // ISO 15924 script code
  private static final Pattern SCRIPT = Pattern.compile("^[a-z]{4}$", Pattern.CASE_INSENSITIVE);

  // ISO 3166-1 or UN M.49 code
  private static final Pattern REGION = Pattern.compile("^([a-z]{2,3}|\\d{3})$", Pattern.CASE_INSENSITIVE);

  // Registered variants
  private static final Pattern VARIANT = Pattern.compile("^([a-z\\d]{5,8}|\\d[a-z\\d]{3})$", Pattern.CASE_INSENSITIVE);

  private static final Pattern EXTENSION_PREFIX = Pattern.compile("^[\\da-wyz]$", Pattern.CASE_INSENSITIVE);
  private static final Pattern EXTENSION_SUBTAG = Pattern.compile("^[\\da-z]{2,8}$", Pattern.CASE_INSENSITIVE);
  private static final Pattern PRIVATEUSE_PREFIX = Pattern.compile("^x$", Pattern.CASE_INSENSITIVE);
  private static final Pattern PRIVATEUSE_SUBTAG = Pattern.compile("^[\\da-z]{1,8}$", Pattern.CASE_INSENSITIVE);


  // https://www.unicode.org/reports/tr35/tr35-33/tr35.html#Key_And_Type_Definitions_
  private static final Set<String> UNICODE_EXTENSION_KEYS = new HashSet<String>() {{
    this.add("ca"); // calendar
    this.add("co"); // collation
    this.add("cu"); // currency
    this.add("nu"); // numbering system
    this.add("tz"); // timezone
    this.add("va"); // common variant type
  }};

  // Grandfathered irregular and regular tags from IANA registry.
  private static final Map<String, String> GRANDFATHERED_TAGS = new HashMap<String, String>() {{
    // Additional fallbacks from ICU
    this.put("cel-gaulish", "xtg-x-cel-gaulish");
    this.put("en-GB-oed", "en-GB-x-oed");
    this.put("i-default", "en-x-i-default");
    this.put("i-enochian", "und-x-i-enochian");
    this.put("i-mingo", "see-x-i-mingo");
    this.put("zh-min", "nan-x-zh-min");
  }};

  static {
    // Grandfathered tag mapping
    String[] tags = LocaleExternalData.GRANDFATHEREDRAW.split("\\|");
    for (String tag : tags) {
      String[] parts = tag.split(":");
      if (parts.length == 1) {
        GRANDFATHERED_TAGS.put(parts[0], "");
      } else {
        GRANDFATHERED_TAGS.put(parts[0], parts[1]);
      }
    }
  }

  private String language;
  private String script;
  private String region;
  private List<String> extlangs;
  private List<String> variants;
  private Map<String, List<String>> extensions;
  private String privateUse;

  private String str;

  private LanguageTagParser(String str) {
    this.str = str;
  }

  /**
   * Low-level parsing of a language tag. No resolution is performed.
   */
  public static LanguageTag parse(String str) {
    return new LanguageTagParser(str).parse();
  }

  private LanguageTag parse() {
    String str = this.str.indexOf("_") == -1 ? this.str : this.str.replaceAll("_", SEP);
    String preferred = GRANDFATHERED_TAGS.get(str.toLowerCase());
    String[] arr = preferred == null ? str.split(SEP) : preferred.split(SEP);
    List<String> parts = toList(arr);
    if (parseLanguage(parts)) {
      parseExtLangs(parts);
      parseScript(parts);
      parseRegion(parts);
      parseVariants(parts);
      parseExtensions(parts);
    }
    parsePrivateUse(parts);

    // If no region was parsed, check if one of the extlangs is actually a valid ISO 3166 code
    if (this.region == null && this.extlangs != null) {
      int size = this.extlangs.size();
      for (int i = 0; i < size; i++) {
        // TODO: implement replaceRegion
//      const replacement = replaceRegion(this.extlangs[i].toUpperCase());
//      if (replacement !== undefined) {
//        this.region = replacement;
//        // Ignore the extlangs since we currently don't add them to the LanguageTag.
//        break;
//      }
      }
    }

    return new LanguageTag(
      this.language,
      this.script,
      this.region,
      this.variants != null ? this.variants.get(0) : null,
      this.extensions,
      this.privateUse
    );
  }

  private boolean parseLanguage(List<String> parts) {
    this.language = match(parts, LANGUAGE);
    return this.language != null;
  }

  private boolean parseExtLangs(List<String> parts) {
    while (!parts.isEmpty()) {
      String result = match(parts, EXTLANG);
      if (result == null) {
        break;
      }
      if (this.extlangs == null) {
        this.extlangs = new ArrayList<>();
      }
      this.extlangs.add(result);
    }
    return this.extlangs != null;
  }

  private boolean parseScript(List<String> parts) {
    this.script = match(parts, SCRIPT);
    return this.script != null;
  }

  private boolean parseRegion(List<String> parts) {
    this.region = match(parts, REGION);
    return this.region != null;
  }

  private boolean parseVariants(List<String> parts) {
    while (!parts.isEmpty()) {
      String result = match(parts, VARIANT);
      if (result == null) {
        break;
      }
      if (this.variants == null) {
        this.variants = new ArrayList<>();
      }
      this.variants.add(result);
    }
    return this.variants != null;
  }

  private boolean parseExtensions(List<String> parts) {
    boolean parsed = false;
    while (!parts.isEmpty()) {
      String prefix = match(parts, EXTENSION_PREFIX);
      if (prefix == null) {
        break;
      }

      List<String> subs = null;
      String temp = "";
      while (!parts.isEmpty()) {
        String subtag = match(parts, EXTENSION_SUBTAG);
        if (subtag == null) {
          break;
        }

        if (!UNICODE_EXTENSION_KEYS.contains(subtag)) {
          temp += temp.length() > 0 ? SEP + subtag : subtag;
          continue;
        }

        if (temp.length() > 0) {
          if (subs == null) {
            subs = new ArrayList<>();
          }
          subs.add(temp);
        }
        temp = subtag;
      }

      if (temp.length() > 0) {
        if (subs == null) {
          subs = new ArrayList<>();
        }
        subs.add(temp);
      }

      if (subs != null && subs.size() > 0) {
        parsed = true;
        Collections.sort(subs);
        if (this.extensions == null) {
          this.extensions = new HashMap<>();
        }
        List<String> curr = this.extensions.get(prefix);
        if (curr != null) {
          curr.addAll(subs);
        } else {
          curr = subs;
        }
        Collections.sort(curr);
        this.extensions.put(prefix, curr);
      }
    }
    return parsed;
  }

  private boolean parsePrivateUse(List<String> parts) {
    while (!parts.isEmpty()) {
      String prefix = match(parts, PRIVATEUSE_PREFIX);
      if (prefix == null) {
        break;
      }

      List<String> subs = null;
      while (!parts.isEmpty()) {
        String subtag = match(parts, PRIVATEUSE_SUBTAG);
        if (subtag == null) {
          break;
        }

        if (subs == null) {
          subs = new ArrayList<>();
        }
        subs.add(subtag);
      }

      if (subs != null && subs.size() > 0) {
        if (this.privateUse == null) {
          this.privateUse = "";
        }
        this.privateUse = this.privateUse + prefix + SEP + StringUtils.join(subs, SEP);
      }
    }
    return this.privateUse != null;
  }

  /**
   * Match the first element of the parts array against the given pattern.
   * Shifts the first element and returns the match, or returns null.
   */
  private String match(List<String> parts, Pattern pattern) {
    if (!parts.isEmpty()) {
      String part = parts.get(0);
      if (pattern.matcher(part).matches()) {
        parts.remove(0);
        return part;
      }
    }
    return null;
  }

  private List<String> toList(String[] arr) {
    if (arr.length == 0) {
      return Collections.emptyList();
    }
    List<String> res = new ArrayList<>(arr.length);
    for (String elem : arr) {
      res.add(elem);
    }
    return res;
  }
}
