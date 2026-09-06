package com.squarespace.cldrengine.general;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.api.ContextType;
import com.squarespace.cldrengine.internal.Internals;
import com.squarespace.cldrengine.internal.MiscData;
import com.squarespace.cldrengine.internal.SchemaConfig;

/**
 * Regression test for GeneralInternals.contextTransform flag handling.
 *
 * TS v1.15.0 reads {@code flag[1]} which is {@code undefined}-safe for a
 * 1-char flag; the Java port called {@code flag.charAt(1)} and threw
 * StringIndexOutOfBoundsException for UI_LIST_OR_MENU transforms. All shipped
 * packs currently use 2-char flags, so this was latent.
 */
public class ContextTransformTest {

  private static final Internals INTERNALS = new Internals(new SchemaConfig(), MiscData.VERSION, false);
  private static final GeneralInternals GENERAL = new GeneralInternals(INTERNALS);

  private static Map<ContextTransformFieldType, String> flag(String s) {
    Map<ContextTransformFieldType, String> info = new HashMap<>();
    info.put(ContextTransformFieldType.LANGUAGES, s);
    return info;
  }

  @Test
  public void testOneCharFlagListOrMenu() {
    // 1-char flag: standalone bit set, ui-list-or-menu bit undefined.
    // TS: flag[1] === 'T' is false; the value must pass through unchanged
    // (previously Java threw StringIndexOutOfBoundsException).
    assertEquals(
        GENERAL.contextTransform("english", flag("T"), ContextType.UI_LIST_OR_MENU, ContextTransformFieldType.LANGUAGES),
        "english");
  }

  @Test
  public void testOneCharFlagStandalone() {
    // flag[0] === 'T' still title-cases standalone transforms.
    assertEquals(
        GENERAL.contextTransform("english", flag("T"), ContextType.STANDALONE, ContextTransformFieldType.LANGUAGES),
        "English");
  }

  @Test
  public void testTwoCharFlags() {
    // "TT": both bits set. "Tt": standalone only.
    assertEquals(
        GENERAL.contextTransform("english", flag("TT"), ContextType.STANDALONE, ContextTransformFieldType.LANGUAGES),
        "English");
    assertEquals(
        GENERAL.contextTransform("english", flag("TT"), ContextType.UI_LIST_OR_MENU, ContextTransformFieldType.LANGUAGES),
        "English");
    assertEquals(
        GENERAL.contextTransform("english", flag("tT"), ContextType.STANDALONE, ContextTransformFieldType.LANGUAGES),
        "english");
    assertEquals(
        GENERAL.contextTransform("english", flag("tT"), ContextType.UI_LIST_OR_MENU, ContextTransformFieldType.LANGUAGES),
        "English");
  }

  @Test
  public void testEmptyValueReturnsAsIs() {
    assertEquals(GENERAL.contextTransform("", flag("TT"), ContextType.BEGIN_SENTENCE, ContextTransformFieldType.LANGUAGES), "");
  }

}
