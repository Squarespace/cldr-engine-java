import * as fs from 'fs';
import { join } from 'path';

import {
  RelativeTimeFormatOptions,
  RelativeTimeFieldFormatOptions,
  RelativeTimeFieldType,
} from '@phensley/cldr';

import { framework } from './framework';
import { Dimension, product, reduce } from './dimension';

import { DATES, LOCALES, ZONES } from './data';

const REL_FIELDS: RelativeTimeFieldType[] = [
  'year',
  'quarter',
  'month',
  'week',
  'day',
  'sun',
  'mon',
  'tue',
  'wed',
  'thu',
  'fri',
  'sat',
  'hour',
  'minute',
  'second',
];

const DIM_CONTEXT = new Dimension<RelativeTimeFieldFormatOptions>('context', [
  undefined,
  'begin-sentence',
]);
const DIM_WIDTH = new Dimension<RelativeTimeFieldFormatOptions>('width', [
  undefined,
  'narrow',
  'short',
  'wide',
]);
const DIM_ALWNOW = new Dimension<RelativeTimeFieldFormatOptions>('alwaysNow', [
  undefined,
  false,
  true,
]);
const DIM_NUMONLY = new Dimension<RelativeTimeFieldFormatOptions>(
  'numericOnly',
  [undefined, false, true],
);
const DIM_DOW = new Dimension<RelativeTimeFormatOptions>('dayOfWeek', [
  false,
  true,
]);
const DIM_FIELD = new Dimension<RelativeTimeFormatOptions>('field', [
  undefined,
  'year',
  'month',
  'week',
  'day',
  'hour',
  'minute',
  'second',
  'millis',
]);
const NUMBERS: string[] = [
  '-70',
  '50',
  '20',
  '-14.5',
  '-14',
  '-13',
  '-7',
  '-5',
  '-4',
  '-3',
  '-2',
  '-1',
  '-0.1',
  '0',
  '0.1',
  '1',
  '2',
  '3',
  '4',
  '5',
  '7',
  '13',
  '14',
  '14.5',
  '20',
  '50',
  '70',
];

const buildRelativeTimeFormat = <T>(
  name: string,
  method: string,
  dims: Dimension<RelativeTimeFieldFormatOptions>[],
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
            const s = cldr.Calendars.formatRelativeTime(
              start,
              end,
              o as RelativeTimeFieldFormatOptions,
            );
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

const buildRelativeTimeField = (
  name: string,
  dims: Dimension<RelativeTimeFieldFormatOptions>[],
) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');

  const items = dims.map((e) => e.build());
  const options = reduce(product(items));
  const cldrs = LOCALES.map((id) => framework.get(id));

  let r = JSON.stringify({
    locales: LOCALES,
    numbers: NUMBERS,
    fields: REL_FIELDS,
    options,
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  for (let i = 0; i < cldrs.length; i++) {
    const cldr = cldrs[i];
    for (let j = 0; j < NUMBERS.length; j++) {
      const n = NUMBERS[j];
      for (let k = 0; k < options.length; k++) {
        const res: string[] = [];
        for (let m = 0; m < REL_FIELDS.length; m++) {
          const o = options[k] as RelativeTimeFieldFormatOptions;
          const s = cldr.Calendars.formatRelativeTimeField(n, REL_FIELDS[m], o);
          res.push(s);
        }
        r = JSON.stringify({
          i,
          j,
          k,
          results: res,
        });
        fs.writeSync(fd, r);
        fs.writeSync(fd, '\n');
      }
    }
  }

  fs.closeSync(fd);
};

export const relativeSuite = (root: string) => {
  buildRelativeTimeFormat(
    join(root, 'relativetime-format.txt'),
    'formatRelativeTime',
    [DIM_DOW, DIM_FIELD],
  );

  buildRelativeTimeField(join(root, 'relativetime-field.txt'), [
    DIM_CONTEXT,
    DIM_WIDTH,
    DIM_ALWNOW,
    DIM_NUMONLY,
  ]);
};

relativeSuite(process.argv[2]);
