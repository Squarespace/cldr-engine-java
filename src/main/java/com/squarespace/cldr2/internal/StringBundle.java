package com.squarespace.cldr2.internal;

import java.util.Map;

public class StringBundle {

  private String _languageRegion;
  private String _languageScript;
  private String _calendarSystem = "";
  private String _numberSystem = "default";
  private String _id;
  private LanguageTag _tag;
  private String[] strings;
  private String[] exceptions;
  private Map<Integer, Integer> index;

  public StringBundle(
      String _id,
      LanguageTag _tag,
      String[] strings,
      String[] exceptions,
      Map<Integer, Integer> index) {

    this._id = _id;
    this._tag = _tag;
    this.strings = strings;
    this.exceptions = exceptions;
    this.index = index;

    this._languageRegion = String.format("%s-%s", _tag.language(), _tag.region());
    this._languageScript = String.format("%s-%s", _tag.language(), _tag.script());

    // TODO: set _calendarSystem and _numberSystem from language tag extensions

      // When bundle is constructed, see if there are unicode extensions for
      // number and calendar systems.
//    for (const subtag of tag.extensionSubtags('u')) {
//      if (subtag.startsWith('nu-')) {
//        this._numberSystem = subtag.substring(3);
//      } else if (subtag.startsWith('ca-')) {
//        this._calendarSystem = subtag.substring(3);
//      }
//    }

  }

  String id() {
    return _id;
  }

  String language() {
    return this._tag.language();
  }

  String region() {
    return this._tag.region();
  }

  String languageScript() {
    return this._languageScript;
  }

  String languageRegion() {
    return this._languageRegion;
  }

  String calendarSystem() {
    return this._calendarSystem;
  }

  String numberSystem() {
    return this._numberSystem;
  }

  String get(int offset) {
    // If there is an exception index, attempt to resolve it.
    if (this.index != null) {
      Integer i = this.index.get(offset);
      if (i != null) {
        return this.exceptions[i];
      }
    }
    // Return the actual string
    return offset < strings.length ? this.strings[offset] : null;
  }
}
