package com.squarespace.cldrengine.internal;

public class Internals {

  public final SchemaConfig config;
  public final boolean debug;

  public final String checksum;
  public final Schema schema;

  public Internals(SchemaConfig config, String version, boolean debug) {
    this.config = config;
    this.debug = debug;
    this.checksum = config.checksum(version);
    this.schema = Meta.SCHEMA;
  }

}
