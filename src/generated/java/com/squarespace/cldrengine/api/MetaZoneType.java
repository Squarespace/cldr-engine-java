package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum MetaZoneType implements StringEnum<MetaZoneType> {

  ACRE("Acre"),
  AFGHANISTAN("Afghanistan"),
  AFRICA_CENTRAL("Africa_Central"),
  AFRICA_EASTERN("Africa_Eastern"),
  AFRICA_SOUTHERN("Africa_Southern"),
  AFRICA_WESTERN("Africa_Western"),
  ALASKA("Alaska"),
  ALMATY("Almaty"),
  AMAZON("Amazon"),
  AMERICA_CENTRAL("America_Central"),
  AMERICA_EASTERN("America_Eastern"),
  AMERICA_MOUNTAIN("America_Mountain"),
  AMERICA_PACIFIC("America_Pacific"),
  ANADYR("Anadyr"),
  APIA("Apia"),
  AQTAU("Aqtau"),
  AQTOBE("Aqtobe"),
  ARABIAN("Arabian"),
  ARGENTINA("Argentina"),
  ARGENTINA_WESTERN("Argentina_Western"),
  ARMENIA("Armenia"),
  ATLANTIC("Atlantic"),
  AUSTRALIA_CENTRAL("Australia_Central"),
  AUSTRALIA_CENTRALWESTERN("Australia_CentralWestern"),
  AUSTRALIA_EASTERN("Australia_Eastern"),
  AUSTRALIA_WESTERN("Australia_Western"),
  AZERBAIJAN("Azerbaijan"),
  AZORES("Azores"),
  BANGLADESH("Bangladesh"),
  BHUTAN("Bhutan"),
  BOLIVIA("Bolivia"),
  BRASILIA("Brasilia"),
  BRUNEI("Brunei"),
  CAPE_VERDE("Cape_Verde"),
  CASEY("Casey"),
  CHAMORRO("Chamorro"),
  CHATHAM("Chatham"),
  CHILE("Chile"),
  CHINA("China"),
  CHOIBALSAN("Choibalsan"),
  CHRISTMAS("Christmas"),
  COCOS("Cocos"),
  COLOMBIA("Colombia"),
  COOK("Cook"),
  CUBA("Cuba"),
  DAVIS("Davis"),
  DUMONTDURVILLE("DumontDUrville"),
  EAST_TIMOR("East_Timor"),
  EASTER("Easter"),
  ECUADOR("Ecuador"),
  EUROPE_CENTRAL("Europe_Central"),
  EUROPE_EASTERN("Europe_Eastern"),
  EUROPE_FURTHER_EASTERN("Europe_Further_Eastern"),
  EUROPE_WESTERN("Europe_Western"),
  FALKLAND("Falkland"),
  FIJI("Fiji"),
  FRENCH_GUIANA("French_Guiana"),
  FRENCH_SOUTHERN("French_Southern"),
  GMT("GMT"),
  GALAPAGOS("Galapagos"),
  GAMBIER("Gambier"),
  GEORGIA("Georgia"),
  GILBERT_ISLANDS("Gilbert_Islands"),
  GREENLAND_EASTERN("Greenland_Eastern"),
  GREENLAND_WESTERN("Greenland_Western"),
  GUAM("Guam"),
  GULF("Gulf"),
  GUYANA("Guyana"),
  HAWAII_ALEUTIAN("Hawaii_Aleutian"),
  HONG_KONG("Hong_Kong"),
  HOVD("Hovd"),
  INDIA("India"),
  INDIAN_OCEAN("Indian_Ocean"),
  INDOCHINA("Indochina"),
  INDONESIA_CENTRAL("Indonesia_Central"),
  INDONESIA_EASTERN("Indonesia_Eastern"),
  INDONESIA_WESTERN("Indonesia_Western"),
  IRAN("Iran"),
  IRKUTSK("Irkutsk"),
  ISRAEL("Israel"),
  JAPAN("Japan"),
  KAMCHATKA("Kamchatka"),
  KAZAKHSTAN_EASTERN("Kazakhstan_Eastern"),
  KAZAKHSTAN_WESTERN("Kazakhstan_Western"),
  KOREA("Korea"),
  KOSRAE("Kosrae"),
  KRASNOYARSK("Krasnoyarsk"),
  KYRGYSTAN("Kyrgystan"),
  LANKA("Lanka"),
  LINE_ISLANDS("Line_Islands"),
  LORD_HOWE("Lord_Howe"),
  MACAU("Macau"),
  MACQUARIE("Macquarie"),
  MAGADAN("Magadan"),
  MALAYSIA("Malaysia"),
  MALDIVES("Maldives"),
  MARQUESAS("Marquesas"),
  MARSHALL_ISLANDS("Marshall_Islands"),
  MAURITIUS("Mauritius"),
  MAWSON("Mawson"),
  MEXICO_NORTHWEST("Mexico_Northwest"),
  MEXICO_PACIFIC("Mexico_Pacific"),
  MONGOLIA("Mongolia"),
  MOSCOW("Moscow"),
  MYANMAR("Myanmar"),
  NAURU("Nauru"),
  NEPAL("Nepal"),
  NEW_CALEDONIA("New_Caledonia"),
  NEW_ZEALAND("New_Zealand"),
  NEWFOUNDLAND("Newfoundland"),
  NIUE("Niue"),
  NORFOLK("Norfolk"),
  NORONHA("Noronha"),
  NORTH_MARIANA("North_Mariana"),
  NOVOSIBIRSK("Novosibirsk"),
  OMSK("Omsk"),
  PAKISTAN("Pakistan"),
  PALAU("Palau"),
  PAPUA_NEW_GUINEA("Papua_New_Guinea"),
  PARAGUAY("Paraguay"),
  PERU("Peru"),
  PHILIPPINES("Philippines"),
  PHOENIX_ISLANDS("Phoenix_Islands"),
  PIERRE_MIQUELON("Pierre_Miquelon"),
  PITCAIRN("Pitcairn"),
  PONAPE("Ponape"),
  PYONGYANG("Pyongyang"),
  QYZYLORDA("Qyzylorda"),
  REUNION("Reunion"),
  ROTHERA("Rothera"),
  SAKHALIN("Sakhalin"),
  SAMARA("Samara"),
  SAMOA("Samoa"),
  SEYCHELLES("Seychelles"),
  SINGAPORE("Singapore"),
  SOLOMON("Solomon"),
  SOUTH_GEORGIA("South_Georgia"),
  SURINAME("Suriname"),
  SYOWA("Syowa"),
  TAHITI("Tahiti"),
  TAIPEI("Taipei"),
  TAJIKISTAN("Tajikistan"),
  TOKELAU("Tokelau"),
  TONGA("Tonga"),
  TRUK("Truk"),
  TURKMENISTAN("Turkmenistan"),
  TUVALU("Tuvalu"),
  URUGUAY("Uruguay"),
  UZBEKISTAN("Uzbekistan"),
  VANUATU("Vanuatu"),
  VENEZUELA("Venezuela"),
  VLADIVOSTOK("Vladivostok"),
  VOLGOGRAD("Volgograd"),
  VOSTOK("Vostok"),
  WAKE("Wake"),
  WALLIS("Wallis"),
  YAKUTSK("Yakutsk"),
  YEKATERINBURG("Yekaterinburg")
  ;

  private static final Map<String, MetaZoneType> REVERSE = new HashMap<>();
  static {
    Arrays.stream(MetaZoneType.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private MetaZoneType(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static MetaZoneType fromString(String s) {
    return REVERSE.get(s);
  }
}
