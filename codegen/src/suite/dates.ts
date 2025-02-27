import * as fs from 'fs';
import { join } from 'path';

import { TimePeriod, TimePeriodField } from '@phensley/cldr';
import { framework } from './framework';
import { Dimension, product, reduce } from './dimension';

import { DATES } from './data';
import { timed } from "../utils";

const FIELDS: TimePeriodField[] = [
  'year',
  'month',
  'week',
  'day',
  'hour',
  'minute',
  'second',
  'millis',
];

const VALUES: (undefined | number)[] = [
  undefined,
  0.1,
  0.3,
  0.5,
  1,
  1.5,
  2,
  2.5,
  3,
  5,
  8,
  13,
  21,
  34,
  55,
  89,
  144,
];

const BASES: number[] = [...DATES, 1581527437123];

const buildDates = (name: string, dims: Dimension<Partial<TimePeriod>>[]) => {
  const fd = fs.openSync(name, 'w');

  const items = dims.map((e) => e.build());
  const properties = dims.map((d) => d.property);
  const options = reduce(product(items));

  let r = JSON.stringify({
    dates: BASES,
    properties,
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  const cldr = framework.get('en');
  const zoneId = 'UTC';
  for (const o of options) {
    const results: any[] = [];
    for (const base of BASES) {
      const start = cldr.Calendars.toGregorianDate({ date: base, zoneId });
      const e1 = start.add(o as Partial<TimePeriod>);
      const e2 = start.subtract(o as Partial<TimePeriod>);
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
  const datedims: Dimension<Partial<TimePeriod>>[] = [
    new Dimension<Partial<TimePeriod>>('year', VALUES),
    new Dimension<Partial<TimePeriod>>('month', VALUES),
    new Dimension<Partial<TimePeriod>>('week', VALUES),
    new Dimension<Partial<TimePeriod>>('day', VALUES),
  ];

  let name = 'datemath-dates.txt';
  timed(name, () => buildDates(join(root, name), datedims));

  const timedims: Dimension<Partial<TimePeriod>>[] = [
    new Dimension<Partial<TimePeriod>>('hour', VALUES),
    new Dimension<Partial<TimePeriod>>('minute', VALUES),
    new Dimension<Partial<TimePeriod>>('second', VALUES),
    new Dimension<Partial<TimePeriod>>('millis', VALUES),
  ];

  name = 'datemath-times.txt';
  timed(name, () => buildDates(join(root, name), timedims));
};

dateMathSuite(process.argv[2]);
