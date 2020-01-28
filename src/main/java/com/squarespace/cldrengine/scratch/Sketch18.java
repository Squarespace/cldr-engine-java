package com.squarespace.cldrengine.scratch;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.squarespace.cldrengine.CLDR;
import com.squarespace.cldrengine.api.CLocale;

public class Sketch18 {

  public static void main(String[] args) throws Exception {

    int[] packcounts = new int[] {1, 4, 16, 64, 128, 256};
    report(0);
    List<String> rawids = CLDR.availableLocales();
    List<CLocale> ids = rawids.stream()
        .map(i -> CLDR.resolveLocale(i))
        .collect(Collectors.toList());;
    int iters = 1000;

    // preload
    for (int i = 0; i < ids.size(); i++) {
      CLDR.get(ids.get(i));
    }

//    for (int packs : packcounts) {
//      System.out.println("\n============== " + packs + "  packs  " + (iters * packs) + " iters");
//      long start = System.currentTimeMillis();
//      for (int j = 0; j < packs; j++) {
//        String id = rawids.get(j);
//        for (int i = 0; i < iters; i++) {
//          CLDR.resolveLocale(id);
//        }
//      }
//      long elapsed = System.currentTimeMillis() - start;
//      report(elapsed);
//    }

    ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    for (int packs : packcounts) {
      System.out.println("\n============== " + packs + "  packs  " + (iters * packs) + " iters");
      long start = System.currentTimeMillis();
      for (int j = 0; j < packs; j++) {
        String id = rawids.get(j);
        for (int i = 0; i < iters; i++) {
          map.computeIfAbsent(id, x -> x);
        }
      }
      long elapsed = System.currentTimeMillis() - start;
      report(elapsed);
    }

//    System.exit(1);;

    for (int packs : packcounts) {
      System.out.println("\n============== " + packs + "  packs  " + (iters * packs) + " iters");
      for (int j = 0; j < 10; j++) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < iters; i++) {
          for (int k = 0; k < packs; k++) {
            CLDR.get(ids.get(k));
          }
        }
        long elapsed = System.currentTimeMillis() - start;
        report(elapsed);
      }
      for (int i = 0; i < 5; i++) {
        report(0);
        Thread.sleep(50);
      }
    }
    System.out.println("\n=============== EXIT");
    for (int i = 0; i < ids.size(); i++) {
      CLDR.get(ids.get(i));
    }
    report(0);
  }

  private static final double MB = 1024 * 1024;

  public static void report(long elapsed) {
    Runtime rt = Runtime.getRuntime();
    double used = (rt.totalMemory() - rt.freeMemory()) / MB;
    String prefix = "";
    if (elapsed > 0) {
      prefix = "Elapsed " + elapsed + "   ";
    }
    System.out.println(String.format("%s   Free: %d bytes, Total: %d bytes, Max: %d bytes  Used: %.3f MB",
        prefix, rt.freeMemory(), rt.totalMemory(), rt.maxMemory(), used));
    rt.gc();
    rt.gc();
  }
}
