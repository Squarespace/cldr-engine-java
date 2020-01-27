package com.squarespace.cldrengine;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import com.squarespace.cldrengine.LocaleResolverBenchmark.BenchmarkState;

@Measurement(iterations = 5, time = 5)
@Warmup(iterations = 3, time = 2)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class LanguageTagParserBenchmark {

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

}
