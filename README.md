Port of @phensley/cldr to Java.

[![javadoc](https://javadoc.io/badge2/com.squarespace.cldr-engine/cldr-engine/javadoc.svg)](https://javadoc.io/doc/com.squarespace.cldr-engine/cldr-engine)

The API is compatible with that of the TypeScript [@phensley/cldr](https://phensley.github.io/cldr-engine/docs/en/api-cldr) library.

Example usage

```java
for (String locale : new String[] { "en-US", "es-419", "fr", "zh" }) {
  System.out.println("Locale: " + locale + ":");

  CLDR cldr = CLDR.get(locale);
  String s;

  s = cldr.Numbers.formatCurrency(new Decimal("12345.6789"), CurrencyType.USD);
  System.out.println(s);

  CurrencyFormatOptions currencyOpts = CurrencyFormatOptions.build()
      .style(CurrencyFormatStyleType.SHORT);
  s = cldr.Numbers.formatCurrency(new Decimal("12345.6789"), CurrencyType.USD, currencyOpts);
  System.out.println(s);

  DateFormatOptions dateOpts = DateFormatOptions.build()
      .date(FormatWidthType.LONG)
      .time(FormatWidthType.MEDIUM);
  GregorianDate date = cldr.Calendars.toGregorianDate(1582060591000L, "America/New_York");
  s = cldr.Calendars.formatDate(date, dateOpts);
  System.out.println(s);

  GregorianDate end = cldr.Calendars.toGregorianDate(1583334991000L, "America/New_York");
  s = cldr.Calendars.formatDateInterval(date, end, null);
  System.out.println(s);

  Quantity qty = Quantity.build().value(new Decimal("3413")).unit(UnitType.MILE);
  s = cldr.Units.formatQuantity(qty, null);
  System.out.println(s);

  List<Quantity> seq = Arrays.asList(
      Quantity.build().value(new Decimal("17")).unit(UnitType.HOUR),
      Quantity.build().value(new Decimal("37.2")).unit(UnitType.MINUTE)
  );
  s = cldr.Units.formatQuantitySequence(seq, UnitFormatOptions.build().length(UnitLength.SHORT));
  System.out.println(s);

  System.out.println();
}
```

```
Locale: en-US:
$12,345.68
$12K
February 18, 2020 at 4:16:31 PM
Feb 18 – Mar 4, 2020
3,413 miles
17 hr, 37.2 min

Locale: es-419:
USD 12,345.68
USD 12 K
18 de febrero de 2020 a las 16:16:31
18 de feb. – 4 de mar. de 2020
3413 millas
17 h y 37.2 min

Locale: fr:
12 345,68 $US
12 k $US
18 février 2020 à 16:16:31
18 févr. – 4 mars 2020
3 413 miles
17 h et 37,2 min

Locale: zh:
US$12,345.68
US$1.2万
2020年2月18日 下午4:16:31
2020年2月18日至3月4日
3,413英里
17小时37.2分钟
```
