package com.squarespace.cldrengine.plurals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squarespace.cldrengine.internal.PluralExternalData;

import lombok.experimental.UtilityClass;

@UtilityClass
class PluralData {

  public static final Map<String, Rule[]> CARDINALS;
  public static final Map<String, Rule[]> ORDINALS;
  public static final List<Expr> EXPRESSIONS;

  static {
    JsonObject cardinals = JsonParser.parseString(PluralExternalData.CARDINALRULES).getAsJsonObject();
    CARDINALS = decodeRules(cardinals);
    JsonObject ordinals = JsonParser.parseString(PluralExternalData.ORDINALRULES).getAsJsonObject();
    ORDINALS = decodeRules(ordinals);
    JsonArray expressions = JsonParser.parseString(PluralExternalData.EXPRESSIONS).getAsJsonArray();
    EXPRESSIONS = decodeExpressions(expressions);
  }

  private static Map<String, Rule[]> decodeRules(JsonObject set) {
    Map<String, Rule[]> res = new HashMap<>();
    for (String id : set.keySet()) {
      List<Rule> rules = new ArrayList<>();
      JsonArray a1 = set.get(id).getAsJsonArray();
      for (int i = 0; i < a1.size(); i++) {
        JsonArray a2 = a1.get(i).getAsJsonArray();
        int index = a2.get(0).getAsInt();
        int[][] indices = intArrayArray(a2.get(1).getAsJsonArray());
        Rule rule = new Rule(index, indices);
        rules.add(rule);
      }
      res.put(id, rules.toArray(new Rule[] {}));
    }
    return res;
  }

  private static List<Expr> decodeExpressions(JsonArray arr1) {
    List<Expr> res = new ArrayList<>();
    for (int i = 0; i < arr1.size(); i++) {
      JsonArray raw = arr1.get(i).getAsJsonArray();
      char operand = raw.get(0).getAsString().charAt(0);
      int mod = raw.get(1).getAsInt();
      int relop = raw.get(2).getAsInt();

      // Decode rangelist
      JsonArray arr2 = raw.get(3).getAsJsonArray();
      List<Object> rangelist = new ArrayList<>();
      for (int j = 0; j < arr2.size(); j++) {
        if (arr2.get(j).isJsonArray()) {
          int[] range = intArray(arr2.get(j).getAsJsonArray());
          rangelist.add(range);
        } else {
          rangelist.add(arr2.get(j).getAsInt());
        }
      }
      res.add(new Expr(operand, mod, relop, rangelist));
    }
    return res;
  }

  private static int[] intArray(JsonArray arr) {
    List<Integer> inner = new ArrayList<>();
    for (int j = 0; j < arr.size(); j++) {
      int e = arr.get(j).getAsInt();
      inner.add(e);
    }
    return inner.stream().mapToInt(e -> e).toArray();
  }

  private static int[][] intArrayArray(JsonArray arr1) {
    List<int[]> res = new ArrayList<>();
    for (int i = 0; i < arr1.size(); i++) {
      JsonArray arr2 = arr1.get(i).getAsJsonArray();
      int[] innerarr = intArray(arr2);
      res.add(innerarr);
    }
    return res.stream().toArray(int[][]::new);
  }

  public static void main(String[] args) {
    System.out.println(ORDINALS.keySet());
  }

}
