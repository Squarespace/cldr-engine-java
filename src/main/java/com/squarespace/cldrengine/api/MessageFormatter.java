package com.squarespace.cldrengine.api;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.squarespace.cldrengine.message.DefaultMessageArgConverter;
import com.squarespace.cldrengine.message.MessageCode;
import com.squarespace.cldrengine.message.MessageEngine;
import com.squarespace.cldrengine.message.MessagePatternParser;
import com.squarespace.cldrengine.plurals.Plurals;
import com.squarespace.cldrengine.utils.Cache;

/**
 * Convenience class that caches parsed messages.
 */
public class MessageFormatter {

  private static final MessageFormatFuncMap EMPTY = new MessageFormatFuncMap();
  private final PluralRules plurals;
  private final MessageArgConverter converter;
  private final MessageFormatFuncMap formatters;
  private final Collection<String> formatterNames;
  private final Cache<MessageCode> cache;

  public MessageFormatter(MessageFormatterOptions options) {
    options = (options == null ? MessageFormatterOptions.build() : options);
    String language = options.language.or("root");
    String region = options.region.get();
    this.plurals = options.plurals.or(Plurals.get(language, region));
    this.converter = options.converter.ok() ? options.converter.get() : new DefaultMessageArgConverter();
    this.formatters = options.formatters.or(EMPTY);
    List<String> names = this.formatters.keySet().stream()
        .sorted((a, b) -> Integer.compare(b.length(), a.length()))
        .collect(Collectors.toList());
    this.formatterNames = names;
    this.cache = new Cache<>(this::parse, options.cacheSize.or(256));
  }

  public String format(String message, MessageArgs args) {
    MessageCode code = this.cache.get(message);
    return new MessageEngine(this.plurals, this.converter, this.formatters, code).evaluate(args);
  }

  protected MessageCode parse(String raw) {
    return new MessagePatternParser(this.formatterNames, raw).parse();
  }
}
