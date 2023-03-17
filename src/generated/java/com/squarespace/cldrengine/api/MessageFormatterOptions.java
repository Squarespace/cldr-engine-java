package com.squarespace.cldrengine.api;

import lombok.Generated;
import lombok.EqualsAndHashCode;

@Generated
@EqualsAndHashCode
public class MessageFormatterOptions {

  public final Option<String> language = Option.option();
  public final Option<String> region = Option.option();
  public final Option<PluralRules> plurals = Option.option();
  public final Option<MessageArgConverter> converter = Option.option();
  public final Option<MessageFormatFuncMap> formatters = Option.option();
  public final Option<Integer> cacheSize = Option.option();
  public final Option<Boolean> disableEscapes = Option.option();

  public MessageFormatterOptions() {
  }

  public MessageFormatterOptions(MessageFormatterOptions arg) {
    this.language.set(arg.language);
    this.region.set(arg.region);
    this.plurals.set(arg.plurals);
    this.converter.set(arg.converter);
    this.formatters.set(arg.formatters);
    this.cacheSize.set(arg.cacheSize);
    this.disableEscapes.set(arg.disableEscapes);
  }

  public MessageFormatterOptions language(String arg) {
    this.language.set(arg);
    return this;
  }

  public MessageFormatterOptions language(Option<String> arg) {
    this.language.set(arg);
    return this;
  }

  public MessageFormatterOptions region(String arg) {
    this.region.set(arg);
    return this;
  }

  public MessageFormatterOptions region(Option<String> arg) {
    this.region.set(arg);
    return this;
  }

  public MessageFormatterOptions plurals(PluralRules arg) {
    this.plurals.set(arg);
    return this;
  }

  public MessageFormatterOptions plurals(Option<PluralRules> arg) {
    this.plurals.set(arg);
    return this;
  }

  public MessageFormatterOptions converter(MessageArgConverter arg) {
    this.converter.set(arg);
    return this;
  }

  public MessageFormatterOptions converter(Option<MessageArgConverter> arg) {
    this.converter.set(arg);
    return this;
  }

  public MessageFormatterOptions formatters(MessageFormatFuncMap arg) {
    this.formatters.set(arg);
    return this;
  }

  public MessageFormatterOptions formatters(Option<MessageFormatFuncMap> arg) {
    this.formatters.set(arg);
    return this;
  }

  public MessageFormatterOptions cacheSize(Integer arg) {
    this.cacheSize.set(arg);
    return this;
  }

  public MessageFormatterOptions cacheSize(Option<Integer> arg) {
    this.cacheSize.set(arg);
    return this;
  }

  public MessageFormatterOptions disableEscapes(Boolean arg) {
    this.disableEscapes.set(arg);
    return this;
  }

  public MessageFormatterOptions disableEscapes(Option<Boolean> arg) {
    this.disableEscapes.set(arg);
    return this;
  }

  public static MessageFormatterOptions build() {
    return new MessageFormatterOptions();
  }

  public MessageFormatterOptions copy() {
    return new MessageFormatterOptions(this);
  }

  public MessageFormatterOptions mergeIf(MessageFormatterOptions ...args) {
    MessageFormatterOptions o = new MessageFormatterOptions(this);
    for (MessageFormatterOptions arg : args) {
      o._mergeIf(arg);
    }
    return o;
  }

  protected void _mergeIf(MessageFormatterOptions o) {
    this.language.setIf(o.language);
    this.region.setIf(o.region);
    this.plurals.setIf(o.plurals);
    this.converter.setIf(o.converter);
    this.formatters.setIf(o.formatters);
    this.cacheSize.setIf(o.cacheSize);
    this.disableEscapes.setIf(o.disableEscapes);
  }

  public MessageFormatterOptions merge(MessageFormatterOptions ...args) {
    MessageFormatterOptions o = new MessageFormatterOptions(this);
    for (MessageFormatterOptions arg : args) {
      o._merge(arg);
    }
    return o;
  }

  protected void _merge(MessageFormatterOptions o) {
    this.language.set(o.language);
    this.region.set(o.region);
    this.plurals.set(o.plurals);
    this.converter.set(o.converter);
    this.formatters.set(o.formatters);
    this.cacheSize.set(o.cacheSize);
    this.disableEscapes.set(o.disableEscapes);
  }


  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("MessageFormatterOptions( ");
    this._tostring(buf);
    return buf.append(')').toString();
  }

  protected void _tostring(StringBuilder buf) {
    if (language.ok()) {
      buf.append("language=").append(language).append(' ');
    }
    if (region.ok()) {
      buf.append("region=").append(region).append(' ');
    }
    if (plurals.ok()) {
      buf.append("plurals=").append(plurals).append(' ');
    }
    if (converter.ok()) {
      buf.append("converter=").append(converter).append(' ');
    }
    if (formatters.ok()) {
      buf.append("formatters=").append(formatters).append(' ');
    }
    if (cacheSize.ok()) {
      buf.append("cacheSize=").append(cacheSize).append(' ');
    }
    if (disableEscapes.ok()) {
      buf.append("disableEscapes=").append(disableEscapes).append(' ');
    }
  }

}
