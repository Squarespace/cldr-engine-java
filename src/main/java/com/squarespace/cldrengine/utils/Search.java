package com.squarespace.cldrengine.utils;

public class Search {


  /**
   * Binary search an array of numbers. The parameter lessThan
   * determines which index is returned:
   *
   *   true  - index of element less-than-or-equal to our search
   *   false -               .. greater-than-or-equal ..
   */
  public static int binarySearch(long[] elems, boolean lessThan, long n) {
    int lo = 0;
    int hi = elems.length - 1;
    int mid = 0;
    while (lo <= hi) {
      mid = lo + ((hi - lo) >> 1);
      long e = elems[mid];
      switch (e > n ? 1 : e < n ? -1 : 0) {
        case -1:
          lo = mid + 1;
          break;
        case 1:
          hi = mid - 1;
          break;
        default:
          // found
          return mid;
      }
    }

    // when not found, return the index of the element less than
    // or greater than the desired value
    return lessThan ? lo - 1 : lo;
  }

}
