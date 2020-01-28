package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.ListPatternPositionType;
public class ListPatternsSchema {

  public final Vector1Arrow<ListPatternPositionType> and;
  public final Vector1Arrow<ListPatternPositionType> andShort;
  public final Vector1Arrow<ListPatternPositionType> or;
  public final Vector1Arrow<ListPatternPositionType> unitLong;
  public final Vector1Arrow<ListPatternPositionType> unitNarrow;
  public final Vector1Arrow<ListPatternPositionType> unitShort;

  public ListPatternsSchema(
      Vector1Arrow<ListPatternPositionType> and,
      Vector1Arrow<ListPatternPositionType> andShort,
      Vector1Arrow<ListPatternPositionType> or,
      Vector1Arrow<ListPatternPositionType> unitLong,
      Vector1Arrow<ListPatternPositionType> unitNarrow,
      Vector1Arrow<ListPatternPositionType> unitShort) {
    this.and = and;
    this.andShort = andShort;
    this.or = or;
    this.unitLong = unitLong;
    this.unitNarrow = unitNarrow;
    this.unitShort = unitShort;
  }

}
