package com.squarespace.cldrengine.internal;



public class Schema {

  public final NamesSchema Names;
  public final NumbersSchema Numbers;
  public final DateFieldsSchema DateFields;
  public final LayoutSchema Layout;
  public final ListPatternsSchema ListPatterns;
  public final BuddhistSchema Buddhist;
  public final GregorianSchema Gregorian;
  public final JapaneseSchema Japanese;
  public final PersianSchema Persian;
  public final TimeZoneSchema TimeZones;
  public final CurrenciesSchema Currencies;
  public final UnitsSchema Units;
  public final ContextTransformsSchema ContextTransforms;

  public Schema(
      NamesSchema Names,
      NumbersSchema Numbers,
      DateFieldsSchema DateFields,
      LayoutSchema Layout,
      ListPatternsSchema ListPatterns,
      BuddhistSchema Buddhist,
      GregorianSchema Gregorian,
      JapaneseSchema Japanese,
      PersianSchema Persian,
      TimeZoneSchema TimeZones,
      CurrenciesSchema Currencies,
      UnitsSchema Units,
      ContextTransformsSchema ContextTransforms) {
    this.Names = Names;
    this.Numbers = Numbers;
    this.DateFields = DateFields;
    this.Layout = Layout;
    this.ListPatterns = ListPatterns;
    this.Buddhist = Buddhist;
    this.Gregorian = Gregorian;
    this.Japanese = Japanese;
    this.Persian = Persian;
    this.TimeZones = TimeZones;
    this.Currencies = Currencies;
    this.Units = Units;
    this.ContextTransforms = ContextTransforms;
  }

}
