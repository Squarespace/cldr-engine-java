package com.squarespace.cldrengine.units.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.decimal.RationalConstants;
import com.squarespace.cldrengine.utils.Heap;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Build a graph of conversion factors between units. Sets of conversion
 * factors are closed, e.g. any factor must be convertible into any other
 * unit in the set. There must be a connected path of length 1 or greater
 * between any two units.
 *
 * Gaps in the graph represent conversion factors that are unknown at the
 * time of graph construction. These are incrementally filled in on demand
 * by finding the shortest and lowest-cost conversion path between two units
 * with no direct conversion factor. The path is transformed into a direct
 * conversion factor by multiplying all factors along the path. Finally the
 * new factor is added to the graph.
 */
public class UnitFactors {

  private static final Rational ONE = new Rational("1");
  private static final Rational ZERO = RationalConstants.ZERO;

  public final Set<UnitType> unitset = new HashSet<>();
  public final List<UnitType> units;
  private final List<FactorDef> factors;
  // Inner maps are insertion-ordered (LinkedHashMap) to mirror the TS plain-object
  // graph: JS keys iterate in insertion order, and shortestPath relaxation order
  // acts as the tie-break between equal-cost paths (see foot -> yard in LENGTH).
  private final Map<UnitType, Map<UnitType, Edge>> graph = new LinkedHashMap<>();
  private final ConcurrentMap<UnitType, ConcurrentMap<UnitType, UnitConversion>> cache =
      new ConcurrentHashMap<>();

  public UnitFactors(List<FactorDef> factors) {
    this(factors, false);
  }

  /**
   * Build a unit converter graph using the given factors.
   */
  public UnitFactors(List<FactorDef> factors, boolean memoize) {
    this.factors = factors;
    int size = factors.size();
    for (int i = 0; i < size; i++) {
      FactorDef factor = factors.get(i);
      this.unitset.add(factor.src);
      this.unitset.add(factor.dst);
    }
    this.units = new ArrayList<>(this.unitset);
    this.units.sort((a, b) -> a.value().compareTo(b.value()));
    this.init();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (UnitType src : graph.keySet()) {
      Map<UnitType, Edge> m = graph.get(src);
      buf.append(src.value()).append(":\n");
      if (m != null) {
        for (UnitType dst : m.keySet()) {
          Edge e = m.get(dst);
          buf.append("  ").append(dst.value()).append(" = ").append(e.factor);
          if (e.offset.compare(ZERO, null) != 0) {
            buf.append(" + ").append(e.offset);
          }
          buf.append('\n');
        }
      }
    }
    return buf.toString();
  }

  /**
   * Return the factor that converts units of 'src' into 'dst'.
   */
  public UnitConversion get(UnitType src, UnitType dst) {
    // Units are the same, conversion is 1
    if (src == dst) {
      return new UnitConversion(Arrays.asList(src, dst), Arrays.asList(ONE));
    }

    // See if a direct conversion exists.
    Edge e = this.graph.get(src).get(dst);
    if (e != null) {
      Rational offset = e.offset.compare(ZERO, null) != 0 ? e.offset : null;
      return new UnitConversion(Arrays.asList(src, dst), Arrays.asList(e.factor), offset);
    }

    // Find the shortest, lowest-cost conversion path between
    // the two factors
    List<UnitType> path = this.shortestPath(src, dst);
    if (path != null) {
      // Collect the conversion factors, composing the additive
      // offset: after each step `offset = offset × factor + a`
      List<Rational> factors = new ArrayList<>();
      Rational offset = ZERO;
      UnitType curr = path.get(0);
      for (int i = 1; i < path.size(); i++) {
        UnitType next = path.get(i);
        Edge e1 = this.graph.get(curr).get(next);
        factors.add(e1.factor);
        offset = offset.multiply(e1.factor, null).add(e1.offset, null);
        curr = next;
      }
      if (offset.compare(ZERO, null) == 0) {
        offset = null;
      }

      // Cache this conversion path
      final Rational offsetFinal = offset;
      ConcurrentMap<UnitType, UnitConversion> m = this.cache.computeIfAbsent(src, s -> new ConcurrentHashMap<>());
      return m.computeIfAbsent(dst, s -> new UnitConversion(path, factors, offsetFinal));
    }

    // No conversion factor exists
    return null;
  }

  /**
   * Constructing the initial factor graph.
   */
  private void init() {
    for (FactorDef factor : this.factors) {
      UnitType src = factor.src;
      UnitType dst = factor.dst;
      Rational rat = new Rational(factor.factor);
      Rational off = factor.offset == null ? ZERO : new Rational(factor.offset);

      // Convert src -> dst
      Map<UnitType, Edge> m = this.graph.get(src);
      if (m == null) {
        m = new LinkedHashMap<>();
        this.graph.put(src, m);
      }
      m.put(dst, new Edge(rat, off));

      // Convert dst -> src, if an explicit mapping does not already exist.
      // Inverting `dst = src × rat + off` gives `src = dst × (1/rat) − off/rat`.
      m = this.graph.get(dst);
      if (m == null) {
        m = new LinkedHashMap<>();
        this.graph.put(dst, m);
      }
      Edge tmp = m.get(src);
      if (tmp == null) {
        m.put(src, new Edge(rat.inverse(), off.divide(rat, null).negate()));
      }
    }
  }

  /**
   * Calculates the shortest path between a source and destination factor
   * using Dijkstra's algorithm. The cost of a path is the minimized
   * precision of the conversion factors.
   */
  private List<UnitType> shortestPath(UnitType src, UnitType dst) {
    Heap<HeapEntry> heap = new Heap<>(new HeapEntry[] { new HeapEntry(src, 0) }, UnitFactors::compare);
    Map<UnitType, Cost> edges = new EnumMap<>(UnitType.class);
    edges.put(src, new Cost(0, null));

    while (!heap.empty()) {
      HeapEntry e = heap.pop();
      UnitType edge = e.edge;
      int cost = e.cost;
      if (edge == dst) {
        break;
      }

      Map<UnitType, Edge> nbr = this.graph.get(edge);
      if (nbr == null) {
        continue;
      }

      for (UnitType n : nbr.keySet()) {
        Edge edge2 = nbr.get(n);
        int newcost = cost + precision(edge2.factor);
        Cost path = edges.get(n);
        if (path == null || newcost < path.cost) {
          edges.put(n, new Cost(newcost, edge));
          heap.push(new HeapEntry(n, newcost));
        }
      }
    }

    Cost res = edges.get(dst);
    return res == null ? null : extractPath(edges, dst);
  }

  private static int compare(HeapEntry a, HeapEntry b) {
    return a.cost < b.cost ? -1 : a.cost > b.cost ? 1 : 0;
  }

  private static int precision(Rational r) {
    Decimal n = r.numerator();
    Decimal d = r.denominator();
    // Absolute magnitudes keep the cost non-negative (Dijkstra's algorithm
    // requires non-negative edge costs; e.g. `0.001` must not cost less than
    // `1000`). Mirrors TS commit f3f8f271.
    return Math.abs(n.precision() + n.alignexp()) + Math.abs(d.precision() + d.alignexp());
  }

  private static List<UnitType> extractPath(Map<UnitType, Cost> edges, UnitType dst) {
    List<UnitType> r = new ArrayList<>();
    for (UnitType c = dst; c != null; c = edges.get(c).previous) {
      r.add(c);
    }
    Collections.reverse(r);
    return r;
  }

  @AllArgsConstructor
  @ToString
  private static class Cost {
    public int cost;
    public UnitType previous;
  }

  @AllArgsConstructor
  @ToString
  private static class HeapEntry {
    public UnitType edge;
    public int cost;
  }

  /**
   * An edge converts `src` to `dst` as `dst = src × factor + offset`
   * (mirrors TS `Edge = [Rational, Rational]`).
   */
  @AllArgsConstructor
  @ToString
  private static class Edge {
    public Rational factor;
    public Rational offset;
  }
}

