package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

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
    ResourcePackLoader rawLoader = new FileResourcePackLoader(Paths.get("custom-packs"));
    ResourcePack raw = rawLoader.get("sv");

    ResourcePackLoader gzipLoader = new FileResourcePackLoader(Paths.get("custom-packs"), true);
    ResourcePack gzip = gzipLoader.get("sv");

    // Fresh loader instances must not share cached packs: the compressed
    // loader genuinely reads sv.json.gz (a static cache would return the
    // uncompressed pack and mask a broken gzip path).
    assertNotSame(gzip, raw);
    assertEquals(gzip.defaultTag().expanded(), "sv-Latn-SE");
    assertEquals(gzip.version(), raw.version());
    assertEquals(gzip.checksum(), raw.checksum());
  }

  @Test
  public void testPatchedResourcePack() {
    CLDR cldr = CLDR.get("sv");
    String name;

    // Default name for "Ivory Coast" in Swedish
    Vector2Arrow<AltType, RegionIdType> displayName = cldr.Schema.Names.regions.displayName;
    name = displayName.get(cldr.General.bundle(), AltType.NONE, RegionIdType.CI);
    assertEquals(name, "Elfenbenskusten");

    // Two fresh loader instances over the same directory must not return the
    // same cached pack (a static cache would alias them).
    ResourcePack a = new FileResourcePackLoader(Paths.get("custom-packs")).get("sv");
    ResourcePack b = new FileResourcePackLoader(Paths.get("custom-packs")).get("sv");
    assertNotSame(a, b);
    assertEquals(a.defaultTag().expanded(), b.defaultTag().expanded());
    assertEquals(a.checksum(), b.checksum());

    // Name for "Ivory Coast" in Swedish in our patched resource pack
    ResourcePack pack = new FileResourcePackLoader(Paths.get("custom-packs")).get("sv");
    CLocale locale = CLDR.resolveLocale("sv-Latn-SE");
    Bundle bundle = pack.get(locale.tag());
    name = displayName.get(bundle, AltType.NONE, RegionIdType.CI);
    assertEquals(name, "Ivory Coast");
  }

}
