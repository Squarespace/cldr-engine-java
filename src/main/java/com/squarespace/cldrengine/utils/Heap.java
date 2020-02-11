package com.squarespace.cldrengine.utils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Minimum heap which manages its own lightweight internal array.
 */
public class Heap<T> {

  private final Comparator<T> cmp;
  private T[] items;

  // Pointer to the last element in the array. Also used as
  // the current populated length of the heap.
  private int index;

  public Heap(T[] items, Comparator<T> cmp) {
    this.items = Arrays.copyOf(items, items.length);
    this.index = this.items.length - 1;
    this.cmp = cmp;
    for (int i = parent(items.length - 1); i >= 0; i--) {
      this._down(i);
    }
  }

  @Override
  public String toString() {
    int len = this.index + 1;
    return Arrays.toString(Arrays.copyOf(this.items, len));
  }

  /**
   * Is the heap empty?
   */
  public boolean empty() {
    return this.index == -1;
  }

  /**
   * Push an item and sift up.
   */
  public void push(T item) {
    index++;
    if (index >= this.items.length) {
      grow();
    }
    this.items[index] = item;
    this._up();
  }

  /**
   * Pop the minimum item.
   */
  public T pop() {
    int len = index + 1;
    if (len <= 1) {
      return this._pop();
    }
    T result = this.items[0];
    this.items[0] = this._pop();
    this._down();
    return result;
  }

  private T _pop() {
    if (index == -1) {
      throw new IndexOutOfBoundsException("Attempt to pop from an empty heap");
    }
    int i = this.index;
    this.index--;
    return this.items[i];
  }

  private void grow() {
    int oldsize = this.index + 1;
    int newsize = oldsize + (oldsize >> 1);
    T[] tmp = Arrays.copyOf(this.items, newsize);
    this.items = tmp;
  }

  /**
   * Sift down.
   */
  private void _down() {
    this._down(0);
  }

  /**
   * Sift down.
   */
  private void _down(int i) {
    int len = this.index + 1;
    for (;;) {
      int lx = left(i);
      if (lx >= len) {
        break;
      }
      int rx = right(i);
      int sm = rx < len
          ? (this.cmp.compare(this.items[lx], this.items[rx]) < 0 ? lx : rx)
          : lx;
      if (this.cmp.compare(this.items[sm], this.items[i]) >= 0) {
        break;
      }
      this.swap(sm, i);
      i = sm;
    }
  }

  /**
   * Sift up.
   */
  private void _up() {
    int i = this.index;
    while (i > 0) {
      int px = parent(i);
      if (this.cmp.compare(this.items[i], this.items[px]) >= 0) {
        break;
      }
      this.swap(i, px);
      i = px;
    }
  }

  private void swap(int i, int j) {
    T tmp = this.items[i];
    this.items[i] = this.items[j];
    this.items[j] = tmp;
  }

  private int parent(int i) {
    return (i - 1) >> 1;
  }

  private int left(int i) {
    return (i << 1) + 1;
  }

  private int right(int i) {
    return (i << 1) + 2;
  }
}
