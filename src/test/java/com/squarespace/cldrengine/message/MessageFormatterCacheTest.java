package com.squarespace.cldrengine.message;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;

import org.testng.annotations.Test;

import com.squarespace.cldrengine.api.MessageArgs;
import com.squarespace.cldrengine.api.MessageFormatter;
import com.squarespace.cldrengine.api.MessageFormatterOptions;
import com.squarespace.cldrengine.utils.Cache;

/**
 * Pins the effective default MessageFormatter cache size at 100
 * (TS DEFAULT_CACHE_SIZE), matched via options.cacheSize.or(100).
 * The cache field is private, so it is inspected via reflection
 * (no public API added), reflecting the codebase's existing
 * reflection-based test style (see LRUTest).
 */
public class MessageFormatterCacheTest {

  private static Cache<?> cacheOf(MessageFormatter formatter) throws Exception {
    Field cacheField = MessageFormatter.class.getDeclaredField("cache");
    cacheField.setAccessible(true);
    return (Cache<?>) cacheField.get(formatter);
  }

  @Test
  public void testDefaultCacheSizeIs100() throws Exception {
    MessageFormatter formatter = new MessageFormatter(
        MessageFormatterOptions.build().language("en").region("US"));
    MessageArgs args = new MessageArgs();

    // Load 150 distinct messages. With the effective default capacity of
    // 100 the LRU holds exactly the 100 most recent entries.
    for (int i = 0; i < 150; i++) {
      formatter.format("msg " + i, args);
    }
    assertEquals(cacheOf(formatter).size(), 100);

    // Evicted entries rebuild on re-get and format identically.
    assertEquals(formatter.format("msg 5", args), "msg 5");
    // Recently-cached entries are still held (no rebuild).
    assertEquals(formatter.format("msg 149", args), "msg 149");
    assertEquals(cacheOf(formatter).size(), 100);
  }
}
