Port of @phensley/cldr to Java.

[![javadoc](https://javadoc.io/badge2/com.squarespace.cldr-engine/cldr-engine/javadoc.svg)](https://javadoc.io/doc/com.squarespace.cldr-engine/cldr-engine)

The API is compatible with that of the TypeScript [@phensley/cldr](https://phensley.github.io/cldr-engine/docs/en/api-cldr) library (**@phensley/cldr 1.15.0**, CLDR **48.2.0**).

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

// New API surface (cldr-engine 1.15.0):

// new*Date(fields) builds dates from wall-clock fields; until / since return
// signed, Temporal-style periods.
CLDR cldr = CLDR.get("en-US");
GregorianDate start = cldr.Calendars.newGregorianDate(CalendarDateFields.build()
    .year(2024).month(1).day(30).hour(12).minute(0).zoneId("America/New_York"));
GregorianDate end = cldr.Calendars.newGregorianDate(CalendarDateFields.build()
    .year(2024).month(3).day(3).hour(12).minute(0).zoneId("America/New_York"));
TimePeriod until = start.until(end, null);
TimePeriod since = end.since(start, null);
System.out.println(String.format("until: %.0f month, %.0f day | since: %.0f month, %.0f day",
    until.month, until.day, since.month, since.day));

// startOf / endOf truncate or extend a date to a field boundary.
GregorianDate d = cldr.Calendars.newGregorianDate(CalendarDateFields.build()
    .year(2024).month(2).day(29).hour(22).minute(45).second(30).millis(123)
    .zoneId("America/New_York"));
System.out.println(d.startOf(CalendarDateModFields.DAY).toDateTimeString(TimeStringOptions.build()));
System.out.println(d.endOf(CalendarDateModFields.MONTH).toDateTimeString(TimeStringOptions.build()));

// Reduced date and time strings.
System.out.println(d.toDateString());
System.out.println(d.toTimeString(TimeStringOptions.build().includeZoneId(true)));

// now* returns the current instant in a given calendar and zone.
System.out.println(cldr.Calendars.nowGregorian("America/New_York").toDateString());

// General.messageFormatter renders ICU-style plural / select messages.
MessageFormatter mf = cldr.General.messageFormatter();
System.out.println(mf.format("{0 plural one {# item} other {# items}}", new MessageArgs().add(5)));
System.out.println(mf.format("{gender select female {her} male {his} other {their}}",
    new MessageArgs().add("gender", "male")));
```

```
Locale: en-US:
$12,345.68
$12K
February 18, 2020 at 4:16:31 PM
Feb 18 – Mar 4, 2020
3,413 miles
17 hr, 37.2 min

Locale: es-419:
USD 12,345.68
USD 12 k
18 de febrero de 2020 a las 4:16:31 p.m.
18 de feb – 4 de mar de 2020
3,413 millas
17 h y 37.2 min

Locale: fr:
12 345,68 $US
12 k $US
18 février 2020 à 16:16:31
18 févr. – 4 mars 2020
3 413 miles
17 h et 37,2 min

Locale: zh:
US$12,345.68
US$1.2万
2020年2月18日 16:16:31
2020年2月18日 – 3月4日
3,413英里
17小时37.2分钟

until: 1 month, 3 day | since: 1 month, 4 day
2024-02-29 00:00:00.000
2024-02-29 23:59:59.999
2024-02-29
22:45:30.123 America/New_York
2026-09-06
5 items
his
```
