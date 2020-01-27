package com.squarespace.cldrengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * High coverage by comparing output against the TypeScript library as
 * source of truth. The codegen tool generates files containing JSON-encoded
 * test cases, consisting of inputs and expected output. This package
 * reads those files, executes the corresponding API method and compares
 * the output.
 *
 * This allows us to achieve high test coverage at the API level without
 * having to duplicate all low-level test cases from the original library.
 */
public class CoverageSuite {

  public BufferedReader getTestCase(String name) throws IOException {
    Path root = Paths.get(System.getProperty("user.dir"), ".cldrsuite");
    Path path = root.resolve(name + ".txt");
    File file = path.toFile();
    if (!file.exists()) {
      throw new RuntimeException("Test suite must be generated using the codegen tool");
    }
    return new BufferedReader(new FileReader(file));
  }

}
