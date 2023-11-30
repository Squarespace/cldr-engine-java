package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.nio.file.Paths;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.RegionIdType;
import com.squarespace.cldrengine.internal.Vector2Arrow;

public class CustomResourcePackLoaderTest {

  @Test
  public void testLoadResource() {
    ResourcePackLoader loader = new FileResourcePackLoader(Paths.get("custom-packs"));
    ResourcePack pack = loader.get("sv");
    assertEquals(pack.defaultTag().expanded(), "sv-Latn-SE");
  }

  @Test
  public void testLoadResourceGzip() {
    ResourcePackLoader loader = new FileResourcePackLoader(Paths.get("custom-packs"), true);
    ResourcePack pack = loader.get("sv");
    assertEquals(pack.defaultTag().expanded(), "sv-Latn-SE");
  }

  @Test
  public void testPatchedResourcePack() {
    CLDR cldr = CLDR.get("sv");
    String name;

    // Default name for "Ivory Coast" in Swedish
    Vector2Arrow<AltType, RegionIdType> displayName = cldr.Schema.Names.regions.displayName;
    name = displayName.get(cldr.General.bundle(), AltType.NONE, RegionIdType.CI);
    assertEquals(name, "Elfenbenskusten");

    // Name for "Ivory Coast" in Swedish in our patched resource pack
    ResourcePackLoader loader = new FileResourcePackLoader(Paths.get("custom-packs"));
    ResourcePack pack = loader.get("sv");
    CLocale locale = CLDR.resolveLocale("sv-Latn-SE");
    Bundle bundle = pack.get(locale.tag());
    name = displayName.get(bundle, AltType.NONE, RegionIdType.CI);
    assertEquals(name, "Ivory Coast");
  }

}
