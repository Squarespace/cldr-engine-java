package com.squarespace.cldrengine.internal;

import java.util.Map;

import com.squarespace.cldrengine.locale.LanguageTag;

public class StringBundle implements Bundle {

  private String languageRegion;
  private String languageScript;
  private String calendarSystem = "";
  private String numberSystem = "default";
  private String id;
  private LanguageTag tag;
  private String[] strings;
  private String[] exceptions;
  private Map<Integer, Integer> index;

  public StringBundle(
      String id,
      LanguageTag tag,
      String[] strings,
      String[] exceptions,
      Map<Integer, Integer> index) {

    this.id = id;
    this.tag = tag;
    this.strings = strings;
    this.exceptions = exceptions;
    this.index = index;

    this.languageRegion = String.format("%s-%s", tag.language(), tag.region());
    this.languageScript = String.format("%s-%s", tag.language(), tag.script());

    // When bundle is constructed, see if there are unicode extensions for
    // number and calendar systems.
    for (String subtag : tag.extensionSubtags("u")) {
      if (subtag.startsWith("nu-")) {
        this.numberSystem = subtag.substring(3);
      } else if (subtag.startsWith("ca-")) {
        this.calendarSystem = subtag.substring(3);
      }
    }
  }

  public String id() {
    return id;
  }

  public String language() {
    return this.tag.language();
  }

  public String region() {
    return this.tag.region();
  }

  public String languageScript() {
    return this.languageScript;
  }

  public String languageRegion() {
    return this.languageRegion;
  }

  public String calendarSystem() {
    return this.calendarSystem;
  }

  public String numberSystem() {
    return this.numberSystem;
  }

  public String get(int offset) {
    // If there is an exception index, attempt to resolve it.
    if (this.index != null) {
      Integer i = this.index.get(offset);
      if (i != null) {
        return i < this.exceptions.length ? this.exceptions[i] : "";
      }
    }
    // Return the actual string
    return offset < strings.length ? this.strings[offset] : "";
  }

}
