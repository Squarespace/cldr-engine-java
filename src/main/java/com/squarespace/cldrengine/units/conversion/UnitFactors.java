package com.squarespace.cldrengine.units.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.squarespace.cldrengine.api.Decimal;
import com.squarespace.cldrengine.api.Rational;
import com.squarespace.cldrengine.api.UnitType;
import com.squarespace.cldrengine.utils.Heap;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Build a graph of conversion factors between units. Sets of conversion
 * factors are closed, e.g. any factor must be convertable into any other
 * unit in the set. Their must be a connected path of length 1 or greater
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

  public final Set<UnitType> unitset = new HashSet<>();
  public final List<UnitType> units;
  private final List<FactorDef> factors;
  private final Map<UnitType, Map<UnitType, Rational>> graph = new EnumMap<>(UnitType.class);
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
      Map<UnitType, Rational> m = graph.get(src);
      buf.append(src.value()).append(":\n");
      if (m != null) {
        for (UnitType dst : m.keySet()) {
          buf.append("  ").append(dst.value()).append(" = ").append(m.get(dst)).append('\n');
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
    Rational fac = this.graph.get(src).get(dst);
    if (fac != null) {
      return new UnitConversion(Arrays.asList(src, dst), Arrays.asList(fac));
    }

    // Find the shortest, lowest-cost conversion path between
    // the two factors
    List<UnitType> path = this.shortestPath(src, dst);
    if (path != null) {
      // Multiply the factors together
      List<Rational> factors = new ArrayList<>();
      UnitType curr = path.get(0);
      for (int i = 1; i < path.size(); i++) {
        UnitType next = path.get(i);
        Rational nextfac = this.graph.get(curr).get(next);
        factors.add(nextfac);
        curr = next;
      }

      // Cache this conversion path
      ConcurrentMap<UnitType, UnitConversion> m = this.cache.computeIfAbsent(src, s -> new ConcurrentHashMap<>());
      return m.computeIfAbsent(dst, s -> new UnitConversion(path, factors));
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

      // Convert src -> dst
      Map<UnitType, Rational> m = this.graph.get(src);
      if (m == null) {
        m = new EnumMap<>(UnitType.class);
        this.graph.put(src, m);
      }
      m.put(dst, rat);

      // Convert dst -> src, if an explicit mapping does not already exist
      m = this.graph.get(dst);
      if (m == null) {
        m = new EnumMap<>(UnitType.class);
        this.graph.put(dst, m);
      }
      Rational tmp = m.get(src);
      if (tmp == null) {
        m.put(src, rat.inverse());
      }
    }
  }

  /**
   * Calculates the shortest path between a source and destination factor
   * using Dijkstra's algorithm. The cost of a path is the minimized
   * precision of the conversion factors.
   */
  private List<UnitType> shortestPath(UnitType src, UnitType dst) {
    Heap<Edge> heap = new Heap<>(new Edge[] { new Edge(src, 0) }, UnitFactors::compare);
    Map<UnitType, Cost> edges = new EnumMap<>(UnitType.class);
    edges.put(src, new Cost(0, null));

    while (!heap.empty()) {
      Edge e = heap.pop();
      UnitType edge = e.edge;
      int cost = e.cost;
      if (edge == dst) {
        break;
      }

      Map<UnitType, Rational> nbr = this.graph.get(edge);
      if (nbr == null) {
        continue;
      }

      for (UnitType n : nbr.keySet()) {
        Rational r = nbr.get(n);
        int newcost = cost + precision(r);
        Cost path = edges.get(n);
        if (path == null || newcost < path.cost) {
          edges.put(n, new Cost(newcost, edge));
          heap.push(new Edge(n, newcost));
        }
      }
    }

    Cost res = edges.get(dst);
    return res == null ? null : extractPath(edges, dst);
  }

  private static int compare(Edge a, Edge b) {
    return a.cost < b.cost ? -1 : a.cost > b.cost ? 1 : 0;
  }

  private static int precision(Rational r) {
    Decimal n = r.numerator();
    Decimal d = r.denominator();
    return (n.precision() + n.alignexp()) + (d.precision() + d.alignexp());
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
  private static class Edge {
    public UnitType edge;
    public int cost;
  }
}
