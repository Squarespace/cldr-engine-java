import * as fs from 'fs';
import { join } from 'path';

import { TimePeriod, TimePeriodField } from '@phensley/cldr';
import { framework } from './framework';
import { Dimension, product, reduce } from './dimension';

import {
  DATES,
  LOCALES,
  ZONES,
} from './data';

const FIELDS: TimePeriodField[] = [
  'year', 'month', 'week', 'day',
  'hour', 'minute', 'second', 'millis'
];

const VALUES: (undefined | number)[] = [
  undefined, 0.1, 0.7, 1, 1.3, 1.5, 2, 2.2, 3,
  5, 7, 9, 10, 12, 13, 25, 29, 30, 31, 45, 89, 90, 91, 100
];

const BASES: number[] = [
  ...DATES,
  1581527437123
];

const buildDates = (name: string, dims: Dimension<TimePeriod>[]) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  const items = dims.map(e => e.build());
  const properties = dims.map(d => d.property);
  const options = reduce(product(items));

  let r = JSON.stringify({
    dates: BASES,
    properties
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  const cldr = framework.get('en');
  const zoneId = 'UTC';
  for (const o of options) {
    const results: any[] = [];
    for (const base of BASES) {
      const start = cldr.Calendars.toGregorianDate({ date: base, zoneId });
      const e1 = start.add(o as TimePeriod);
      const e2 = start.subtract(o as TimePeriod);
      const d1 = start.difference(e1);
      const d2 = start.difference(e2);
      results.push(e1.unixEpoch());
      results.push(e2.unixEpoch());
      results.push(d1);
      results.push(d2);
      for (const field of FIELDS) {
        const d3 = start.difference(e1, [field]);
        results.push(d3);
      }
    }
    r = JSON.stringify({
      options: properties.map((k: any) => o[k]),
      results,
    });
    fs.writeSync(fd, r);
    fs.writeSync(fd, '\n');
  }
  fs.closeSync(fd);
};

export const dateMathSuite = (root: string) => {
  const datedims: Dimension<TimePeriod>[] = [
    new Dimension<TimePeriod>('year', VALUES),
    new Dimension<TimePeriod>('month', VALUES),
    new Dimension<TimePeriod>('week', VALUES),
    new Dimension<TimePeriod>('day', VALUES),
  ];

  buildDates(join(root, 'datemath-dates.txt'), datedims);

  const timedims: Dimension<TimePeriod>[] = [
    new Dimension<TimePeriod>('hour', VALUES),
    new Dimension<TimePeriod>('minute', VALUES),
    new Dimension<TimePeriod>('second', VALUES),
    new Dimension<TimePeriod>('millis', VALUES)
  ];

  buildDates(join(root, 'datemath-times.txt'), timedims);
};
