package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.EraAltType;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.EraWidthType;

public class BuddhistSchema extends CalendarSchema {

  public BuddhistSchema(
      Vector3Arrow<EraWidthType, String, EraAltType> eras,
      CalendarFields format,
      CalendarFields standAlone,
      Vector1Arrow<String> availableFormats,
      Vector2Arrow<PluralType, String> pluralFormats,
      Vector2Arrow<String, DateTimePatternFieldType> intervalFormats,
      Vector1Arrow<FormatWidthType> dateFormats,
      Vector1Arrow<FormatWidthType> timeFormats,
      Vector1Arrow<FormatWidthType> dateTimeFormats,
      Vector1Arrow<FormatWidthType> dateTimeFormatsAt,
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
      dateTimeFormatsAt,
      intervalFormatFallback
    );

  }

}
