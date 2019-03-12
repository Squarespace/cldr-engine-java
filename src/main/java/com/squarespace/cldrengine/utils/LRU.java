package com.squarespace.cldrengine.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Evicts the least-recently-used key when capacity is exceeded.
 *
 * Ported from @phensley/cldr-utils src/lru.ts
 */
public class LRU<K, V> {

  private static final int DEFAULT_CAPACITY = 100;

  private Map<K, Node<K, V>> storage = new HashMap<>();
  private final Node<K, V> root;
  private final int capacity;

  public LRU() {
    this(DEFAULT_CAPACITY);
  }

  public LRU(int capacity) {
    this.root = new Node<>();
    this.root.next = root;
    this.root.prev = root;
    this.capacity = capacity;
  }

  public int size() {
    return this.storage.size();
  }

  public V get(K key) {
    Node<K, V> n = this.storage.get(key);
    if (n == null) {
      return null;
    }
    this.moveFront(n);
    return n.val;
  }

  public void set(K key, V val) {
    if (this.capacity == 0) {
      return;
    }

    // Key already exists, so replace its value and bump it
    // to the front. Size does not change.
    Node<K, V> n = this.storage.get(key);
    if (n != null) {
      n.val = val;
      this.moveFront(n);
      return;
    }

    // The lru is full, so reuse the oldest node to keep the
    // total node allocation stable.
    if (this.storage.size() == this.capacity) {
      Node<K, V> old = this.root.prev;
      if (old != null) {
        this.storage.remove(old.key);
        this.storage.put(key, old);
        old.key = key;
        old.val = val;
        this.moveFront(old);
      }
      return;
    }

    // The lru is not full, so allocate a new node.
    n = new Node<>();
    n.key = key;
    n.val = val;
    this.storage.put(key, n);
    this.insert(n, this.root);
  }

  public String toString() {
    StringBuilder buf = new StringBuilder();
    Node<K, V> n = this.root.next;
    boolean nz = false;
    while (n != null && n != this.root) {
      if (nz) {
        buf.append(' ');
      }
      buf.append(n.key).append('=').append(n.val);
      n = n.next;
      nz = true;
    }
    return buf.toString();
  }

  private void moveFront(Node<K, V> n) {
    this.insert(this.remove(n), this.root);
  }

  private Node<K, V> insert(Node<K, V> e, Node<K, V> at) {
    Node<K, V> n = at.next;
    at.next = e;
    e.prev = at;
    e.next = n;
    if (n != null) {
      n.prev = e;
    }
    return e;
  }

  private Node<K, V> remove(Node<K, V> n) {
    if (n.prev != null) {
      n.prev.next = n;
    }
    if (n.next != null) {
      n.next.prev = n.prev;
    }
    n.prev = null;
    n.next = null;
    return n;
  }

  private static class Node<K, V> {
    K key;
    V val;
    Node<K, V> next;
    Node<K, V> prev;
  }
}
