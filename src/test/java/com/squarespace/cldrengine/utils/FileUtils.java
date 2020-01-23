package com.squarespace.cldrengine.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class FileUtils {

  /**
   * Loads a resource from the Java package relative to {@code cls}
   */
  public static String loadResource(Class<?> cls, String path) throws FileNotFoundException {
    try (InputStream stream = cls.getResourceAsStream(path)) {
      if (stream == null) {
        throw new FileNotFoundException(path + "not found");
      }
      return IOUtils.toString(stream, "UTF-8");
    } catch (IOException e) {
      throw new FileNotFoundException(path + " " + e.toString());
    }
  }

}

