import * as fs from 'fs';
import { join } from 'path';

import { TimePeriod, TimePeriodField } from '@phensley/cldr';
import { framework } from './framework';
import { Dimension, product, reduce } from './dimension';
import { RNG } from './rng';

import {
  DATES,
} from './data';

const buildDateConvert = (name: string) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  const count = 1000;
  const factors: number[] = [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
    .map(f => Math.pow(10, f));

  const rng = new RNG('cldr-engine');
  const incr: number[] = [0];
  for (let i = 0; i < count; i++) {
    const n = rng.rand();
    for (const factor of factors) {
      incr.push(Math.round(n * factor));
    }
  }

  let r = JSON.stringify({
    dates: DATES,
    incr
  });

  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  const cldr = framework.get('en');
  const zoneId = 'UTC';

  for (let i = 0; i < DATES.length; i++) {
    const date = DATES[i];
    for (let k = 0; k < incr.length; k++) {
      const d = cldr.Calendars.toGregorianDate({ date: date + incr[k], zoneId });
      const buddhist = cldr.Calendars.toBuddhistDate(d);
      const iso8601 = cldr.Calendars.toISO8601Date(d);
      const japanese = cldr.Calendars.toJapaneseDate(d);
      const persian = cldr.Calendars.toPersianDate(d);
      const res = [
        buddhist.toString(),
        iso8601.toString(),
        japanese.toString(),
        persian.toString()
      ];
      fs.writeSync(fd, JSON.stringify({
        i,
        k,
        results: res
      }));
      fs.writeSync(fd, '\n');
    }
  }

  fs.closeSync(fd);
};

export const dateConvertSuite = (root: string) => {
  buildDateConvert(join(root, 'dateconv.txt'));
};

dateConvertSuite(process.argv[2]);
