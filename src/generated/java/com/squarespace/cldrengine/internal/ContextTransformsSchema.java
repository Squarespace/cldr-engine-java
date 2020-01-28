package com.squarespace.cldrengine.internal;


import com.squarespace.cldrengine.api.ContextTransformFieldType;

public class ContextTransformsSchema {

  public final Vector1Arrow<ContextTransformFieldType> contextTransforms;

  public ContextTransformsSchema(
      Vector1Arrow<ContextTransformFieldType> contextTransforms) {
    this.contextTransforms = contextTransforms;
  }

}
