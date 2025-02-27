import * as fs from 'fs';
import { join } from 'path';
import { Decimal } from '@phensley/cldr';
import { RNG } from './rng';
import { timed } from "../utils";

const buildRandomMath = (name: string) => {
  const rnd = new RNG('cldr-engine');
  const fd = fs.openSync(name, 'w');

  const count = 100;
  const factors: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map((f) =>
    Math.pow(10, f),
  );
  const numbers: string[] = [];
  for (let i = 0; i < count; i++) {
    const n = rnd.rand();
    for (const factor of factors) {
      numbers.push(`${factor * n}`);
    }
  }

  let r = JSON.stringify({
    numbers,
  });

  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  const decimals = numbers.map((n) => new Decimal(n));
  for (let i = 0; i < decimals.length; i++) {
    const res: string[] = [];
    for (let j = 0; j < decimals.length; j++) {
      const n = decimals[i];
      const m = decimals[j];

      const add = n.add(m);
      const sub = n.subtract(m);
      const mul = n.multiply(m);
      const div = n.divide(m);

      res.push(add.toString());
      res.push(sub.toString());
      res.push(mul.toString());
      res.push(div.toString());
    }
    fs.writeSync(
      fd,
      JSON.stringify({
        i,
        results: res,
      }),
    );
    fs.writeSync(fd, '\n');
  }
  fs.closeSync(fd);
};

const mathRandomSuite = (root: string) => {
  let name = 'math-random.txt';
  timed(name, () => buildRandomMath(join(root, name)));
};

mathRandomSuite(process.argv[2]);
