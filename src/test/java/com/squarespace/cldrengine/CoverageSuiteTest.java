package com.squarespace.cldrengine;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * Regression test for the CoverageSuite.typeArray null-element handling.
 *
 * The helper checked {@code e.isJsonNull()} on the enclosing JSON array
 * instead of on the element, so any {@code null} entry in a suite options
 * array threw IllegalStateException ("Not a JSON Object: null") instead of
 * mapping to a Java null option. All the other element helpers
 * (typeValue, boolValue, ...) already check the element.
 */
public class CoverageSuiteTest {

  @Test
  public void testTypeArrayNullElement() {
    JsonArray arr = new JsonArray();
    arr.add(JsonNull.INSTANCE);

    JsonObject obj = new JsonObject();
    obj.addProperty("k", "v");
    arr.add(obj);

    // Element null -> null; non-null element -> applied through the function.
    assertEquals(
        CoverageSuite.typeArray(arr, e -> e.getAsJsonObject().get("k").getAsString()),
        Arrays.asList((String) null, "v"));
  }

}
