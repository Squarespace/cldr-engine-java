package com.squarespace.cldrengine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;
import com.squarespace.cldrengine.utils.JsonUtils;

/**
 * Looks for resource packs in the path "<dir>/<language>.json" or with
 * a ".gz" extension if the compression flag is set.
 */
public class FileResourcePackLoader implements ResourcePackLoader {

  private static final ConcurrentHashMap<String, ResourcePack> PACKS = new ConcurrentHashMap<>();
  private final Path directory;
  private final boolean compressed;

  public FileResourcePackLoader(Path directory) {
    this(directory, false);
    System.out.println(directory.toAbsolutePath());
  }

  public FileResourcePackLoader(Path directory, boolean compressed) {
    this.directory = directory;
    this.compressed = compressed;
  }

  public ResourcePack get(String language) {
    return PACKS.computeIfAbsent(language, lang -> {
      String name = language + (compressed ? ".json.gz" : ".json");
      Path path = this.directory.resolve(name);
      try {
        JsonObject obj = JsonUtils.loadJsonFile(path.toString());
        return obj == null ? null : new ResourcePack(obj);
      } catch (IOException e) {
        throw new RuntimeException("Fatal: failed to load resource pack: '" + name + "'", e);
      }
    });
  }

}
