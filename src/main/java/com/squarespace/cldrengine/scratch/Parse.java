package com.squarespace.cldrengine.scratch;

import java.util.Locale;

public class Parse {

  public static void main(String[] args) {
    Locale locale = Locale.forLanguageTag("FR-latn-fr");
    System.out.println(locale.toLanguageTag());
  }
}
