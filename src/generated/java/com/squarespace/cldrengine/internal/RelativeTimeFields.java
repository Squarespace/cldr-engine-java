package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.PluralType;
import com.squarespace.cldrengine.api.RelativeTimeFieldType;
public class RelativeTimeFields {

  public final Vector1Arrow<RelativeTimeFieldType> previous2;
  public final Vector1Arrow<RelativeTimeFieldType> previous;
  public final Vector1Arrow<RelativeTimeFieldType> current;
  public final Vector1Arrow<RelativeTimeFieldType> next;
  public final Vector1Arrow<RelativeTimeFieldType> next2;
  public final Vector2Arrow<PluralType, RelativeTimeFieldType> future;
  public final Vector2Arrow<PluralType, RelativeTimeFieldType> past;

  public RelativeTimeFields(
      Vector1Arrow<RelativeTimeFieldType> previous2,
      Vector1Arrow<RelativeTimeFieldType> previous,
      Vector1Arrow<RelativeTimeFieldType> current,
      Vector1Arrow<RelativeTimeFieldType> next,
      Vector1Arrow<RelativeTimeFieldType> next2,
      Vector2Arrow<PluralType, RelativeTimeFieldType> future,
      Vector2Arrow<PluralType, RelativeTimeFieldType> past) {
    this.previous2 = previous2;
    this.previous = previous;
    this.current = current;
    this.next = next;
    this.next2 = next2;
    this.future = future;
    this.past = past;
  }

}
