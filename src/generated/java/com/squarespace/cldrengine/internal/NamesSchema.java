package com.squarespace.cldrengine.internal;



public class NamesSchema {

  public final LanguageNameInfo languages;
  public final ScriptNameInfo scripts;
  public final RegionNameInfo regions;

  public NamesSchema(
      LanguageNameInfo languages,
      ScriptNameInfo scripts,
      RegionNameInfo regions) {
    this.languages = languages;
    this.scripts = scripts;
    this.regions = regions;
  }

}
