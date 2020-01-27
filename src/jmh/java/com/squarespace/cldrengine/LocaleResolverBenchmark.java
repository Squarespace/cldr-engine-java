package com.squarespace.cldrengine;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Fork(1)
@Measurement(iterations = 5, time = 5)
@Warmup(iterations = 3, time = 2)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class LocaleResolverBenchmark {

  @Benchmark
  public void unknownLocale(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(CLDR.resolveLocale(state.unknown));
  }

  @Benchmark
  public void englishBare(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(CLDR.resolveLocale(state.english));
  }

  @Benchmark
  public void unitedStatesRegion(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(CLDR.resolveLocale(state.unitedstates));
  }

  @Benchmark
  public void complex(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(CLDR.resolveLocale(state.complex));
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {
    public final String unknown = "zz-Zzzz-ZZ";
    public final String english = "en";
    public final String unitedstates = "zz-US";
    public final String complex = "zh-Latn-AU-u-ca-buddhist-u-nu-beng";
  }
}
