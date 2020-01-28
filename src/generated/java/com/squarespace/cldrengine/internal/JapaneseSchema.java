package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.EraWidthType;
public class JapaneseSchema extends CalendarSchema {

  public JapaneseSchema(
      Vector2Arrow<EraWidthType, String> eras,
      CalendarFields format,
      CalendarFields standAlone,
      Vector1Arrow<String> availableFormats,
      Vector2Arrow<PluralType, String> pluralFormats,
      Vector2Arrow<DateTimePatternFieldType, String> intervalFormats,
      Vector1Arrow<FormatWidthType> dateFormats,
      Vector1Arrow<FormatWidthType> timeFormats,
      Vector1Arrow<FormatWidthType> dateTimeFormats,
      FieldArrow intervalFormatFallback) {
    super(
      eras,
      format,
      standAlone,
      availableFormats,
      pluralFormats,
      intervalFormats,
      dateFormats,
      timeFormats,
      dateTimeFormats,
      intervalFormatFallback
    );

  }

}
