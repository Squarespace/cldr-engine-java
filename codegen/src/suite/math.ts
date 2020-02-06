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

const ROUNDING: RoundingModeType[] = [
  'ceiling',
  'up',
  'down',
  'floor',
  'half-down',
  'half-up',
  'half-even'
];

const CONTEXTS: MathContext[] = [];

for (const round of ROUNDING) {
  for (let i = 0; i < 40; i += 3) {
    CONTEXTS.push({ precision: i, round });
  }
  for (let i = -10; i < 12; i += 2) {
    CONTEXTS.push({ scale: i, round });
  }
}

const buildMath = (name: string) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  let add: Decimal;
  let sub: Decimal;
  let mul: Decimal;
  let div: Decimal;

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

        const res: any[] = [
          i,
          j,
          k,
          add.toString(),
          sub.toString(),
          mul.toString(),
          div.toString()
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