import * as fs from 'fs';
import { join } from 'path';

import {
  CLDR,
  DateFormatOptions,
  FormatWidthType,
  CalendarDate,
  DateIntervalFormatOptions,
} from '@phensley/cldr';

import { framework } from './framework';
import { Dimension, product, reduce } from './dimension';

import { DATES, LOCALES, ZONES } from './data';

const SKELETONS: (string | undefined)[] = [
  undefined,
  'yMMMd',
  'hmsv',
  'EEEyMMMd',
  'GyMd',
  'Bhhmm',
  'EEEMMMMd',
];
const FORMAT_WIDTHS: (FormatWidthType | undefined)[] = [
  undefined,
  'full',
  'long',
  'medium',
  'short',
];
const DATE_PATTERN_FIELDS: string[] = [
  'G',
  'y',
  'Y',
  'u',
  'U',
  'r',
  'Q',
  'q',
  'M',
  'L',
  'l',
  'w',
  'W',
  'd',
  'D',
  'F',
  'g',
  'E',
  'e',
  'c',
  'a',
  'b',
  'B',
  'h',
  'H',
  'K',
  'k',
  'j',
  'J',
  'C',
  'm',
  's',
  'S',
  'A',
  'z',
  'Z',
  'O',
  'v',
  'V',
  'X',
  'x',
];
const DATE_PATTERN_WIDTHS: number[] = [1, 2, 3, 4, 5, 6];

const DIM_CALENDAR = new Dimension<DateFormatOptions>('ca', [
  undefined,
  'buddhist',
  'japanese',
  'persian',
  'iso8601',
]);
const DIM_DATETIME = new Dimension<DateFormatOptions>(
  'datetime',
  FORMAT_WIDTHS,
);
const DIM_DATE = new Dimension<DateFormatOptions>('date', FORMAT_WIDTHS);
const DIM_TIME = new Dimension<DateFormatOptions>('time', FORMAT_WIDTHS);
const DIM_SKEL = new Dimension<DateFormatOptions>('skeleton', SKELETONS);
const DIM_CONTEXT = new Dimension<DateFormatOptions>('context', [
  'begin-sentence',
  'middle-of-text',
]);

const INT_SKELETON = new Dimension<DateIntervalFormatOptions>('skeleton', [
  undefined,
  'yMMd',
  'EEEyMMMMd',
  'Bh',
  'hmsv',
  'hma',
  'EEEyMMMdhms',
]);
const INT_CONTEXT = new Dimension<DateIntervalFormatOptions>('context', [
  'begin-sentence',
  'middle-of-text',
]);

type DateFunc<R> = <T>(cldr: CLDR, date: CalendarDate, o: T) => R;
type DateIntFunc<R> = <T>(
  cldr: CLDR,
  start: CalendarDate,
  end: CalendarDate,
  o: T,
) => R;

const buildDateFormat = <T, R>(
  name: string,
  method: string,
  dims: Dimension<T>[],
  meth: DateFunc<R>,
) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');
  const items = dims.map((e) => e.build());
  const properties = dims.map((d) => d.property);
  const options = reduce(product(items));
  const cldrs = LOCALES.map((id) => framework.get(id));

  let r = JSON.stringify({
    method,
    locales: LOCALES,
    dates: DATES,
    zones: ZONES,
    properties,
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  for (const o of options) {
    const results: any[] = [];
    for (const cldr of cldrs) {
      const res: any[] = [];
      for (let i = 0; i < DATES.length; i++) {
        for (let j = 0; j < ZONES.length; j++) {
          const d = cldr.Calendars.toGregorianDate({
            date: DATES[i],
            zoneId: ZONES[j],
          });
          const s = meth(cldr, d, o);
          res.push(s);
        }
      }
      results.push(res);
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

const buildDateIntervalFormat = <T, R>(
  name: string,
  method: string,
  dims: Dimension<T>[],
  meth: DateIntFunc<R>,
) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');
  const items = dims.map((e) => e.build());
  const options = reduce(product(items));
  const cldrs = LOCALES.map((id) => framework.get(id));

  let r = JSON.stringify({
    method,
    locales: LOCALES,
    dates: DATES,
    zones: ZONES,
    options,
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  for (let i = 0; i < cldrs.length; i++) {
    const cldr = cldrs[i];
    for (let j = 0; j < DATES.length; j++) {
      for (let k = 0; k < DATES.length; k++) {
        for (let m = 0; m < ZONES.length; m++) {
          const zoneId = ZONES[m];
          const start = cldr.Calendars.toGregorianDate({
            date: DATES[j],
            zoneId,
          });
          const end = cldr.Calendars.toGregorianDate({
            date: DATES[k],
            zoneId,
          });
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
            results: res,
          });

          fs.writeSync(fd, r);
          fs.writeSync(fd, '\n');
        }
      }
    }
  }
  fs.closeSync(fd);
};

const buildRawFormat = (name: string) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  const cldrs = LOCALES.map((id) => framework.get(id));

  let r = JSON.stringify({
    method: 'formatDateRaw',
    locales: LOCALES,
    dates: DATES,
    zones: ZONES,
    fields: DATE_PATTERN_FIELDS,
    widths: DATE_PATTERN_WIDTHS,
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  for (let i = 0; i < cldrs.length; i++) {
    const cldr = cldrs[i];
    for (let j = 0; j < DATES.length; j++) {
      const date = DATES[j];
      for (let k = 0; k < ZONES.length; k++) {
        const zoneId = ZONES[k];
        let results: any = [];
        for (let m = 0; m < DATE_PATTERN_FIELDS.length; m++) {
          const field = DATE_PATTERN_FIELDS[m];
          for (let n = 0; n < DATE_PATTERN_WIDTHS.length; n++) {
            const width = DATE_PATTERN_WIDTHS[n];
            const pattern = field.repeat(width);
            const d = cldr.Calendars.toGregorianDate({ date, zoneId });
            const s = cldr.Calendars.formatDateRaw(d, { pattern });
            results.push(s);
          }
        }
        r = JSON.stringify({
          i,
          j,
          k,
          results,
        });
        fs.writeSync(fd, r);
        fs.writeSync(fd, '\n');
      }
    }
  }
  fs.closeSync(fd);
};

export const dateSuite = (root: string) => {
  let datedims: Dimension<DateFormatOptions>[];

  const f1 = (c: CLDR, date: CalendarDate, opts: DateFormatOptions): string =>
    c.Calendars.formatDate(date, opts);

  datedims = [
    DIM_CALENDAR,
    DIM_DATETIME,
    DIM_DATE,
    DIM_TIME,
    DIM_SKEL,
    DIM_CONTEXT,
  ];
  buildDateFormat(join(root, 'dateformat.txt'), 'formatDate', datedims, f1);

  const f2 = (c: CLDR, date: CalendarDate, opts: DateFormatOptions) =>
    c.Calendars.formatDateToParts(date, opts);
  buildDateFormat(
    join(root, 'dateformat-parts.txt'),
    'formatDateToParts',
    datedims,
    f2,
  );

  let intdims: Dimension<DateIntervalFormatOptions>[];

  const f3 = (
    c: CLDR,
    start: CalendarDate,
    end: CalendarDate,
    opts: DateIntervalFormatOptions,
  ) => c.Calendars.formatDateInterval(start, end, opts);

  intdims = [INT_SKELETON, INT_CONTEXT];
  buildDateIntervalFormat(
    join(root, 'dateinterval.txt'),
    'formatDateInterval',
    intdims,
    f3,
  );
  buildRawFormat(join(root, 'dateformat-raw.txt'));
};

dateSuite(process.argv[2]);
