package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.EraAltType;
import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.FormatWidthType;
import com.squarespace.cldrengine.api.EraWidthType;

public class GregorianSchema extends CalendarSchema {

  public GregorianSchema(
      Vector3Arrow<EraWidthType, String, EraAltType> eras,
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
