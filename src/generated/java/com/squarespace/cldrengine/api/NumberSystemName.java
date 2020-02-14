package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import com.squarespace.cldrengine.internal.StringEnum;

@Generated
public enum NumberSystemName implements StringEnum<NumberSystemName> {

  ADLM("adlm"),
  AHOM("ahom"),
  ARAB("arab"),
  ARABEXT("arabext"),
  BALI("bali"),
  BENG("beng"),
  BHKS("bhks"),
  BRAH("brah"),
  CAKM("cakm"),
  CHAM("cham"),
  DEVA("deva"),
  FULLWIDE("fullwide"),
  GONM("gonm"),
  GUJR("gujr"),
  GURU("guru"),
  HANIDEC("hanidec"),
  HMNG("hmng"),
  JAVA("java"),
  KALI("kali"),
  KHMR("khmr"),
  KNDA("knda"),
  LANA("lana"),
  LANATHAM("lanatham"),
  LAOO("laoo"),
  LATN("latn"),
  LEPC("lepc"),
  LIMB("limb"),
  MATHBOLD("mathbold"),
  MATHDBL("mathdbl"),
  MATHMONO("mathmono"),
  MATHSANB("mathsanb"),
  MATHSANS("mathsans"),
  MLYM("mlym"),
  MODI("modi"),
  MONG("mong"),
  MROO("mroo"),
  MTEI("mtei"),
  MYMR("mymr"),
  MYMRSHAN("mymrshan"),
  MYMRTLNG("mymrtlng"),
  NEWA("newa"),
  NKOO("nkoo"),
  OLCK("olck"),
  ORYA("orya"),
  OSMA("osma"),
  SAUR("saur"),
  SHRD("shrd"),
  SIND("sind"),
  SINH("sinh"),
  SORA("sora"),
  SUND("sund"),
  TAKR("takr"),
  TALU("talu"),
  TAMLDEC("tamldec"),
  TELU("telu"),
  THAI("thai"),
  TIBT("tibt"),
  TIRH("tirh"),
  VAII("vaii"),
  WARA("wara")
  ;

  private static final Map<String, NumberSystemName> REVERSE = new HashMap<>();
  static {
    Arrays.stream(NumberSystemName.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private NumberSystemName(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static NumberSystemName fromString(String s) {
    return REVERSE.get(s);
  }
}
