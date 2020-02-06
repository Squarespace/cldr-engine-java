import * as fs from 'fs';
import { join } from 'path';

import {
  CLDR,
  DateFormatOptions,
  FormatWidthType,
  CalendarDate,
  DateIntervalFormatOptions,
  RelativeTimeFormatOptions,

} from '@phensley/cldr';

import { framework } from './framework';
import { Dimension, product, reduce } from './dimension';

import {
  DATES,
  LOCALES,
  ZONES
} from './data';

type RelFunc = <T>(c: CLDR, start: CalendarDate, end: CalendarDate, opts: T) => string;

const DIM_DOW = new Dimension<RelativeTimeFormatOptions>('dayOfWeek', [false, true]);
const DIM_FIELD = new Dimension<RelativeTimeFormatOptions>('field', [
  undefined,
  'year', 'month', 'week', 'day', 'hour', 'minute', 'second', 'millis'
]);

const buildRelativeTimeFormat = <T>(name: string, method: string,
  dims: Dimension<T>[], meth: RelFunc) => {

  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  const items = dims.map(e => e.build());
  const options = reduce(product(items));
  const cldrs = LOCALES.map(id => framework.get(id));

  let r = JSON.stringify({
    method,
    locales: LOCALES,
    dates: DATES,
    zones: ZONES,
    options
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  for (let i = 0; i < cldrs.length; i++) {
    const cldr = cldrs[i];
    for (let j = 0; j < DATES.length; j++) {
      for (let k = 0; k < DATES.length; k++) {
        for (let m = 0; m < ZONES.length; m++) {
          const zoneId = ZONES[m];
          const start = cldr.Calendars.toGregorianDate({ date: DATES[j], zoneId });
          const end = cldr.Calendars.toGregorianDate({ date: DATES[k], zoneId });
          const res: any[] = [];
          for (let n = 0; n < options.length; n++) {
            const o = options[n];
            const s = meth(cldr, start, end, o);
            res.push(s);
          }
          r = JSON.stringify({
            i,
            j,
            k,
            m,
            results: res
          });
          fs.writeSync(fd, r);
          fs.writeSync(fd, '\n');
        }
      }
    }
  }

  fs.closeSync(fd);
};

export const relativeSuite = (root: string) => {
  let dims: Dimension<RelativeTimeFormatOptions>[];

  dims = [DIM_DOW, DIM_FIELD];
  const f = (c: CLDR, start: CalendarDate, end: CalendarDate, opts: RelativeTimeFormatOptions) =>
    c.Calendars.formatRelativeTime(start, end, opts);
  buildRelativeTimeFormat(join(root, 'relativetime-format.txt'), 'formatRelativeTime', dims, f);
};
