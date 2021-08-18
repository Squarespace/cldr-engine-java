package com.squarespace.cldrengine.plurals;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.PluralType;

public class PluralTest {

  @Test
  public void testFrenchCompact() {
    assertEquals(cardinal("fr", "1.2"), PluralType.ONE);
    assertEquals(cardinal("fr", "1.7"), PluralType.ONE);

    assertEquals(cardinal("fr", "100000"), PluralType.OTHER);
    assertEquals(cardinal("fr", "1000000"), PluralType.MANY);

    // Compact format specifies the compact shifted exponent
    assertEquals(cardinal("fr", "100000", 5), PluralType.OTHER);
    assertEquals(cardinal("fr", "1000000", 6), PluralType.MANY);
    assertEquals(cardinal("fr", "30000001", 6), PluralType.MANY);
  }

  private PluralType cardinal(String lang, String n) {
    return cardinal(lang, n, 0);
  }

  private PluralType cardinal(String lang, String n, int c) {
    PluralRulesImpl rules = Plurals.get(lang, null);
    return rules.cardinal(new Decimal(n), c);
  }
}
