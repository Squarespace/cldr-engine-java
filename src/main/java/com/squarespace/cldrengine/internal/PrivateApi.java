package com.squarespace.cldrengine.internal;

import java.util.Map;

import com.squarespace.cldrengine.api.Bundle;
import com.squarespace.cldrengine.api.ContextTransformFieldType;
import com.squarespace.cldrengine.numbers.NumberParams;
import com.squarespace.cldrengine.numbers.NumberParamsCache;

public class PrivateApi {

  private final NumberParamsCache numberParamsCache;
  private final Map<ContextTransformFieldType, String> contextTransforms;

  public PrivateApi(Bundle bundle, Internals internals) {
    this.numberParamsCache = new NumberParamsCache(bundle, internals);
    this.contextTransforms = internals.schema.ContextTransforms.contextTransforms.mapping(bundle);
  }

  public NumberParams getNumberParams(String numberSystem, String defaultSystem) {
    return this.numberParamsCache.getNumberParams(numberSystem, defaultSystem);
  }

  public Map<ContextTransformFieldType, String> getContextTransformInfo() {
    return this.contextTransforms;
  }
}
