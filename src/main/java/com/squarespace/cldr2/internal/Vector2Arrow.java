package com.squarespace.cldr2.internal;

public class Vector2Arrow<R, S> {

  private int size;
  private int size2;
  private int offset;
  private KeyIndex<String> index1;
  private KeyIndex<String> index2;

  public Vector2Arrow(int offset, KeyIndex<String> index1, KeyIndex<String> index2) {
    this.size = index1.size * index2.size;
    this.size2 = index2.size;
    this.offset = offset + 1; // skip header
    this.index1 = index1;
    this.index2 = index2;
  }

  boolean exists(PrimitiveBundle bundle) {
    return "E".equals(bundle.get(this.offset - 1));
  }

  public String get(PrimitiveBundle bundle, String key1, String key2) {
    boolean exists = this.exists(bundle);
    if (exists) {
      int i = this.index1.get(key1);
      if (i != -1) {
        int j = this.index2.get(key2);
        if (j != -1) {
          int k = this.offset + (i * this.size2) + j;
          return bundle.get(k);
        }
      }
    }
    return "";
  }

  // TODO: implement mapping

  //mapping(bundle: PrimitiveBundle): { [P in T]: { [Q in S]: string }} {
  //  const offset = this.offset;
  //  /* tslint:disable-next-line */
  //  const res: { [P in T]: { [Q in S]: string } } = Object.create(null);
  //  let exists = bundle.get(offset - 1) === 'E';
  //  if (!exists) {
  //    return res;
  //  }
  //
  //  const size2 = this.size2;
  //  const keys1 = this.index1.keys;
  //  const keys2 = this.index2.keys;
  //  for (let i = 0; i < keys1.length; i++) {
  //    exists = false;
  //    /* tslint:disable-next-line */
  //    const o: { [Q in S]: string } = Object.create(null);
  //    for (let j = 0; j < keys2.length; j++) {
  //      const k = offset + (i * size2) + j;
  //      const s = bundle.get(k);
  //      if (s) {
  //        exists = true;
  //        const key2 = keys2[j];
  //        o[key2] = s;
  //      }
  //    }
  //    if (exists) {
  //      const key1 = keys1[i];
  //      res[key1] = o;
  //    }
  //  }
  //  return res;
  //}

}
