package com.squarespace.cldrengine.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.squarespace.cldrengine.internal.StringEnum;

public enum UnitCategory implements StringEnum<UnitCategory> {

  ACCELERATION("acceleration"),
  ANGLE("angle"),
  AREA("area"),
  CONCENTR("concentr"),
  CONSUMPTION("consumption"),
  DIGITAL("digital"),
  DURATION("duration"),
  ELECTRIC("electric"),
  ENERGY("energy"),
  FORCE("force"),
  FREQUENCY("frequency"),
  GRAPHICS("graphics"),
  LENGTH("length"),
  LIGHT("light"),
  MASS("mass"),
  POWER("power"),
  PRESSURE("pressure"),
  SPEED("speed"),
  TEMPERATURE("temperature"),
  TORQUE("torque"),
  VOLUME("volume")
  ;

  private static final Map<String, UnitCategory> REVERSE = new HashMap<>();
  static {
    Arrays.stream(UnitCategory.values()).forEach(e -> REVERSE.put(e.value, e));
  }

  private final String value;

  private UnitCategory(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  public static UnitCategory fromString(String s) {
    return REVERSE.get(s);
  }
}
