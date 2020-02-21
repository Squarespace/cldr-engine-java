import * as fs from 'fs';
import { join } from 'path';

import {
  CLDR,
  CurrencyType,
  Decimal,
  DecimalFormatOptions,
  DecimalFormatStyleType,
  CurrencyFormatStyleType,
  CurrencyFormatOptions,
  DecimalConstants,
  DecimalAdjustOptions,
  NumberFormatOptions,
  NumberFormatStyleType,
  NumberSystemType,
  RoundingModeType,
} from '@phensley/cldr';

import { framework } from "./framework";
import { Dimension, product, reduce } from './dimension';

const NUMBERS: string[] = [
  '0',
  '1',
  '1.0',
  '1.111',
  '2',
  '2.15',
  DecimalConstants.PI.setScale(30).toString(),
  '3.9999999999',
  '15.59',
  '12345.67890',
  '99900.5',
  '999900',
  '654321.456789',
  '1000000000000000',
  String(Number.MAX_SAFE_INTEGER)
];

const LOCALES = ['en', 'es-419', 'de', 'ja', 'pt-PT', 'zh'];

const ROUND: (RoundingModeType | undefined)[] = [
  undefined, 'ceiling', 'floor', 'up', 'down', 'half-down', 'half-up', 'half-even'];

const MIN_MAX: (number | undefined)[] = [undefined, 0, 1, 3, 5];
const NUM_SYSTEMS: (NumberSystemType | undefined)[] = [
  undefined,
  'latn',
  'beng',
  'guru',
  'sund' // sund has digits but no system-specific formats, so will fall back to latn
];
const NUM_STYLE: (NumberFormatStyleType | undefined)[] = [
  undefined, 'decimal', 'scientific'
];
const NUM_COMPACT_STYLE: (NumberFormatStyleType | undefined)[] = [
  'short', 'long'
];
const DECIMAL_STYLES: (DecimalFormatStyleType | undefined)[] = [
  undefined, ...NUM_STYLE, 'percent', 'percent-scaled', 'permille', 'permille-scaled'
];
const CURRENCY_STYLES: (CurrencyFormatStyleType | undefined)[] = [
  undefined, 'symbol', 'accounting', 'code', 'name'
];

const CURRENCY_COMPACT_STYLES: CurrencyFormatStyleType[] = ['short'];

const CURRENCIES: CurrencyType[] = [
  'USD', 'CAD', 'AUD', 'JPY', 'EUR', 'PTE'
];

const DIVISORS: (number | undefined)[] = [undefined, 100, 1000, 10000];
const BOOLEANS: (boolean | undefined)[] = [undefined, true, false];

const DECADJ_ROUND = new Dimension<DecimalAdjustOptions>('round', ROUND);
const DECADJ_MINFRAC = new Dimension<DecimalAdjustOptions>('minimumFractionDigits', MIN_MAX);
const DECADJ_MAXFRAC = new Dimension<DecimalAdjustOptions>('maximumFractionDigits', MIN_MAX);
const DECADJ_MINSIG = new Dimension<DecimalAdjustOptions>('minimumSignificantDigits', MIN_MAX);
const DECADJ_MAXSIG = new Dimension<DecimalAdjustOptions>('maximumSignificantDigits', MIN_MAX);

const NUMFMT_GROUP = new Dimension<NumberFormatOptions>('group', BOOLEANS);
const NUMFMT_NUM_SYSTEMS = new Dimension<NumberFormatOptions>('nu', NUM_SYSTEMS);

const DECFMT_DIVISOR = new Dimension<DecimalFormatOptions>('divisor', DIVISORS);
const DECFMT_NEGZERO = new Dimension<DecimalFormatOptions>('negativeZero', BOOLEANS);
const DECFMT_STYLE = new Dimension<DecimalFormatOptions>('style', DECIMAL_STYLES);
const DECFMT_COMPACT_STYLE = new Dimension<DecimalFormatOptions>('style', NUM_COMPACT_STYLE);
const DECFMT_MININT = new Dimension<DecimalFormatOptions>('minimumIntegerDigits', MIN_MAX);

const CURFMT_STYLE = new Dimension<CurrencyFormatOptions>('style', CURRENCY_STYLES);
const CURFMT_STYLE_COMPACT = new Dimension<CurrencyFormatOptions>('style', CURRENCY_COMPACT_STYLES);
const CURFMT_DIVISOR = new Dimension<CurrencyFormatOptions>('divisor', DIVISORS);
const CURFMT_CASH = new Dimension<CurrencyFormatOptions>('cash', BOOLEANS);
const CURFMT_SYMBOLWIDTH = new Dimension<CurrencyFormatOptions>('symbolWidth', [
  undefined, 'default', 'narrow']);

type DecimalFunc = <T>(cldr: CLDR, n: Decimal, o: T) => string;
type CurrencyFunc = <T>(cldr: CLDR, n: Decimal, code: CurrencyType, opts: T) => string;

const buildDecimal = <T>(name: string, dims: Dimension<T>[], meth: DecimalFunc) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');
  const items = dims.map(e => e.build());
  const numbers = NUMBERS.concat(NUMBERS.map(n => `-${n}`));
  const properties = dims.map(d => d.property);
  const options = reduce(product(items));
  const cldrs = LOCALES.map(id => framework.get(id));

  let r = JSON.stringify({
    method: 'formatDecimal',
    locales: LOCALES,
    numbers,
    properties
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  const decimals = numbers.map(n => new Decimal(n));
  for (const o of options) {
    const results: any[] = [];
    for (const cldr of cldrs) {
      const res: string[] = [];
      for (let i = 0; i < decimals.length; i++) {
        const n = decimals[i];
        const s = meth(cldr, n, o);
        res.push(s);
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

const buildCurrency = <T>(name: string, dims: Dimension<T>[], currencies: CurrencyType[], meth: CurrencyFunc) => {
  console.log(`writing ${name}`);
  const fd = fs.openSync(name, 'w');
  const items = dims.map(e => e.build());
  const numbers = NUMBERS.concat(NUMBERS.map(n => `-${n}`));
  const properties = dims.map(d => d.property);
  const options = reduce(product(items));
  const cldrs = LOCALES.map(id => framework.get(id));

  let r = JSON.stringify({
    method: 'formatCurrency',
    locales: LOCALES,
    currencies,
    numbers,
    properties
  });
  fs.writeSync(fd, r);
  fs.writeSync(fd, '\n');

  const decimals = numbers.map(n => new Decimal(n));
  for (const o of options) {
    const results: any[] = [];
    for (const cldr of cldrs) {
      const res: string[] = [];
      for (let i = 0; i < decimals.length; i++) {
        for (let j = 0; j < currencies.length; j++) {
          const n = decimals[i];
          const s = meth(cldr, n, currencies[j], o);
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

export const currencySuite = (root: string) => {
  let dims: Dimension<CurrencyFormatOptions>[];
  const f = (c: CLDR, n: Decimal, code: CurrencyType, opts: CurrencyFormatOptions) =>
    c.Numbers.formatCurrency(n, code, opts);

  dims = [CURFMT_STYLE, NUMFMT_NUM_SYSTEMS, NUMFMT_GROUP];
  buildCurrency(join(root, 'currencyformat-numsystems.txt'), dims, CURRENCIES, f);

  dims = [CURFMT_CASH, CURFMT_STYLE, CURFMT_SYMBOLWIDTH, NUMFMT_GROUP];
  buildCurrency(join(root, 'currencyformat.txt'), dims, CURRENCIES, f);

  dims = [CURFMT_CASH, CURFMT_STYLE_COMPACT, CURFMT_DIVISOR, CURFMT_SYMBOLWIDTH, NUMFMT_GROUP];
  buildCurrency(join(root, 'currencyformat-compact.txt'), dims, CURRENCIES, f);
};

export const decimalSuite = (root: string) => {
  let dims: Dimension<DecimalFormatOptions>[];
  const f = (c: CLDR, n: Decimal, o: DecimalFormatOptions) =>
    c.Numbers.formatDecimal(n, o);

  dims = [NUMFMT_NUM_SYSTEMS, DECFMT_STYLE, NUMFMT_GROUP];
  buildDecimal(join(root, 'decimalformat-numsystems.txt'), dims, f);

  dims = [DECFMT_STYLE, DECADJ_ROUND, NUMFMT_GROUP,
    DECFMT_MININT, DECADJ_MINFRAC, DECADJ_MAXFRAC];
  buildDecimal(join(root, 'decimalformat.txt'), dims, f);

  dims = [DECFMT_NEGZERO, DECADJ_ROUND, DECFMT_MININT];
  buildDecimal(join(root, 'decimalformat-negzero.txt'), dims, f);

  dims = [DECADJ_ROUND, DECFMT_MININT, DECADJ_MAXSIG, DECADJ_MINSIG];
  buildDecimal(join(root, 'decimalformat-sig.txt'), dims, f);

  dims = [DECFMT_DIVISOR, DECFMT_COMPACT_STYLE,
    DECADJ_ROUND, DECFMT_MININT, DECADJ_MINFRAC, DECADJ_MAXFRAC];
  buildDecimal(join(root, 'decimalformat-compact.txt'), dims, f);

  dims = [DECADJ_ROUND, DECFMT_COMPACT_STYLE, DECFMT_MININT,
    DECADJ_MAXSIG, DECADJ_MINSIG];
  buildDecimal(join(root, 'decimalformat-compact-sig.txt'), dims, f);
};

currencySuite(process.argv[2]);
decimalSuite(process.argv[2]);
