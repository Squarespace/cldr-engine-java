package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.EraAltType;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.EraWidthType;

public class CalendarSchema {

  public final Vector3Arrow<EraWidthType, String, EraAltType> eras;
  public final CalendarFields format;
  public final CalendarFields standAlone;
  public final Vector1Arrow<String> availableFormats;
  public final Vector2Arrow<PluralType, String> pluralFormats;
  public final Vector2Arrow<DateTimePatternFieldType, String> intervalFormats;
  public final Vector1Arrow<FormatWidthType> dateFormats;
  public final Vector1Arrow<FormatWidthType> timeFormats;
  public final Vector1Arrow<FormatWidthType> dateTimeFormats;
  public final Vector1Arrow<FormatWidthType> dateTimeFormatsAt;
  public final FieldArrow intervalFormatFallback;

  public CalendarSchema(
      Vector3Arrow<EraWidthType, String, EraAltType> eras,
      CalendarFields format,
      CalendarFields standAlone,
      Vector1Arrow<String> availableFormats,
      Vector2Arrow<PluralType, String> pluralFormats,
      Vector2Arrow<DateTimePatternFieldType, String> intervalFormats,
      Vector1Arrow<FormatWidthType> dateFormats,
      Vector1Arrow<FormatWidthType> timeFormats,
      Vector1Arrow<FormatWidthType> dateTimeFormats,
      Vector1Arrow<FormatWidthType> dateTimeFormatsAt,
      FieldArrow intervalFormatFallback) {
    this.eras = eras;
    this.format = format;
    this.standAlone = standAlone;
    this.availableFormats = availableFormats;
    this.pluralFormats = pluralFormats;
    this.intervalFormats = intervalFormats;
    this.dateFormats = dateFormats;
    this.timeFormats = timeFormats;
    this.dateTimeFormats = dateTimeFormats;
    this.dateTimeFormatsAt = dateTimeFormatsAt;
    this.intervalFormatFallback = intervalFormatFallback;
  }

}
