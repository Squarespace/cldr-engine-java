import * as fs from 'fs';
import { join } from 'path';

import {
  Decimal,
  DecimalConstants,
  MathContext,
  RoundingModeType
} from '@phensley/cldr';

const { PI } = DecimalConstants;

const RAW_NUMBERS: string = `
  infinity nan 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
  0.000 0.0000000000001
  1e-10 1e-30
  9e-10 9e-30
  1e10 1e30
  9e10 9e30
  333 3333333333
  666 6666666666
  999 9999999999

  ${PI.toString().substring(0, 20)}

  # bell primes
  2 3 877 27644437

  # misc from test suite
  888888888888888888888888888888888888888 33333333000000420
`;

const NUMBERS: Decimal[] = [];

RAW_NUMBERS.split(/\n/).map(s => s.trim())
  .filter(s => s[0] !== '#')
  .map(s => s.split(/\s+/))
  .forEach(
    r => r.filter(s => s.length).forEach(s => NUMBERS.push(new Decimal(s))));

const ROUNDING: (RoundingModeType | undefined)[] = [
  undefined,
  'ceiling',
  'up',
  'down',
  'floor',
  'half-down',
  'half-up',
  'half-even'
];

const CONTEXTS: MathContext[] = [];
const PRECISION: number[] = [0, 1, 2, 3, 4, 5, 8, 12, 15, 40];
const SCALE: number[] = [-4, -2, -1, 0, 1, 2, 3, 5];

for (const round of ROUNDING) {
  CONTEXTS.push({ round });
  for (const precision of PRECISION) {
    CONTEXTS.push({ precision, round });
  }
  for (const scale of SCALE) {
    CONTEXTS.push({ scale, round });
  }
}

const buildMath = (name: string) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  let add: Decimal;
  let sub: Decimal;
  let mul: Decimal;
  let div: Decimal;
  let cmp: number;
  let dec: Decimal;
  let inc: Decimal;
  let dmq: Decimal;
  let dmr: Decimal;
  let mod: Decimal;
  let sig: number;
  let int: boolean;
  let shl: Decimal;
  let shr: Decimal;

  const numbers: Decimal[] = NUMBERS.concat(NUMBERS.map(n => n.negate()));
  let r = JSON.stringify({
    method: 'math',
    numbers: numbers.map(n => n.properties()),
    contexts: CONTEXTS
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  for (let i = 0; i < numbers.length; i++) {
    for (let j = 0; j < numbers.length; j++) {
      for (let k = 0; k < CONTEXTS.length; k++) {
        const n = numbers[i];
        const m = numbers[j];
        const c = CONTEXTS[k];

        add = n.add(m);
        sub = n.subtract(m);
        mul = n.multiply(m, c);
        div = n.divide(m, c);
        cmp = n.compare(m);
        dec = n.decrement();
        inc = n.increment();
        [dmq, dmr] = n.divmod(m);
        mod = n.mod(m);
        sig = n.signum();
        int = n.isInteger();
        shl = n.shiftleft(j);
        shr = n.shiftright(j, c.round);

        const res: any[] = [
          i,
          j,
          k,
          add.toString(),
          sub.toString(),
          mul.toString(),
          div.toString(),
          cmp,
          dec.toString(),
          inc.toString(),
          dmq.toString(),
          dmr.toString(),
          mod.toString(),
          sig,
          int,
          shl.toString(),
          shr.toString()
        ];
        fs.writeSync(fd, JSON.stringify(res));
        fs.writeSync(fd, '\n');
      }
    }
  }
  fs.closeSync(fd);
};

export const mathSuite = (root: string) => {
  // TODO: shifts, compares, etc
  buildMath(join(root, 'math.txt'));
};
