package com.squarespace.cldrengine;

/**
 * Instantiates the resource pack for the given language. Note that any
 * exceptions encountered by the loader are fatal, since the library
 * cannot function without the packs. Such an error represents a failure
 * in configuration or packaging of the library within an application.
 */
public interface ResourcePackLoader {
  ResourcePack get(String language);
}
