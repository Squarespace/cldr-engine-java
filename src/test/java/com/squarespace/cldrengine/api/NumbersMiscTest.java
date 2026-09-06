package com.squarespace.cldrengine.api;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.CLDR;

/**
 * Port of Numbers.parseDecimal (packages/cldr-core/src/api/numbers.ts) and the
 * TS top-level currency exports (packages/cldr-core/src/index.ts:13, tag
 * v1.15.0): getCurrencyFractions and getCurrencyForRegion.
 *
 * The TS exports are top-level module functions with no resource-pack
 * dependency, so the Java statics live on {@link CLDR} (mirroring
 * parseLanguageTag and friends).
 *
 * Expected values were verified against the published 1.15.0 engine
 * (@phensley/cldr in codegen/node_modules):
 *   getCurrencyForRegion('ZZ') -> USD, 'US' -> USD, 'JP' -> JPY, 'XX' -> USD
 *   getCurrencyFractions('JPY') -> {digits: 0, rounding: 0, cashDigits: 0, cashRounding: 0}
 *   getCurrencyFractions('USD') -> {digits: 2, rounding: 0, cashDigits: 2, cashRounding: 0}
 */
public class NumbersMiscTest {

  private static final CLDR EN = CLDR.get("en");

  @Test
  public void testParseDecimal() {
    Decimal num = EN.Numbers.parseDecimal("1234.5");
    assertEquals(num, new Decimal("1234.5"));
    assertEquals(num.toString(), "1234.5");

    // "-0" signum behavior must match the Decimal constructor exactly.
    Decimal viaCtor = new Decimal("-0");
    Decimal viaParse = EN.Numbers.parseDecimal("-0");
    assertEquals(viaParse.signum(), viaCtor.signum());
    assertEquals(viaParse.signum(), 0);
    assertEquals(viaParse.isNegative(), viaCtor.isNegative());
    assertEquals(viaParse.isNegative(), true);
  }

  @Test
  public void testGetCurrencyForRegion() {
    // The 'ZZ' region is present in the data (mapped to XAD) but the TS
    // export resolves it to USD.
    assertEquals(CLDR.getCurrencyForRegion("ZZ"), CurrencyType.USD);
    assertEquals(CLDR.getCurrencyForRegion("US"), CurrencyType.USD);
    assertEquals(CLDR.getCurrencyForRegion("JP"), CurrencyType.JPY);
    assertEquals(CLDR.getCurrencyForRegion("GB"), CurrencyType.GBP);

    // Unknown regions fall back to USD.
    assertEquals(CLDR.getCurrencyForRegion("XX"), CurrencyType.USD);
  }

  @Test
  public void testInstanceGetCurrencyForRegionMatchesStatic() {
    // Regression: the protected INSTANCE method used
    // CURRENCY_REGIONS.getOrDefault(region, USD), which returned null for
    // 'ZZ' (the data maps ZZ->XAD but XAD is absent from the CurrencyType
    // enum, and getOrDefault only substitutes on a MISSING key) while the
    // TS export resolves both ZZ and missing entries to USD.
    String[] regions = { "ZZ", "US", "JP", "GB", "XX", "AU" };
    for (String region : regions) {
      CurrencyType instance = EN.Numbers.getCurrencyForRegion(region);
      CurrencyType staticRes = CLDR.getCurrencyForRegion(region);
      assertEquals(instance, staticRes, "mismatch for region " + region);
    }

    assertEquals(EN.Numbers.getCurrencyForRegion("ZZ"), CurrencyType.USD);
    assertEquals(EN.Numbers.getCurrencyForRegion("US"), CurrencyType.USD);
    assertEquals(EN.Numbers.getCurrencyForRegion("JP"), CurrencyType.JPY);
    assertEquals(EN.Numbers.getCurrencyForRegion("GB"), CurrencyType.GBP);
    assertEquals(EN.Numbers.getCurrencyForRegion("XX"), CurrencyType.USD);
  }

  @Test
  public void testGetCurrencyFractions() {
    // JPY has no explicit fractional digits in the autogen data.
    CurrencyFractions jpy = CLDR.getCurrencyFractions(CurrencyType.JPY);
    assertEquals(jpy.digits, 0);
    assertEquals(jpy.rounding, 0);
    assertEquals(jpy.cashDigits, 0);
    assertEquals(jpy.cashRounding, 0);

    // USD has no explicit entry, so the default fractions are returned.
    CurrencyFractions usd = CLDR.getCurrencyFractions(CurrencyType.USD);
    assertEquals(usd.digits, 2);
    assertEquals(usd.rounding, 0);
    assertEquals(usd.cashDigits, 2);
    assertEquals(usd.cashRounding, 0);
  }
}
