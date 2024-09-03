package com.squarespace.cldrengine.api;

import static com.squarespace.cldrengine.api.Part.part;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

public class NumbersApiTest {

  private final static CLDR EN = CLDR.get("en");
  private final static CLDR FR = CLDR.get("fr");
  private final static CLDR JA = CLDR.get("ja");

  @Test
  public void testAdjustDecimal() {
    DecimalAdjustOptions opts = DecimalAdjustOptions.build();
    Decimal r = EN.Numbers.adjustDecimal(dec("1"));
    assertEquals(r.toString(), "1");

    r = EN.Numbers.adjustDecimal(dec("1"), opts.minimumFractionDigits(3));
    assertEquals(r.toString(), "1.000");

    r = EN.Numbers.adjustDecimal(dec("1.555"), opts.minimumFractionDigits(2));
    assertEquals(r.toString(), "1.56");
  }

  @Test
  public void testJapaneseUnits() {
    Quantity q = Quantity.build().value(dec("5")).unit(UnitType.HOUR);
    String r = JA.Units.formatQuantity(q, UnitFormatOptions.build().length(UnitLength.SHORT));
    assertEquals(r.toString(), "5時間");
  }

  @Test
  public void testFormatCurrency() {
    CurrencyFormatOptions opts = CurrencyFormatOptions.build();
    Decimal n = dec("1234.5678");
    String r = EN.Numbers.formatCurrency(n, CurrencyType.USD);
    assertEquals(r, "$1,234.57");

    r = EN.Numbers.formatCurrency(n, CurrencyType.EUR, opts.group(false));
    assertEquals(r, "€1234.57");

    // French
    r = FR.Numbers.formatCurrency(n, CurrencyType.EUR, opts.group(false));
    assertEquals(r, "1234,57 €");

    try {
      r = FR.Numbers.formatCurrency(dec("infinity"), CurrencyType.EUR);
      fail("expected formatCurrency(Infinity) to throw an error");
    } catch (Exception e) {
      // ok
    }
  }

  @Test
  public void testFormatCurrencyNumberSystems() {
    CurrencyFormatOptions opts = CurrencyFormatOptions.build()
        .numberSystem("beng").style(CurrencyFormatStyleType.CODE);
    Decimal n = dec("0");
    String r = EN.Numbers.formatCurrency(n, CurrencyType.USD, opts);
    assertEquals(r, "০.০০ USD");
  }

  @Test
  public void testFormatCurrencyToParts() {
    Decimal n = dec("1234.5678");
    List<Part> r = EN.Numbers.formatCurrencyToParts(n, CurrencyType.USD);
    assertEquals(r, Arrays.asList(
        part("currency", "$"),
        part("integer", "1"),
        part("group", ","),
        part("integer", "234"),
        part("decimal", "."),
        part("fraction", "57")
    ));

    r = EN.Numbers.formatCurrencyToParts(n, CurrencyType.JPY);
    assertEquals(r, Arrays.asList(
        part("currency", "¥"),
        part("integer", "1"),
        part("group", ","),
        part("integer", "235")
    ));
  }

  @Test
  public void testFormatDecimal() {
    Decimal n = dec("1234.5678");
    String r = EN.Numbers.formatDecimal(n);
    assertEquals(r, "1,234.568");

    DecimalFormatOptions opts = DecimalFormatOptions.build()
        .maximumFractionDigits(1);
    r = EN.Numbers.formatDecimal(n, opts);
    assertEquals(r, "1,234.6");

    r = EN.Numbers.formatDecimal(dec("-infinity"), opts.errors("nan"));
    assertEquals(r, "∞");

    try {
      EN.Numbers.formatDecimal(dec("nan"), opts.errors("nan"));
      fail("expected formatDecimal(nan) to throw");
    } catch (Exception e) {
      // ok
    }

    opts.numberSystem("sund");
    r = EN.Numbers.formatDecimal(n, opts);
    assertEquals(r, "᮱,᮲᮳᮴.᮶");
  }

  @Test
  public void testFormatDecimalCompactRounding() {
    DecimalFormatOptions opts = DecimalFormatOptions.build().style(DecimalFormatStyleType.SHORT);
    String r;

    // When rounded up the compact divisor changes, going from 3 digit format "999K" to 1 digit "1M"
    r = EN.Numbers.formatDecimal(dec("999900"), opts);
    assertEquals(r, "1M");

    r = EN.Numbers.formatDecimal(dec("999900"), opts.minimumFractionDigits(1));
    assertEquals(r, "999.9K");

    r = EN.Numbers.formatDecimal(dec("999900"), opts.minimumFractionDigits(2));
    assertEquals(r, "999.90K");
  }

  @Test
  public void testFormatDecimalToParts() {
    Decimal n = dec("1234.5678");
    List<Part> r = EN.Numbers.formatDecimalToParts(n);
    assertEquals(r, Arrays.asList(
        part("integer", "1"),
        part("group", ","),
        part("integer", "234"),
        part("decimal", "."),
        part("fraction", "568")
    ));

    DecimalFormatOptions opts = DecimalFormatOptions.build()
        .maximumFractionDigits(1);
    r = EN.Numbers.formatDecimalToParts(n, opts);
    assertEquals(r, Arrays.asList(
        part("integer", "1"),
        part("group", ","),
        part("integer", "234"),
        part("decimal", "."),
        part("fraction", "6")
    ));
  }

  @Test
  public void testGetCurrencyPluralName() {
    String r = EN.Numbers.getCurrencyPluralName(dec("1"), CurrencyType.USD);
    assertEquals(r, "US dollar");

    r = EN.Numbers.getCurrencyPluralName(dec("0"), CurrencyType.USD);
    assertEquals(r, "US dollars");
  }

  @Test
  public void testGetCurrencyDisplayName() {
    String r = EN.Numbers.getCurrencyDisplayName(CurrencyType.USD);
    assertEquals(r, "US Dollar");

    r = EN.Numbers.getCurrencyDisplayName(CurrencyType.EUR);
    assertEquals(r, "Euro");
}

  @Test
  public void testGetCurrencyFractions() {
    CurrencyFractions r = EN.Numbers.getCurrencyFractions(CurrencyType.USD);
    assertEquals(r.digits, 2);
    assertEquals(r.rounding, 0);
    assertEquals(r.cashDigits, 2);
    assertEquals(r.cashRounding, 0);

    r = EN.Numbers.getCurrencyFractions(CurrencyType.DKK);
    assertEquals(r.digits, 2);
    assertEquals(r.rounding, 0);
    assertEquals(r.cashDigits, 2);
    assertEquals(r.cashRounding, 50);
  }

  @Test
  public void testGetCurrencyForRegion() {
    CurrencyType r = EN.Numbers.getCurrencyForRegion("US");
    assertEquals(r, CurrencyType.USD);

    r = EN.Numbers.getCurrencyForRegion("GB");
    assertEquals(r, CurrencyType.GBP);

    r = EN.Numbers.getCurrencyForRegion("AU");
    assertEquals(r, CurrencyType.AUD);
  }

  @Test
  public void testGetCurrencySymbol() {
    String r = EN.Numbers.getCurrencySymbol(CurrencyType.USD);
    assertEquals(r, "$");

    r = EN.Numbers.getCurrencySymbol(CurrencyType.AUD);
    assertEquals(r, "A$");

    r = EN.Numbers.getCurrencySymbol(CurrencyType.AUD, CurrencySymbolWidthType.NARROW);
    assertEquals(r, "$");
  }

  @Test
  public void testGetPluralCardinal() {
    PluralType r = EN.Numbers.getPluralCardinal(dec("1"));
    assertEquals(r, PluralType.ONE);

    DecimalAdjustOptions opts = DecimalAdjustOptions.build().minimumFractionDigits(1);
    r = EN.Numbers.getPluralCardinal(dec("1"), opts);
    assertEquals(r, PluralType.OTHER);

    r = EN.Numbers.getPluralCardinal(dec("2"));
    assertEquals(r, PluralType.OTHER);
  }

  @Test
  public void testGetPluralOrdinal() {
    PluralType r = EN.Numbers.getPluralOrdinal(dec("1"));
    assertEquals(r, PluralType.ONE);

    r = EN.Numbers.getPluralOrdinal(dec("2"));
    assertEquals(r, PluralType.TWO);

    r = EN.Numbers.getPluralOrdinal(dec("3"));
    assertEquals(r, PluralType.FEW);
}

  private Decimal dec(String repr) {
    return new Decimal(repr);
  }

}
