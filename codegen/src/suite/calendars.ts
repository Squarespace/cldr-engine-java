import * as fs from 'fs';
import { join } from 'path';

import {
  CLDR,
  DateFormatOptions,
  FormatWidthType,
  ContextType,
  CalendarDate,

} from '@phensley/cldr';

import { framework } from './framework';
import { Dimension, product, reduce } from './dimension';

// const LOCALES = ['en', 'es-419', 'de', 'ja', 'pt-PT', 'zh'];
const LOCALES = ['en'];

const DATES: number[] = [
  // Oct 9 1582 12:34:56 PM GMT - middle of Gregorian switchover
  -12219765904000,
  // Thursday, January 1, 1970 12:00:00 AM GMT
  0,
  // Monday, January 27, 2020 12:34:56 PM GMT
  1580128496000

];

const ZONES: string[] = [
  'America/Catamarca',
  'America/New_York',
  'Europe/Rome',
  'Asia/Tokyo',
];

const SKELETONS: string[] = ['yMMMd', 'hmsv', 'EEEyMMMd', 'GyMd', 'Bhhmm', 'EEEMMMMd'];
const FORMAT_WIDTHS: (FormatWidthType | undefined)[] = [
  undefined, 'full', 'long', 'medium', 'short'];

const DIM_CALENDAR = new Dimension<DateFormatOptions>('ca', [undefined, 'buddhist', 'japanese', 'persian', 'iso8601']);
const DIM_DATETIME = new Dimension<DateFormatOptions>('datetime', FORMAT_WIDTHS);
const DIM_DATE = new Dimension<DateFormatOptions>('date', FORMAT_WIDTHS);
const DIM_TIME = new Dimension<DateFormatOptions>('time', FORMAT_WIDTHS);
const DIM_SKEL = new Dimension<DateFormatOptions>('skeleton', SKELETONS);
const DIM_CONTEXT = new Dimension<DateFormatOptions>('context', ['begin-sentence']);

type DateFunc = <T>(cldr: CLDR, date: CalendarDate, o: T) => string;

const buildDateFormat = <T>(name: string, dims: Dimension<T>[], meth: DateFunc) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');
  const items = dims.map(e => e.build());
  const properties = dims.map(d => d.property);
  const options = reduce(product(items));
  const cldrs = LOCALES.map(id => framework.get(id));

  let r = JSON.stringify({
    method: 'formatDate',
    locales: LOCALES,
    dates: DATES,
    zones: ZONES,
    properties
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  for (const o of options) {
    const results: any[] = [];
    for (const cldr of cldrs) {
      const res: string[] = [];
      for (let i = 0; i < DATES.length; i++) {
        for (let j = 0; j < ZONES.length; j++) {
          const d = cldr.Calendars.toGregorianDate({ date: DATES[i], zoneId: ZONES[j] });
          const s = meth(cldr, d, o);
          res.push(s);
        }
      }
      results.push(res);
    }
    r = JSON.stringify({
      options: properties.map((k: any) => o[k]),
      results
    });
    fs.writeSync(fd, r);
    fs.writeSync(fd, '\n');
  }
  fs.closeSync(fd);
};

export const dateSuite = (root: string) => {
  let dims: Dimension<DateFormatOptions>[];

  const f = (c: CLDR, date: CalendarDate, opts: DateFormatOptions) =>
    c.Calendars.formatDate(date, opts);

  dims = [
    DIM_CALENDAR, DIM_DATETIME, DIM_DATE, DIM_TIME, DIM_SKEL, DIM_CONTEXT
  ];
  buildDateFormat(join(root, 'dateformat.txt'), dims, f);
};
