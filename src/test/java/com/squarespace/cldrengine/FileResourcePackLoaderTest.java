package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.api.AltType;
import com.squarespace.cldrengine.api.CLocale;
import com.squarespace.cldrengine.api.RegionIdType;
import com.squarespace.cldrengine.internal.DefaultResourcePackLoader;
import com.squarespace.cldrengine.internal.Vector2Arrow;

/**
 * Regression tests for {@link FileResourcePackLoader} cache semantics:
 * (a) compressed pack loading actually decompresses the .gz file,
 * (b) the pack cache is scoped to each loader instance (not shared globally),
 * (c) swapping the global loader via {@code CLDR.resourcePackLoader(...)}
 * invalidates previously built CLDR instances.
 */
public class FileResourcePackLoaderTest {

  /**
   * Loads a bundled resource pack for the given language and rewrites the
   * version field so the generated file is distinguishable.
   */
  private static String bundledPackJson(String language, String version) throws IOException {
    try (InputStream in = DefaultResourcePackLoader.class.getResourceAsStream(language + ".json")) {
      JsonObject obj = (JsonObject) JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      JsonObject copy = obj.deepCopy();
      copy.addProperty("version", version);
      return copy.toString();
    }
  }

  private static void writeFile(Path dir, String name, String content) throws IOException {
    Files.write(dir.resolve(name), content.getBytes(StandardCharsets.UTF_8));
  }

  private static void writeGzip(Path dir, String name, String content) throws IOException {
    try (GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(dir.resolve(name)))) {
      out.write(content.getBytes(StandardCharsets.UTF_8));
    }
  }

  private static Path tempDir() throws IOException {
    return Files.createTempDirectory("cldr-file-loader-test-" + UUID.randomUUID().toString().substring(0, 8));
  }

  private static void deleteRecursive(Path dir) throws IOException {
    if (!Files.exists(dir)) {
      return;
    }
    Files.walk(dir).sorted(Comparator.reverseOrder()).forEach(p -> {
      try {
        Files.deleteIfExists(p);
      } catch (IOException e) {
        // best-effort cleanup
      }
    });
  }

  /**
   * (a) Compressed loading: a loader with {@code compressed = true} must
   * decompress the .gz file and return the same pack content as the
   * uncompressed path. The gzip key is only ever loaded through the
   * compressed loader, so a broken gzip path cannot be masked by a
   * previously cached uncompressed pack.
   */
  @Test
  public void testCompressedLoading() throws IOException {
    Path dir = tempDir();
    try {
      String content = bundledPackJson("sv", "compressed");
      writeFile(dir, "uncompressed.json", content);
      writeGzip(dir, "gz.json.gz", content);

      ResourcePack raw = new FileResourcePackLoader(dir).get("uncompressed");
      ResourcePack gz = new FileResourcePackLoader(dir, true).get("gz");
      assertNotNull(raw);
      assertNotNull(gz);

      // Distinct loader instances must not alias cached packs.
      assertNotSame(gz, raw);

      // Compressed and uncompressed paths return identical content.
      assertEquals(gz.defaultTag().expanded(), "sv-Latn-SE");
      assertEquals(gz.language(), raw.language());
      assertEquals(gz.version(), raw.version());
      assertEquals(gz.cldrVersion(), raw.cldrVersion());
      assertEquals(gz.checksum(), raw.checksum());

      // Spot-check real content carried by the compressed pack.
      Vector2Arrow<AltType, RegionIdType> displayName =
          CLDR.get("sv").Schema.Names.regions.displayName;
      CLocale locale = CLDR.resolveLocale("sv-Latn-SE");
      assertEquals(displayName.get(gz.get(locale.tag()), AltType.NONE, RegionIdType.CI), "Elfenbenskusten");
      assertEquals(displayName.get(raw.get(locale.tag()), AltType.NONE, RegionIdType.CI),
          displayName.get(gz.get(locale.tag()), AltType.NONE, RegionIdType.CI));
    } finally {
      deleteRecursive(dir);
    }
  }

  /**
   * (b) Cache scoping: two loaders pointed at different directories must
   * each return their own directory's pack for the same language, and a
   * single loader instance must cache for itself only.
   */
  @Test
  public void testLoadersAreIsolated() throws IOException {
    Path dir1 = tempDir();
    Path dir2 = tempDir();
    try {
      writeFile(dir1, "en.json", bundledPackJson("en", "dir-one"));
      writeFile(dir2, "en.json", bundledPackJson("en", "dir-two"));

      ResourcePack one = new FileResourcePackLoader(dir1).get("en");
      ResourcePack two = new FileResourcePackLoader(dir2).get("en");
      assertNotNull(one);
      assertNotNull(two);

      // The second directory must not be silently ignored.
      assertNotSame(one, two);
      assertEquals(one.language(), "en");
      assertEquals(one.defaultTag().expanded(), "en-Latn-US");
      assertEquals(one.version(), "dir-one");
      assertEquals(two.version(), "dir-two");

      // Per-instance caching: the same loader instance returns the same pack.
      FileResourcePackLoader loader = new FileResourcePackLoader(dir1);
      assertSame(loader.get("en"), loader.get("en"));
    } finally {
      deleteRecursive(dir1);
      deleteRecursive(dir2);
    }
  }

  /**
   * (c) Loader swap: after replacing the global loader, a previously built
   * CLDR instance cache must be invalidated so {@code CLDR.get(...)} serves
   * packs from the new loader.
   */
  @Test
  public void testResourcePackLoaderSwapInvalidatesCache() throws IOException {
    CLDR sv = CLDR.get("sv");
    Vector2Arrow<AltType, RegionIdType> displayName = sv.Schema.Names.regions.displayName;
    assertEquals(displayName.get(sv.General.bundle(), AltType.NONE, RegionIdType.CI), "Elfenbenskusten");

    try {
      sv.resourcePackLoader(new FileResourcePackLoader(Paths.get("custom-packs")));

      // The cached "sv" instance must be rebuilt from the new loader.
      CLDR patched = CLDR.get("sv");
      assertNotSame(patched, sv);
      assertEquals(displayName.get(patched.General.bundle(), AltType.NONE, RegionIdType.CI), "Ivory Coast");
    } finally {
      // Restore the bundled default loader for other tests.
      sv.resourcePackLoader(new DefaultResourcePackLoader());
    }
  }

}
