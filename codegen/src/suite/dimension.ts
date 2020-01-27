/**
 * Defines a dimension of an object's properties and its legal values.
 */
export class Dimension<T> {
  constructor(readonly property: keyof T, readonly values: T[keyof T][]) { }

  build(): [keyof T, T[keyof T]][] {
    const r: [keyof T, T[keyof T]][] = [];
    for (const v of this.values) {
      r.push([this.property, v]);
    }
    return r;
  }
}

/**
 * Build a cartesian product of the input elements.
 * For example:
 *
 *  INPUT:   [ ['a', 'b'], [1, 2] ]
 * OUTPUT:   [ ['a', 1], ['a', 2], ['b', 1], ['b', 2] ]
 */
class CartesianProduct<T> {

  readonly lengths: number[];

  constructor(readonly elems: T[][]) {
    this.lengths = elems.map(e => e.length);
  }

  build(): T[][] {
    const indices: number[] = this.elems.map(e => 0);
    const len = indices.length;
    const res: T[][] = [];
    outer:
    for (; ;) {
      const r: T[] = [];

      for (let i = 0; i < len; i++) {
        if (this.lengths[i] == 0) {
          continue;
        }
        const elem = this.elems[i][indices[i]];
        r.push(elem);
      }

      res.push(r);

      // increment indices
      for (let i = len - 1; i >= 0; i--) {
        if (this.lengths[i] == 0) {
          continue;
        }
        if (indices[i] == this.lengths[i] - 1) {
          indices[i] = 0;
          if (i == 0) {
            break outer;
          }
        } else {
          indices[i]++;
          break;
        }
      }
    }
    return res;
  }
}

export const product = <T>(elems: T[][]) =>
  new CartesianProduct(elems).build();

// Reduce the array of items [key, val] into an object { [key]: val, ... }
export const reduce = <T>(items: T[][]) => {
  const r: T[] = [];
  for (const p of items) {
    const o = p.reduce((p, c: any) => {
      const [key, val] = c;
      p[key] = val;
      return p;
    }, {} as any);
    r.push(o);
  }
  return r;
};
