package com.squarespace.cldrengine.internal;

import java.util.Map;

import com.squarespace.cldrengine.numbering.NumberParams;
import com.squarespace.cldrengine.numbering.NumberParamsCache;

public class PrivateApi {

  private final Bundle bundle;
  private final Internals internals;
  private final NumberParamsCache numberParamsCache;
  private final Map<ContextTransformFieldType, String> contextTransforms;

  public PrivateApi(Bundle bundle, Internals internals) {
    this.bundle = bundle;
    this.internals = internals;
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
